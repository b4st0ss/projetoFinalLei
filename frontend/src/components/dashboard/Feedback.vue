<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import ErrorAlert from "../alerts/ErrorAlert.vue"
import { feedbackApi } from "../../api/feedback.js"

const props = defineProps({
  user: { type: Object, required: true },
  object: { type: Object, required: true }
})

const emit = defineEmits(['close'])
const { t } = useI18n()

const feedbacks = ref([])
const message = ref('')
const error = ref(null)
const loading = ref(true)

async function fetchFeedbacks() {
  error.value = null
  loading.value = true

  try {
    let output;

    // Verifica o tipo e usa a nova API limpa
    if (props.object.type === 'answer') {
      output = await feedbackApi.getByAnswer(props.object.id);
    } else {
      output = await feedbackApi.getByRequirement(props.object.id);
    }

    feedbacks.value = output;
  } catch (err) {
    error.value = err.message || t('feedback.err_fetch');
  } finally {
    loading.value = false;
  }
}

async function send() {
  error.value = null

  if (message.value.trim() === "") {
    error.value = t('feedback.err_empty')
    return
  }

  try {
    let answerId = null;
    let reqId = null;

    if (props.object.type === 'answer') {
      answerId = props.object.id;
    } else {
      reqId = props.object.id;
    }

    await feedbackApi.create(message.value, answerId, reqId);

    // Refresh e limpa o input
    message.value = "";
    await fetchFeedbacks();

  } catch (err) {
    error.value = err.message || t('feedback.err_submit');
  }
}

// Helper para formatar a data (se o teu backend enviar timestamp)
function formatDate(dateString) {
  if (!dateString) return '';
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('pt-PT', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  }).format(date);
}

onMounted(() => {
  fetchFeedbacks();
})
</script>

<template>
  <div class="fixed inset-0 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm z-50 p-4">

    <div class="bg-white text-slate-900 rounded-2xl w-full max-w-2xl shadow-2xl flex flex-col h-[85vh] max-h-[800px]">

      <!-- Header do Modal -->
      <div class="p-6 border-b border-slate-100 flex items-start justify-between shrink-0">
        <div>
          <h2 class="text-xl font-bold tracking-tight text-slate-900">{{ $t('feedback.title') }}</h2>
        </div>

        <button @click="emit('close')" class="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <div v-if="error" class="mx-6 mt-4">
        <ErrorAlert :message="error" />
      </div>

      <!-- Área de Histórico (Scroll) -->
      <div class="flex-1 overflow-y-auto p-6 bg-slate-50/50 custom-scrollbar">

        <div v-if="loading" class="h-full flex items-center justify-center">
          <svg class="animate-spin h-8 w-8 text-blue-800" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
          </svg>
        </div>

        <div v-else-if="feedbacks.length === 0" class="h-full flex flex-col items-center justify-center text-slate-400">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-12 h-12 mb-3 text-slate-300">
            <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 12.76c0 1.6 1.123 2.994 2.707 3.227 1.068.157 2.148.279 3.238.364.466.037.893.281 1.153.671L12 21l2.652-3.978c.26-.39.687-.634 1.153-.67 1.09-.086 2.17-.208 3.238-.365 1.584-.233 2.707-1.626 2.707-3.228V6.741c0-1.602-1.123-2.995-2.707-3.228A48.394 48.394 0 0012 3c-2.392 0-4.744.175-7.043.513C3.373 3.746 2.25 5.14 2.25 6.741v6.018z" />
          </svg>
          <span class="text-sm font-medium">{{ $t('feedback.no_feedbacks') }}</span>
        </div>

        <!-- Chat Bubbles -->
        <div v-else class="flex flex-col gap-5">
          <div
              v-for="feedback in feedbacks"
              :key="feedback.id"
              class="flex flex-col gap-1"
          >
            <!-- Identificação (Role, Nome e Data) -->
            <div class="flex items-center gap-2 text-xs font-semibold">
              <span v-if="feedback.role === 'ADMIN'" class="text-red-700 bg-red-50 border border-red-100 px-2 py-0.5 rounded uppercase tracking-wider text-[9px]">Admin</span>
              <span v-else-if="feedback.role === 'TECH_TEAM'" class="text-blue-700 bg-blue-50 border border-blue-100 px-2 py-0.5 rounded uppercase tracking-wider text-[9px]">Tech Team</span>
              <span v-else class="text-green-700 bg-green-50 border border-green-100 px-2 py-0.5 rounded uppercase tracking-wider text-[9px]">Client</span>

              <span class="text-slate-700">{{ feedback.username }}</span>
              <span class="text-slate-400 font-normal ml-auto">{{ formatDate(feedback.creation_data) || feedback.creation_data }}</span>
            </div>

            <!-- Balão de Mensagem -->
            <div class="bg-white border border-slate-200 shadow-sm p-4 rounded-b-xl rounded-tr-xl text-sm text-slate-800 whitespace-pre-wrap">
              {{ feedback.message }}
            </div>
          </div>
        </div>

      </div>

      <!-- Caixa de Input (Footer) -->
      <div class="p-6 border-t border-slate-100 shrink-0 bg-white rounded-b-2xl">
        <form @submit.prevent="send" class="flex items-end gap-3">
          <div class="flex-1">
            <textarea
                v-model="message"
                :placeholder="$t('feedback.placeholder')"
                rows="1"
                @keydown.enter.prevent="send"
                class="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm text-slate-900 placeholder-slate-400 focus:bg-white focus:border-blue-700 focus:outline-none focus:ring-1 focus:ring-blue-700 resize-y max-h-32 custom-scrollbar"
            ></textarea>
          </div>

          <button
              type="submit"
              :disabled="!message.trim()"
              class="mb-1 p-3 bg-blue-800 text-white rounded-xl hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed shadow-sm shrink-0"
              :title="$t('feedback.btn_send')"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5 -rotate-45 ml-1">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 12L3.269 3.126A59.768 59.768 0 0121.485 12 59.77 59.77 0 013.27 20.876L5.999 12zm0 0h7.5" />
            </svg>
          </button>
        </form>
      </div>

    </div>
  </div>
</template>

<style scoped>

</style>