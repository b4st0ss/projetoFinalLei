<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { authApi } from '../../api/auth.js'
import { useI18n } from 'vue-i18n'

const username = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)
const error = ref(null)

const emit = defineEmits(['login-success', 'close', 'switch-to-signup'])
const router = useRouter()
const store = useStore()
const { t } = useI18n()

async function login() {
  if (!username.value || !password.value) {
    error.value = t('auth.login_desc')
    return
  }

  error.value = null
  loading.value = true

  try {
    const output = await authApi.login(username.value, password.value)

    await store.dispatch('auth/loginSuccess', output)

    emit('login-success', output)
    emit('close')
    router.push('/dashboard')

  } catch (e) {
    error.value = e.response?.data?.message || e.message || t('auth.auth_error')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div @click.self="$emit('close')" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-xs p-4">
    <div
        class="w-full max-w-md rounded-2xl bg-white p-8 shadow-xl shadow-slate-200/50 border border-slate-200"
        @click.stop
    >
      <!-- Header -->
      <div class="text-center mb-6">
        <div class="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-xl bg-blue-50 border border-blue-100 text-blue-800">
          <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
          </svg>
        </div>
        <h2 class="text-2xl font-bold text-slate-900 tracking-tight">{{ $t('auth.welcome') }}</h2>
        <p class="text-sm text-slate-500 mt-1">{{ $t('auth.login_desc') }}</p>
      </div>

      <!-- Error alert -->
      <div
          v-if="error"
          class="mb-5 flex items-center justify-between rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700"
      >
        <span>{{ error }}</span>
        <button type="button" @click="error = null" class="text-red-400 hover:text-red-600 font-bold ml-2">
          &times;
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="login" class="space-y-4">
        <div>
          <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
            {{ $t('auth.username') }}
          </label>
          <div class="relative">
            <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-slate-400">
              <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
            <input
                v-model="username"
                type="text"
                autocomplete="username"
                required
                :placeholder="$t('auth.username')"
                class="w-full rounded-lg border border-slate-300 bg-white py-2.5 pl-10 pr-3 text-sm text-slate-900 placeholder-slate-400 transition-colors focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
            />
          </div>
        </div>

        <div>
          <div class="flex items-center justify-between mb-1.5">
            <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider">
              {{ $t('auth.password') }}
            </label>
          </div>
          <div class="relative">
            <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-slate-400">
              <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
            </div>
            <input
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                autocomplete="current-password"
                required
                placeholder="••••••••"
                class="w-full rounded-lg border border-slate-300 bg-white py-2.5 pl-10 pr-10 text-sm text-slate-900 placeholder-slate-400 transition-colors focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
            />
            <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute inset-y-0 right-0 flex items-center pr-3 text-slate-400 hover:text-slate-600"
            >
              <svg v-if="!showPassword" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
              <svg v-else class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18" />
              </svg>
            </button>
          </div>
        </div>

        <button
            type="submit"
            :disabled="loading"
            class="w-full flex items-center justify-center rounded-lg bg-blue-800 py-2.5 px-4 text-sm font-semibold text-white shadow-sm hover:bg-blue-700 active:scale-[0.99] transition-all disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <svg v-if="loading" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
          </svg>
          <span>{{ loading ? $t('auth.loading') : $t('auth.login_btn') }}</span>
        </button>
      </form>

      <!-- Footer -->
      <div class="mt-6 text-center text-sm text-slate-500">
        {{ $t('auth.no_account') }}
        <button
            type="button"
            @click="$emit('switch-to-signup')"
            class="font-semibold text-blue-800 hover:text-blue-900 hover:underline ml-1"
        >
          {{ $t('auth.register') }}
        </button>
      </div>
    </div>
  </div>
</template>