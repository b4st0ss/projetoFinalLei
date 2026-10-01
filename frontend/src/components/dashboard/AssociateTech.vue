<script setup>
import { ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { projectApi } from "../../api/project.js"
import { userApi } from "../../api/user.js"

const props = defineProps({
  user: { type: Object, required: true },
  projectId: { type: String, required: true }
})
const emit = defineEmits(['close'])
const { t } = useI18n()

const error = ref(null)
const successMessage = ref("")
const loading = ref(true)

const selectedTechs = ref([])
const activeTechs = ref([])
const searchQuery = ref("")

// Filtra a pesquisa e separa os técnicos que JÁ ESTÃO associados
const filteredAssociated = computed(() => {
  const q = searchQuery.value.toLowerCase()
  return selectedTechs.value.filter(t =>
      t.name?.toLowerCase().includes(q) || t.email?.toLowerCase().includes(q)
  )
})

// Filtra a pesquisa e separa os técnicos que AINDA NÃO estão associados
const filteredAvailable = computed(() => {
  const q = searchQuery.value.toLowerCase()
  return activeTechs.value.filter(t =>
      !selectedTechs.value.some(selected => selected.id === t.id) &&
      (t.name?.toLowerCase().includes(q) || t.email?.toLowerCase().includes(q))
  )
})

async function fetchLists() {
  loading.value = true
  error.value = null

  try {
    // Pedidos paralelos para ser super rápido!
    const [associatedOutput, activeOutput] = await Promise.all([
      projectApi.getTechs(props.projectId),
      userApi.getActiveTechs()
    ])

    selectedTechs.value = associatedOutput
    activeTechs.value = activeOutput
  } catch (e) {
    error.value = e.message || "Erro ao carregar lista de técnicos."
  } finally {
    loading.value = false
  }
}

async function addTech(techId) {
  error.value = null
  successMessage.value = ""

  try {
    const output = await projectApi.associateTech(Number(props.projectId), Number(techId))
    successMessage.value = output.message
    await fetchLists() // Atualiza as listas
  } catch (e) {
    error.value = e.message
  }
}

async function removeTech(techId) {
  error.value = null
  successMessage.value = ""

  try {
    const output = await projectApi.disassociateTech(Number(props.projectId), Number(techId))
    successMessage.value = output.message
    await fetchLists() // Atualiza as listas
  } catch (e) {
    error.value = e.message
  }
}

onMounted(() => {
  fetchLists()
})
</script>

<template>
  <!-- Fundo escuro com desfoque -->
  <div class="fixed inset-0 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm z-50 p-4">

    <!-- Cartão Modal Branco -->
    <div class="bg-white text-slate-900 rounded-2xl w-full max-w-lg shadow-2xl flex flex-col max-h-[90vh]">

      <!-- Header do Modal -->
      <div class="p-6 border-b border-slate-100 flex items-start justify-between shrink-0">
        <div>
          <h2 class="text-xl font-bold tracking-tight text-slate-900">{{ $t('associate_tech.title') }}</h2>
          <p class="text-xs text-slate-500 mt-1">{{ $t('associate_tech.subtitle') }}</p>
        </div>

        <button @click="emit('close')" class="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Alertas -->
      <div v-if="error" class="mx-6 mt-4 p-3 rounded-lg bg-red-50 border border-red-200 text-xs text-red-700 font-medium flex justify-between shrink-0">
        {{ error }}
        <button @click="error = null" class="text-red-500 font-bold">&times;</button>
      </div>
      <div v-if="successMessage" class="mx-6 mt-4 p-3 rounded-lg bg-green-50 border border-green-200 text-xs text-green-700 font-medium flex justify-between shrink-0">
        {{ successMessage }}
        <button @click="successMessage = ''" class="text-green-500 font-bold">&times;</button>
      </div>

      <!-- Barra de Pesquisa -->
      <div class="px-6 py-4 shrink-0">
        <div class="relative w-full">
          <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
            <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
          <input
              v-model="searchQuery"
              type="text"
              :placeholder="$t('associate_tech.search_placeholder')"
              class="w-full rounded-lg border border-slate-300 bg-slate-50 py-2 pl-9 pr-4 text-sm text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:outline-none focus:ring-1 focus:ring-blue-700 focus:bg-white transition-colors"
          />
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex-1 p-10 flex flex-col items-center justify-center">
        <svg class="animate-spin h-8 w-8 text-blue-800 mb-2" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
        </svg>
      </div>

      <!-- Listas de Técnicos (Scrollable) -->
      <div v-else class="flex-1 overflow-y-auto px-6 pb-6 custom-scrollbar">

        <!-- Secção: JÁ ASSOCIADOS -->
        <div class="mb-6">
          <h3 class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3 flex items-center justify-between">
            {{ $t('associate_tech.lbl_associated') }}
            <span class="bg-slate-100 text-slate-500 px-2 py-0.5 rounded-full text-[10px]">{{ filteredAssociated.length }}</span>
          </h3>

          <div v-if="filteredAssociated.length === 0" class="text-center py-4 text-xs text-slate-400 italic bg-slate-50 rounded-lg border border-dashed border-slate-200">
            {{ searchQuery ? $t('associate_tech.no_results') : $t('associate_tech.no_associated') }}
          </div>

          <div class="flex flex-col gap-2">
            <div
                v-for="tech in filteredAssociated"
                :key="tech.id"
                class="flex items-center justify-between p-3 bg-white border border-slate-200 rounded-xl shadow-sm hover:border-slate-300 transition-colors"
            >
              <div class="flex items-center gap-3">
                <div class="h-8 w-8 rounded-full bg-blue-50 text-blue-800 flex items-center justify-center font-bold text-xs">
                  {{ tech.name?.charAt(0).toUpperCase() || 'U' }}
                </div>
                <div>
                  <div class="text-sm font-bold text-slate-900">{{ tech.name }}</div>
                  <div class="text-xs text-slate-500">{{ tech.email }}</div>
                </div>
              </div>
              <button
                  @click="removeTech(tech.id)"
                  class="text-xs font-semibold px-3 py-1.5 text-red-600 bg-red-50 hover:bg-red-100 rounded-lg transition-colors"
              >
                {{ $t('associate_tech.btn_remove') }}
              </button>
            </div>
          </div>
        </div>

        <!-- Secção: DISPONÍVEIS -->
        <div>
          <h3 class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3 flex items-center justify-between">
            {{ $t('associate_tech.lbl_available') }}
            <span class="bg-slate-100 text-slate-500 px-2 py-0.5 rounded-full text-[10px]">{{ filteredAvailable.length }}</span>
          </h3>

          <div v-if="filteredAvailable.length === 0" class="text-center py-4 text-xs text-slate-400 italic bg-slate-50 rounded-lg border border-dashed border-slate-200">
            {{ searchQuery ? $t('associate_tech.no_results') : $t('associate_tech.no_available') }}
          </div>

          <div class="flex flex-col gap-2">
            <div
                v-for="tech in filteredAvailable"
                :key="tech.id"
                class="flex items-center justify-between p-3 bg-slate-50 border border-slate-100 rounded-xl hover:bg-white hover:border-slate-200 hover:shadow-sm transition-all"
            >
              <div class="flex items-center gap-3">
                <div class="h-8 w-8 rounded-full bg-slate-200 text-slate-600 flex items-center justify-center font-bold text-xs">
                  {{ tech.name?.charAt(0).toUpperCase() || 'U' }}
                </div>
                <div>
                  <div class="text-sm font-bold text-slate-900">{{ tech.name }}</div>
                  <div class="text-xs text-slate-500">{{ tech.email }}</div>
                </div>
              </div>
              <button
                  @click="addTech(tech.id)"
                  class="text-xs font-semibold px-3 py-1.5 text-blue-700 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors border border-blue-100"
              >
                {{ $t('associate_tech.btn_add') }}
              </button>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>
