<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { useI18n } from 'vue-i18n'
import { VueDraggable } from 'vue-draggable-plus'
import Feedback from "./Feedback.vue"
import AssociateTech from "./AssociateTech.vue"
import RequirementModal from "./RequirementModal.vue"
import CategoryModal from "./CategoryModal.vue"
import { errorNotification, successNotification } from "../alerts/notifications.js"
import { projectApi } from "../../api/project.js"
import { formQuestionApi } from "../../api/formQuestion.js"

const router = useRouter()
const store = useStore()
const { t } = useI18n()

const props = defineProps({ projectId: { type: String, required: true } })
const currentUser = computed(() => store.getters['auth/currentUser'])

// Estados
const questions = ref([])
const answers = ref([])

// ESTADO DO KANBAN
const backlog = ref([])
const categories = ref([])

// Modais
const showAssociate = ref(false)
const showRequirementModal = ref(false)
const showCategoryModal = ref(false)
const activeRequirement = ref(null)
const activeRequirementIndex = ref(-1)
const activeCategoryId = ref(null) // Para sabermos de qual coluna vem o requisito
const activeFeedback = ref(null)

const loading = ref(true)
const locked = ref(false)
const canFeedback = ref(false)
const canDraft = ref(false)
const error = ref(null)

const canManageCategories = computed(() => !locked.value && currentUser.value?.role !== 'CLIENT')

// --- Gestão de Categorias ---
function handleAddCategory(newCat) {
  categories.value.push({
    id: 'temp-cat-' + Date.now(), // ID temporário para o Drag&Drop não bugar
    name: newCat.name,
    requirements: []
  })
  showCategoryModal.value = false
}

// --- Gestão de Requisitos ---
function openRequirementModal(req = null, index = -1, categoryId = null) {
  activeRequirement.value = req
  activeRequirementIndex.value = index
  activeCategoryId.value = categoryId
  showRequirementModal.value = true
}

function handleSaveRequirement(newReq) {
  newReq.id = 'temp-req-' + Date.now() // ID único temporário
  backlog.value.push(newReq)
  showRequirementModal.value = false
}

function handleUpdateRequirement({ index, data }) {
  if (activeCategoryId.value === null) {
    backlog.value[index] = data
  } else {
    const cat = categories.value.find(c => c.id === activeCategoryId.value)
    if (cat) cat.requirements[index] = data
  }
}

function handleRemoveRequirement(index) {
  if (activeCategoryId.value === null) {
    backlog.value.splice(index, 1)
  } else {
    const cat = categories.value.find(c => c.id === activeCategoryId.value)
    if (cat) cat.requirements.splice(index, 1)
  }
}

function handleRequirementFeedback(reqId) {
  showRequirementModal.value = false
  openFeedback('requirement', reqId)
}

// --- Preparar envio para a API ---
function prepareRequirementsForApi() {
  const flatList = []

  // 1. Guardar o Backlog (soltos)
  backlog.value.forEach((req, index) => {
    flatList.push({
      requirementId: typeof req.id === 'number' ? req.id : null,
      title: req.title,
      description: req.description,
      categoryId: null, // Sem categoria
      position: index // A ordem em que ficou no Drag&Drop
    })
  })

  // 2. Guardar os das Categorias
  categories.value.forEach((cat) => {
    const actualCatId = typeof cat.id === 'number' ? cat.id : null;
    cat.requirements.forEach((req, index) => {
      flatList.push({
        requirementId: typeof req.id === 'number' ? req.id : null,
        title: req.title,
        description: req.description,
        categoryId: actualCatId, // Liga ao ID da categoria
        position: index // A ordem dentro da coluna!
      })
    })
  })

  return flatList
}

function prepareCategoriesForApi() {
  return categories.value.map((cat, index) => ({
    id: typeof cat.id === 'number' ? cat.id : null,
    name: cat.name,
    position: index // A ordem das colunas
  }))
}

function addAnswer() {
  const answersList = []
  for (const question of questions.value) {
    if (!answers.value[question.id] || answers.value[question.id].trim() === "") answers.value[question.id] = ""
    answersList.push({ formQuestionId: question.id, answerText: answers.value[question.id] })
  }
  return answersList
}

function openFeedback(type, itemId) {
  if (!itemId || typeof itemId !== 'number') {
    errorNotification(t('answer_project.err_save_first'))
    return
  }
  activeFeedback.value = { type, id: itemId }
}

