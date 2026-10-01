<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import ErrorAlert from "../alerts/ErrorAlert.vue"

const emit = defineEmits(['close', 'save'])
const { t } = useI18n()

const name = ref('')
const error = ref(null)

function save() {
  error.value = null
  if (!name.value.trim()) {
    error.value = t('category_modal.err_empty')
    return
  }
  emit('save', { name: name.value })
}
</script>

<template>
  <div class="fixed inset-0 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm z-50 p-4">
    <div class="bg-white text-slate-900 rounded-2xl w-full max-w-sm shadow-2xl flex flex-col">
      <div class="p-6 border-b border-slate-100 flex items-start justify-between">
        <h2 class="text-xl font-bold text-slate-900">{{ $t('category_modal.title_new') }}</h2>
        <button @click="emit('close')" class="text-slate-400 hover:text-slate-700">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
        </button>
      </div>
      <div v-if="error" class="mx-6 mt-4"><ErrorAlert :message="error" /></div>
      <div class="p-6">
        <label class="block text-xs font-semibold text-slate-700 uppercase mb-1.5">{{ $t('category_modal.cat_name_label') }}</label>
        <input v-model="name" type="text" :placeholder="$t('category_modal.cat_name_placeholder')" class="w-full rounded-lg border border-slate-300 px-4 py-3 text-sm focus:border-blue-700 focus:ring-1 focus:ring-blue-700 focus:outline-none" @keyup.enter="save" />
      </div>
      <div class="p-6 border-t border-slate-100 flex justify-end gap-3 bg-slate-50 rounded-b-2xl">
        <button @click="emit('close')" class="px-5 py-2.5 bg-white border border-slate-300 text-slate-700 font-semibold rounded-lg hover:bg-slate-50 text-sm">{{ $t('category_modal.btn_close') }}</button>
        <button @click="save" class="px-5 py-2.5 bg-blue-800 text-white font-semibold rounded-lg hover:bg-blue-700 text-sm shadow-sm">{{ $t('category_modal.btn_add') }}</button>
      </div>
    </div>
  </div>
</template>