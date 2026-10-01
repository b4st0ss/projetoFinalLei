<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../../api/auth.js'
import { useI18n } from 'vue-i18n' // Injeção do i18n

const router = useRouter()
const { t } = useI18n() // Usar no script

const name = ref('')
const username = ref('')
const password = ref('')
const email = ref('')
const role = ref('CLIENT')

const error = ref(null)
const successMessage = ref(null)
const loading = ref(false)

async function create() {
  error.value = null;
  successMessage.value = null;

  if (!username.value || !password.value || !email.value || !name.value || !role.value) {
    error.value = t('create_users.err_empty_fields');
    return;
  }

  loading.value = true;

  try {
    const output = await authApi.registerAdmin(
        name.value,
        username.value,
        email.value,
        password.value,
        role.value
    );

    successMessage.value = output.message || t('create_users.msg_success');

    name.value = '';
    username.value = '';
    password.value = '';
    email.value = '';
    role.value = 'CLIENT';

  } catch (e) {
    error.value = e.message || t('create_users.err_create_failed');
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
            :title="$t('create_users.back_tooltip')"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" />
          </svg>
        </button>

        <div>
          <h2 class="text-xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            {{ $t('create_users.title') }}
          </h2>
          <p class="text-sm text-slate-500 mt-1">
            {{ $t('create_users.subtitle') }}
          </p>
        </div>
      </div>
    </div>

    <!-- Formulário: Removido o max-w-4xl para ocupar tudo (w-full implícito) -->
    <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-8">

      <form @submit.prevent="create" class="space-y-6">

        <!-- Grelha de Campos (expandida para o ecrã inteiro) -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-6">

          <!-- Nome -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
              {{ $t('create_users.name_label') }}
            </label>
            <input
                v-model="name"
                type="text"
                :placeholder="$t('create_users.name_placeholder')"
                required
                class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 placeholder-slate-400 transition-colors focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
            />
          </div>

          <!-- Username -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
              {{ $t('create_users.username_label') }}
            </label>
            <input
                v-model="username"
                type="text"
                :placeholder="$t('create_users.username_placeholder')"
                required
                class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 placeholder-slate-400 transition-colors focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
            />
          </div>

          <!-- Email -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
              {{ $t('create_users.email_label') }}
            </label>
            <input
                v-model="email"
                type="email"
                :placeholder="$t('create_users.email_placeholder')"
                required
                class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 placeholder-slate-400 transition-colors focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
            />
          </div>

          <!-- Password -->
          <div>
            <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
              {{ $t('create_users.password_label') }}
            </label>
            <input
                v-model="password"
                type="password"
                placeholder="••••••••"
                required
                class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 placeholder-slate-400 transition-colors focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
            />
          </div>

          <!-- Tipo de Utilizador (Role) - Ocupa ambas as colunas -->
          <div class="md:col-span-2">
            <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
              {{ $t('create_users.role_label') }}
            </label>
            <div class="relative">
              <select
                  v-model="role"
                  class="w-full appearance-none rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 transition-colors focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20 cursor-pointer"
              >
                <option value="CLIENT">{{ $t('create_users.role_client') }}</option>
                <option value="TECH_TEAM">{{ $t('create_users.role_tech') }}</option>
                <option value="ADMIN">{{ $t('create_users.role_admin') }}</option>
              </select>
              <!-- Ícone Customizado para o Select -->
              <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-slate-500">
                <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </div>
            </div>
          </div>
        </div>

        <!-- Alertas -->
        <div v-if="error" class="p-3 mt-4 rounded-lg bg-red-50 border border-red-200 text-sm text-red-700 font-medium flex items-center justify-between">
          {{ error }}
          <button type="button" @click="error = null" class="text-red-500 hover:text-red-700 font-bold">&times;</button>
        </div>

        <div v-if="successMessage" class="p-3 mt-4 rounded-lg bg-green-50 border border-green-200 text-sm text-green-700 font-medium flex items-center justify-between">
          {{ successMessage }}
          <button type="button" @click="successMessage = null" class="text-green-500 hover:text-green-700 font-bold">&times;</button>
        </div>

        <!-- Ações -->
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
            {{ loading ? $t('create_users.btn_creating') : $t('create_users.btn_create') }}
          </button>
        </div>

      </form>
    </div>
  </div>
</template>