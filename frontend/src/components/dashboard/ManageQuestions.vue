<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { formQuestionApi } from '../../api/formQuestion.js'

const router = useRouter()
const { t } = useI18n()

const questions = ref([])
const loading = ref(true)
const error = ref(null)

// Modal de Criação
const showModal = ref(false)
const newQuestionText = ref('')
const isSubmitting = ref(false)

async function fetchQuestions() {
  loading.value = true
  error.value = null

  try {
    const response = await formQuestionApi.getAll()
    questions.value = response || []
  } catch (e) {
    error.value = e.message || t('manage_questions.err_load')
  } finally {
    loading.value = false
  }
}

async function toggleStatus(question) {
  error.value = null

  const originalStatus = question.selected
  const newStatus = !originalStatus

  // Optimistic Update instantâneo na UI
  question.selected = newStatus

  try {
    await formQuestionApi.toggleStatus(question.id)
  } catch (e) {
    question.selected = originalStatus
    error.value = e.message || t('manage_questions.err_toggle')
  }
}

async function handleCreateQuestion() {
  if (!newQuestionText.value.trim()) {
    error.value = t('manage_questions.err_empty')
    return
  }

  isSubmitting.value = true
  error.value = null

  try {
    await formQuestionApi.create(newQuestionText.value.trim())
    newQuestionText.value = ''
    showModal.value = false
    await fetchQuestions()
  } catch (e) {
    error.value = e.message || t('manage_questions.err_create')
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  fetchQuestions()
})
</script>

<template>
  <div class="flex flex-col gap-6 w-full max-w-7xl mx-auto">

    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
      <div class="flex items-center gap-4">
        <button
            @click="router.push('/dashboard/dashboard-main')"
            class="p-2 -ml-2 rounded-lg text-slate-400 hover:text-blue-800 hover:bg-blue-50 transition-colors"
            :title="$t('manage_questions.back_tooltip')"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" />
          </svg>
        </button>

        <div>
          <h2 class="text-xl font-bold text-slate-900 tracking-tight">
            {{ $t('manage_questions.title') }}
          </h2>
          <p class="text-sm text-slate-500 mt-1">
            {{ $t('manage_questions.subtitle') }}
          </p>
        </div>
      </div>

      <!-- Botão Nova Pergunta -->
      <button
          @click="showModal = true"
          class="flex items-center gap-2 px-4 py-2 bg-slate-800 text-white rounded-lg hover:bg-slate-700 transition-colors text-sm font-semibold shadow-sm w-full sm:w-auto justify-center"
      >
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
        </svg>
        {{ $t('manage_questions.btn_new') }}
      </button>
    </div>

    <!-- Alertas de Erro -->
    <div v-if="error" class="p-4 rounded-xl bg-red-50 border border-red-200 text-sm text-red-700 font-medium flex items-center justify-between shadow-sm">
      {{ error }}
      <button @click="error = null" class="text-red-500 hover:text-red-700 font-bold">&times;</button>
    </div>

    <!-- Content Card -->
    <div class="bg-white rounded-2xl border border-slate-200 shadow-sm flex flex-col overflow-hidden">

      <!-- Tabela -->
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm text-slate-600">
          <thead class="bg-slate-50 text-xs uppercase text-slate-500 border-b border-slate-200">
          <tr>
            <th scope="col" class="px-6 py-4 font-semibold w-24">ID</th>
            <th scope="col" class="px-6 py-4 font-semibold">{{ $t('manage_questions.table_question') }}</th>
            <th scope="col" class="px-6 py-4 font-semibold text-center w-36">{{ $t('manage_questions.table_status') }}</th>
          </tr>
          </thead>

          <tbody class="divide-y divide-slate-100">
          <!-- Estado de Loading -->
          <tr v-if="loading">
            <td colspan="3" class="px-6 py-12 text-center text-slate-400">
              <svg class="animate-spin h-6 w-6 text-blue-800 mx-auto mb-3" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
              </svg>
              A carregar...
            </td>
          </tr>

          <!-- Empty State -->
          <tr v-else-if="questions.length === 0">
            <td colspan="3" class="px-6 py-12 text-center text-slate-500">
              {{ $t('manage_questions.no_questions_found') }}
            </td>
          </tr>

          <!-- Linhas de Perguntas -->
          <tr v-else v-for="q in questions" :key="q.id" class="hover:bg-slate-50/80 transition-colors">

            <!-- ID -->
            <td class="px-6 py-4 font-mono text-xs text-slate-400">
              #{{ q.id }}
            </td>

            <!-- Texto da Pergunta -->
            <td class="px-6 py-4">
                <span class="font-medium text-slate-900 leading-relaxed">
                  {{ q.question }}
                </span>
            </td>

            <!-- Toggle Status -->
            <td class="px-6 py-4 text-center">
              <label class="relative inline-flex items-center cursor-pointer justify-center">
                <input
                    type="checkbox"
                    class="sr-only peer"
                    :checked="q.selected"
                    @change="toggleStatus(q)"
                />
                <!-- Fundo do Toggle em Verde quando ativo -->
                <div class="w-11 h-6 bg-slate-200 rounded-full peer peer-checked:bg-green-500 transition-colors duration-300"></div>
                <!-- Bolinha do Toggle -->
                <div class="absolute left-[2px] top-[2px] bg-white border border-slate-300 rounded-full h-5 w-5 transition-transform duration-300 peer-checked:translate-x-full peer-checked:border-white shadow-sm"></div>
              </label>
            </td>

          </tr>
          </tbody>
        </table>
      </div>

    </div>

    <!-- Modal Nova Pergunta -->
    <div v-if="showModal" class="fixed inset-0 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm z-50 p-4">
      <div class="bg-white rounded-2xl w-full max-w-lg shadow-2xl flex flex-col overflow-hidden">

        <div class="p-6 border-b border-slate-100 flex justify-between items-center bg-white">
          <h2 class="text-xl font-bold text-slate-900">
            {{ $t('manage_questions.modal_title') }}
          </h2>
          <button @click="showModal = false" class="p-1 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition-colors">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <div class="p-6 flex flex-col gap-2">
          <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider">
            {{ $t('manage_questions.modal_label') }}
          </label>
          <textarea
              v-model="newQuestionText"
              rows="4"
              :placeholder="$t('manage_questions.modal_placeholder')"
              class="w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:ring-1 focus:ring-blue-700 focus:outline-none resize-y"
          ></textarea>
        </div>

        <div class="p-6 border-t border-slate-100 bg-slate-50 flex justify-end gap-3">
          <button
              @click="showModal = false"
              type="button"
              class="px-5 py-2.5 bg-white border border-slate-300 text-slate-700 font-semibold rounded-lg hover:bg-slate-50 transition-colors text-sm shadow-sm"
          >
            {{ $t('manage_questions.btn_cancel') }}
          </button>
          <button
              @click="handleCreateQuestion"
              :disabled="isSubmitting"
              type="button"
              class="px-5 py-2.5 bg-blue-800 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors text-sm shadow-sm flex items-center gap-2 disabled:opacity-50"
          >
            <svg v-if="isSubmitting" class="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
            </svg>
            {{ $t('manage_questions.btn_save') }}
          </button>
        </div>

      </div>
    </div>

  </div>
</template>