<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import ErrorAlert from "../alerts/ErrorAlert.vue"

const props = defineProps({
  requirement: { type: Object, default: null },
  index: { type: Number, default: -1 },
  locked: { type: Boolean, default: false },
  canFeedback: { type: Boolean, default: false }
})

const emit = defineEmits(['close', 'save', 'update', 'remove', 'feedback'])
const { t } = useI18n()

const title = ref('')
const description = ref('')
const error = ref(null)

onMounted(() => {
  if (props.requirement) {
    title.value = props.requirement.title || ''
    description.value = props.requirement.description || ''
  }
})

function save() {
  error.value = null

  if (!title.value.trim() || !description.value.trim()) {
    error.value = t('requirement_modal.err_empty')
    return
  }

  const payload = {
    id: props.requirement?.id, // Mantém o ID original se existir para a API saber qual atualizar
    title: title.value,
    description: description.value
  }

  if (props.requirement) {
    // Se já existe, emite o evento de UPDATE com o index
    emit('update', { index: props.index, data: payload })
  } else {
    // Se é novo, emite o evento de SAVE
    emit('save', payload)
  }
}

function handleRemove() {
  if (confirm(t('requirement_modal.confirm_remove'))) {
    emit('remove', props.index)
    emit('close')
  }
}

function handleFeedback() {
  emit('feedback', props.requirement.id)
}
</script>

<template>
  <div class="fixed inset-0 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm z-50 p-4">

    <div class="bg-white text-slate-900 rounded-2xl w-full max-w-2xl shadow-2xl flex flex-col max-h-[90vh]">

      <!-- Header do Modal -->
      <div class="p-6 border-b border-slate-100 flex items-start justify-between shrink-0">
        <div>
          <h2 class="text-xl font-bold tracking-tight text-slate-900">
            {{ props.requirement ? $t('requirement_modal.title_details') : $t('requirement_modal.title_new') }}
          </h2>
        </div>

        <div class="flex items-center gap-2">
          <!-- Botão Feedback no Header se estiver a visualizar -->
          <button
              v-if="props.requirement && props.canFeedback && props.requirement.id"
              @click="handleFeedback"
              type="button"
              class="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-amber-600 bg-amber-50 hover:bg-amber-100 rounded-lg transition-colors border border-amber-200"
              :title="$t('requirement_modal.btn_feedback')"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4">
              <path stroke-linecap="round" stroke-linejoin="round" d="M7.5 8.25h9m-9 3H12m-9.75 1.51c0 1.6 1.123 2.994 2.707 3.227 1.129.166 2.27.293 3.423.379.35.026.67.21.865.501L12 21l2.755-4.133a1.14 1.14 0 01.865-.501 48.172 48.172 0 003.423-.379c1.584-.233 2.707-1.626 2.707-3.228V6.741c0-1.602-1.123-2.995-2.707-3.228A48.394 48.394 0 0012 3c-2.392 0-4.744.175-7.043.513C3.373 3.746 2.25 5.14 2.25 6.741v6.018z" />
            </svg>
            {{ $t('requirement_modal.btn_feedback') }}
          </button>

          <button
              @click="emit('close')"
              class="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      <div v-if="error" class="mx-6 mt-4">
        <ErrorAlert :message="error" />
      </div>

      <!-- Formulário / Detalhes -->
      <div class="p-6 flex-1 overflow-y-auto custom-scrollbar flex flex-col gap-5">

        <div>
          <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
            {{ $t('requirement_modal.req_title_label') }}
          </label>
          <!-- Apenas desativado se o projeto inteiro estiver "locked" -->
          <input
              v-model="title"
              type="text"
              :placeholder="$t('requirement_modal.req_title_placeholder')"
              :disabled="props.locked"
              class="w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 placeholder-slate-400 transition-colors focus:border-blue-700 focus:outline-none focus:ring-1 focus:ring-blue-700 disabled:bg-slate-50 disabled:text-slate-700"
          />
        </div>

        <div class="flex-1 flex flex-col">
          <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
            {{ $t('requirement_modal.req_desc_label') }}
          </label>
          <!-- Apenas desativado se o projeto inteiro estiver "locked" -->
          <textarea
              v-model="description"
              :placeholder="$t('requirement_modal.req_desc_placeholder')"
              :disabled="props.locked"
              rows="6"
              class="w-full flex-1 rounded-lg border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 placeholder-slate-400 transition-colors focus:border-blue-700 focus:outline-none focus:ring-1 focus:ring-blue-700 disabled:bg-slate-50 disabled:text-slate-700 resize-y"
          ></textarea>
        </div>

      </div>

      <!-- Footer Actions -->
      <div class="p-6 border-t border-slate-100 flex items-center justify-between shrink-0 bg-slate-50 rounded-b-2xl">

        <!-- Lado Esquerdo: Botão Remover (apenas se for um requisito existente e não estiver bloqueado) -->
        <div>
          <button
              v-if="props.requirement && !props.locked"
              @click="handleRemove"
              type="button"
              class="px-4 py-2 text-xs font-semibold text-red-600 bg-red-50 hover:bg-red-100 rounded-lg transition-colors border border-red-200"
          >
            {{ $t('requirement_modal.btn_remove') }}
          </button>
        </div>

        <!-- Lado Direito: Ações normais -->
        <div class="flex items-center gap-3">
          <button
              @click="emit('close')"
              class="px-5 py-2.5 bg-white border border-slate-300 text-slate-700 font-semibold rounded-lg hover:bg-slate-50 transition-colors text-sm shadow-sm"
          >
            {{ $t('requirement_modal.btn_close') }}
          </button>

          <!-- Botão Salvar/Atualizar (Aparece sempre que não estiver locked) -->
          <button
              v-if="!props.locked"
              @click="save"
              class="px-5 py-2.5 bg-blue-800 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors text-sm shadow-sm"
          >
            {{ props.requirement ? $t('requirement_modal.btn_update') : $t('answer_project.btn_add_req') }}
          </button>
        </div>

      </div>

    </div>
  </div>
</template>

<style scoped>

</style>