async function sendAnswers(draft) {
  try {
    const output = await projectApi.submitClientForm(
        Number(props.projectId),
        addAnswer(),
        prepareRequirementsForApi(), // Array com posições
        draft,
        prepareCategoriesForApi() // Categorias com posições
    )

    successNotification(output.message || t('answer_project.msg_submitted'))
    router.push({ path: '/dashboard/dashboard-main' })
  } catch (err) {
    errorNotification(err.message || t('answer_project.err_submit_failed'))
  }
}

async function fetchProject() {
  try {
    const output = await projectApi.getDetails(props.projectId)
    const role = currentUser.value?.role
    const status = output.status

    switch (status) {
      case 'DRAFT':
      case 'WAITING_FOR_CLIENT':
        locked.value = role !== 'CLIENT'; canDraft.value = role === 'CLIENT'; canFeedback.value = false; break;
      case 'MORE_INFO_NEDDED':
      case 'MORE_INFO_NEEDED':
        locked.value = role !== 'CLIENT'; canDraft.value = role === 'CLIENT'; canFeedback.value = true; break;
      case 'UNDER_TECHNICAL_REVIEW':
        locked.value = role === 'CLIENT'; canDraft.value = false; canFeedback.value = true; break;
      case 'COMPLETED':
        locked.value = true; canDraft.value = false; canFeedback.value = true; break;
      default:
        locked.value = true; canDraft.value = false; canFeedback.value = false; break;
    }

    if (status !== 'WAITING_FOR_CLIENT' && output.answers) {
      const actualQuestions = []
      for (const savedAnswer of output.answers) {
        actualQuestions.push({ answerId: savedAnswer.answerId, id: savedAnswer.formQuestionId, question: savedAnswer.questionText })
        answers.value[savedAnswer.formQuestionId] = savedAnswer.answerText
      }
      questions.value = actualQuestions
    }

    // MAPEAR CATEGORIAS DO BACKEND
    if (output.categories) {
      categories.value = output.categories.map(c => ({
        ...c,
        requirements: [] // Array vazio preparado para o Drag&Drop
      }))
    }

    // MAPEAR REQUISITOS DO BACKEND PARA AS RESPETIVAS COLUNAS
    if (output.requirements) {
      for (const req of output.requirements) {
        // Normalizamos o objeto para usar 'id' em vez de 'requirementId' no frontend
        const normalizedReq = {
          id: req.requirementId,
          title: req.title,
          description: req.description,
          categoryId: req.categoryId,
          position: req.position
        }

        if (normalizedReq.categoryId) {
          const cat = categories.value.find(c => c.id === normalizedReq.categoryId)
          if (cat) cat.requirements.push(normalizedReq)
          else backlog.value.push(normalizedReq)
        } else {
          backlog.value.push(normalizedReq)
        }
      }

      // Ordenar pelas posições vindas da Base de Dados
      backlog.value.sort((a, b) => a.position - b.position)
      categories.value.forEach(cat => cat.requirements.sort((a, b) => a.position - b.position))
    }

    return status

  } catch (err) {
    error.value = err.message || "Error restoring project draft."
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const globalQuestionsPromise = formQuestionApi.getAll().catch(() => [])
    const status = await fetchProject()

    if (status === 'WAITING_FOR_CLIENT') {
      const output = await globalQuestionsPromise
      questions.value = output.map(out => ({ id: out.id, question: out.question }))
    }
  } catch (err) {
    errorNotification("Erro ao carregar o projeto!")
    router.push("/dashboard/dashboard-main")
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="flex flex-col gap-6 w-full max-w-7xl mx-auto overflow-hidden">

    <!-- Loading State -->
    <div v-if="loading" class="bg-white rounded-2xl border border-slate-200 p-12 flex flex-col items-center justify-center min-h-[400px]">
      <svg class="animate-spin h-10 w-10 text-blue-800 mb-4" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path></svg>
      <p class="text-slate-500 font-medium">{{ $t('answer_project.loading') }}</p>
    </div>

    <template v-else>

      <!-- Header -->
      <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm shrink-0">
        <div class="flex items-center gap-4">
          <button @click="router.push('/dashboard/dashboard-main')" class="p-2 -ml-2 rounded-lg text-slate-400 hover:text-blue-800 hover:bg-blue-50 transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" /></svg>
          </button>
          <div>
            <h2 class="text-xl font-bold text-slate-900 tracking-tight">{{ $t('answer_project.title') }}</h2>
            <p class="text-sm text-slate-500 mt-1">{{ $t('answer_project.subtitle') }}</p>
          </div>
        </div>

        <div class="flex items-center gap-3 w-full sm:w-auto">
          <button v-if="currentUser?.role === 'ADMIN'" @click="showAssociate = true" class="flex items-center gap-2 px-4 py-2 bg-slate-50 text-slate-700 border border-slate-200 rounded-lg hover:bg-slate-100 font-semibold text-sm">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7.5v3m0 0v3m0-3h3m-3 0h-3m-2.25-4.125a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zM4 19.235v-.11a6.375 6.375 0 0112.75 0v.109A12.318 12.318 0 0110.374 21c-2.331 0-4.512-.645-6.374-1.766z" /></svg>
            {{ $t('answer_project.btn_associate') }}
          </button>
          <button v-if="canDraft" @click="sendAnswers(true)" class="flex items-center gap-2 px-4 py-2 bg-blue-50 text-blue-700 border border-blue-200 rounded-lg hover:bg-blue-100 font-semibold text-sm">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4"><path stroke-linecap="round" stroke-linejoin="round" d="M12 16.5V9.75m0 0l3 3m-3-3l-3 3M6.75 19.5a4.5 4.5 0 01-1.41-8.775 5.25 5.25 0 0110.233-2.33 3 3 0 013.758 3.848A3.752 3.752 0 0118 19.5H6.75z" /></svg>
            {{ $t('answer_project.btn_draft') }}
          </button>
        </div>
      </div>

      <div v-if="error" class="p-3 rounded-lg bg-red-50 border border-red-200 text-sm text-red-700 font-medium flex justify-between shrink-0">
        {{ error }} <button @click="error = null" class="font-bold">&times;</button>
      </div>

      <!-- MAIN AREA: Form Questions & Kanban -->
      <div class="bg-white rounded-2xl border border-slate-200 shadow-sm flex flex-col flex-1 min-h-0">

        <!-- Perguntas (Scrollavel no topo se houver muitas) -->
        <div class="p-8 pb-4 shrink-0 border-b border-slate-100">
          <div class="space-y-6">
            <div v-for="question in questions" :key="question.id" class="flex flex-col gap-2">
              <div class="flex items-center justify-between">
                <label class="block text-sm font-bold text-slate-800">{{ question.question }}</label>
                <button v-if="canFeedback && question.answerId" @click="openFeedback('answer', question.answerId)" class="text-amber-500 hover:bg-amber-50 p-1.5 rounded-lg"><svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M7.5 8.25h9m-9 3H12m-9.75 1.51c0 1.6 1.123 2.994 2.707 3.227 1.129.166 2.27.293 3.423.379.35.026.67.21.865.501L12 21l2.755-4.133a1.14 1.14 0 01.865-.501 48.172 48.172 0 003.423-.379c1.584-.233 2.707-1.626 2.707-3.228V6.741c0-1.602-1.123-2.995-2.707-3.228A48.394 48.394 0 0012 3c-2.392 0-4.744.175-7.043.513C3.373 3.746 2.25 5.14 2.25 6.741v6.018z" /></svg></button>
              </div>
              <textarea :disabled="locked" v-model="answers[question.id]" rows="2" class="w-full rounded-lg border border-slate-300 bg-white px-4 py-2 text-sm focus:border-blue-700 focus:ring-1 focus:ring-blue-700 disabled:bg-slate-50 resize-y"></textarea>
            </div>
          </div>
        </div>

        <!-- ZONA KANBAN (Flex e Scroll) -->
        <div class="p-8 flex flex-col flex-1 min-h-0 bg-slate-50/50 rounded-b-2xl">

          <div class="flex items-center justify-between mb-6 shrink-0">
            <h3 class="text-lg font-bold text-slate-900">{{ $t('answer_project.req_section_title') }}</h3>
            <div class="flex gap-3">
              <button v-if="canManageCategories" @click="showCategoryModal = true" class="px-4 py-2 bg-white border border-slate-300 text-slate-700 rounded-lg hover:bg-slate-50 text-sm font-semibold shadow-sm flex items-center gap-2">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" /></svg> {{ $t('answer_project.btn_add_category') }}
              </button>
              <button v-if="!locked" @click="openRequirementModal()" class="px-4 py-2 bg-slate-800 text-white rounded-lg hover:bg-slate-700 text-sm font-semibold shadow-sm flex items-center gap-2">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" /></svg> {{ $t('answer_project.btn_add_req') }}
              </button>
            </div>
          </div>

          <!-- BACKLOG (Arraste Horizontal / Grelha) -->
          <div class="mb-8 shrink-0">
            <h4 class="text-xs font-bold text-slate-500 uppercase tracking-wider mb-3 ml-1">{{ $t('answer_project.backlog_title') || 'Requisitos Soltos (Arraste para as categorias)' }}</h4>
            <VueDraggable
                v-model="backlog"
                group="kanban"
                animation="150"
                :disabled="locked"
                class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3 p-4 bg-slate-100 border border-slate-200 border-dashed rounded-xl min-h-[80px]"
            >
              <div v-for="(req, i) in backlog" :key="req.id" @click="openRequirementModal(req, i, null)" class="bg-white border border-slate-200 p-3 rounded-lg shadow-sm hover:border-blue-400 cursor-grab flex items-center justify-between">
                <span class="text-sm font-semibold text-slate-700 truncate">{{ req.title }}</span>
                <svg class="w-4 h-4 text-slate-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M3.75 9h16.5m-16.5 6.75h16.5" /></svg>
              </div>
            </VueDraggable>
          </div>

          <!-- COLUNAS CATEGORIAS (Scroll Horizontal) -->
          <div class="flex gap-4 overflow-x-auto pb-4 flex-1">
            <div v-for="cat in categories" :key="cat.id" class="w-80 shrink-0 bg-slate-100 border border-slate-200 rounded-xl flex flex-col max-h-[500px]">
              <div class="p-3 border-b border-slate-200 bg-slate-50 rounded-t-xl flex justify-between items-center shrink-0">
                <h4 class="font-bold text-slate-800 text-sm">{{ cat.name }}</h4>
                <span class="text-xs bg-slate-200 text-slate-600 px-2 py-0.5 rounded-full font-bold">{{ cat.requirements.length }}</span>
              </div>

              <VueDraggable
                  v-model="cat.requirements"
                  group="kanban"
                  animation="150"
                  :disabled="locked"
                  class="flex-1 p-3 flex flex-col gap-3 overflow-y-auto min-h-[100px] custom-scrollbar"
              >
                <div v-for="(req, i) in cat.requirements" :key="req.id" @click="openRequirementModal(req, i, cat.id)" class="bg-white border border-slate-200 p-3 rounded-lg shadow-sm hover:border-blue-400 cursor-grab">
                  <span class="text-sm font-semibold text-slate-700 line-clamp-2">{{ req.title }}</span>
                </div>
              </VueDraggable>
            </div>

            <!-- Empty state das categorias -->
            <div v-if="categories.length === 0" class="w-full flex flex-col items-center justify-center py-12 text-slate-400 border-2 border-dashed border-slate-200 rounded-xl">
              <svg class="w-10 h-10 mb-2 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M9 4.5v15m6-15v15m-10.5-6h15m-15-6h15" /></svg>
              <span class="text-sm">Sem categorias. Crie uma nova coluna.</span>
            </div>
          </div>

        </div>

        <!-- Botão Submeter Principal -->
        <div v-if="!locked" class="p-6 border-t border-slate-100 flex justify-end shrink-0 bg-white rounded-b-2xl">
          <button @click="sendAnswers(false)" class="bg-blue-800 text-white font-semibold px-8 py-3 rounded-xl flex items-center gap-2 hover:bg-blue-700 active:scale-[0.98] shadow-md">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M6 12L3.269 3.126A59.768 59.768 0 0121.485 12 59.77 59.77 0 013.27 20.876L5.999 12zm0 0h7.5" /></svg>
            {{ $t('answer_project.btn_submit') }}
          </button>
        </div>

      </div>
    </template>

    <!-- Modais -->
    <AssociateTech
        v-if="showAssociate"
        :projectId="projectId"
        @close="showAssociate = false"
    />

    <Feedback
        v-if="activeFeedback"
        :object="activeFeedback"
        @close="activeFeedback = null"
    />

    <CategoryModal
        v-if="showCategoryModal"
        @close="showCategoryModal = false"
        @save="handleAddCategory"
    />

    <RequirementModal
        v-if="showRequirementModal"
        :requirement="activeRequirement" :index="activeRequirementIndex"
        :locked="locked" :canFeedback="canFeedback"
        @close="showRequirementModal = false"
        @save="handleSaveRequirement" @update="handleUpdateRequirement"
        @remove="handleRemoveRequirement" @feedback="handleRequirementFeedback"
    />
  </div>
</template>


<style scoped>

.custom-scrollbar::-webkit-scrollbar { width: 5px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background-color: #cbd5e1; border-radius: 10px; }
.cursor-grab { cursor: grab; }
.cursor-grab:active { cursor: grabbing; }

</style>