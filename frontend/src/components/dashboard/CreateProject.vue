<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { projectApi } from '../../api/project.js'
import { successNotification } from "../alerts/notifications.js"

const router = useRouter()
const { t } = useI18n()

const title = ref('')
const description = ref('')
const email = ref('')

const error = ref(null)
const loading = ref(false)

async function create() {
  error.value = null;

  if (!title.value || !description.value || !email.value) {
    error.value = t('create_project.err_empty_fields');
    return;
  }

  loading.value = true;

  try {
    const output = await projectApi.create(title.value, description.value, email.value);

    successNotification(output.message || t('create_project.msg_success'))
    router.push({ path: '/dashboard/dashboard-main' })

  } catch (e) {
    error.value = e.message || 'Falha ao criar o projeto.';
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <!-- Mantém o max-w-7xl que iguala o DashboardMain e Perfil -->
  <div class="flex flex-col gap-6 w-full max-w-7xl mx-auto">

    <!-- Header / Voltar -->
    <div class="flex items-center justify-between bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
      <div class="flex items-center gap-4">
        <button
            @click="router.push('/dashboard/dashboard-main')"
            class="p-2 -ml-2 rounded-lg text-slate-400 hover:text-blue-800 hover:bg-blue-50 transition-colors"
            :title="$t('create_project.back_tooltip')"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" />
          </svg>
        </button>

        <div>
          <h2 class="text-xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            {{ $t('create_project.title') }}
          </h2>
          <p class="text-sm text-slate-500 mt-1">
            {{ $t('create_project.subtitle') }}
          </p>
        </div>
      </div>
    </div>

    <!-- Formulário: Removido o max-w-4xl para o fundo branco ocupar tudo -->
    <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-8">

      <form @submit.prevent="create" class="space-y-8">

        <!-- Secção: Informação do Cliente -->
        <div>
          <h3 class="text-sm font-bold text-slate-900 mb-4 pb-2 border-b border-slate-100">
            {{ $t('create_project.client_info') }}
          </h3>

          <div class="max-w-md">
            <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
              {{ $t('create_project.email_label') }}
            </label>
            <input
                v-model="email"
                type="email"
                :placeholder="$t('create_project.email_placeholder')"
                required
                class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 placeholder-slate-400 transition-colors focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
            />
          </div>
        </div>

        <!-- Secção: Detalhes do Projeto -->
        <div>
          <h3 class="text-sm font-bold text-slate-900 mb-4 pb-2 border-b border-slate-100">
            {{ $t('create_project.project_info') }}
          </h3>

          <!-- Limite interno (max-w-3xl) para que as caixas de texto não estiquem infinitamente -->
          <div class="grid grid-cols-1 gap-6 max-w-3xl">
            <!-- Título -->
            <div>
              <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
                {{ $t('create_project.project_title_label') }}
              </label>
              <input
                  v-model="title"
                  type="text"
                  :placeholder="$t('create_project.project_title_placeholder')"
                  required
                  class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 placeholder-slate-400 transition-colors focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
              />
            </div>

            <!-- Descrição (Textarea) -->
            <div>
              <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
                {{ $t('create_project.project_desc_label') }}
              </label>
              <textarea
                  v-model="description"
                  :placeholder="$t('create_project.project_desc_placeholder')"
                  required
                  rows="4"
                  class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 placeholder-slate-400 transition-colors focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20 resize-y"
              ></textarea>
            </div>
          </div>
        </div>

        <!-- Alertas de Erro -->
        <div v-if="error" class="p-3 mt-4 rounded-lg bg-red-50 border border-red-200 text-sm text-red-700 font-medium flex items-center justify-between">
          <div class="flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            {{ error }}
          </div>
          <button type="button" @click="error = null" class="text-red-500 hover:text-red-700 font-bold">&times;</button>
        </div>



        <div class="pt-6 border-t border-slate-100 flex justify-center">
          <button
              type="submit"
              :disabled="loading"
              class="bg-blue-800 text-white font-semibold px-6 py-2.5 rounded-lg flex items-center gap-2 hover:bg-blue-700 active:scale-[0.98] transition-all shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg v-if="loading" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
            </svg>
            {{ loading ? $t('create_project.btn_submitting') : $t('create_project.btn_submit') }}
          </button>
        </div>

      </form>
    </div>
  </div>
</template>