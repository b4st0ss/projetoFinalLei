<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { useI18n } from 'vue-i18n'
import { userApi } from '../../api/user.js'
import ErrorAlert from "../alerts/ErrorAlert.vue"
import SuccessAlert from "../alerts/SuccessAlert.vue"

const router = useRouter()
const store = useStore()
const { t } = useI18n()

const currentUser = computed(() => store.getters['auth/currentUser'])

const loading = ref(true)
const error = ref(null)
const successMessage = ref("")

const form = reactive({
  name: '',
  username: '',
  password: '',
  email: '',
  contact: '',
  address: ''
})

async function fetchUser() {
  error.value = null
  successMessage.value = ""
  loading.value = true

  try {
    const profileData = await userApi.getProfile()

    form.name = profileData.name || ''
    form.username = profileData.username || ''
    form.email = profileData.email || ''
    form.contact = profileData.contact || ''
    form.address = profileData.address || ''

  } catch (e) {
    error.value = e.message || t('profile.err_load')
  } finally {
    loading.value = false
  }
}

async function updateUser(field) {
  error.value = null
  successMessage.value = ""

  const newInfo = form[field]

  if (!newInfo && field !== 'password') return

  try {
    const output = await userApi.updateProfile(field, newInfo)
    successMessage.value = output.message || t('profile.msg_success')

    if (field === 'password') form.password = ''

  } catch (e) {
    error.value = e.message || t('profile.err_update')
  }
}

onMounted(() => {
  fetchUser()
})
</script>

<template>
  <div class="flex flex-col gap-6 w-full max-w-7xl mx-auto">

    <div class="flex items-center justify-between bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
      <div class="flex items-center gap-4">
        <button
            @click="router.push('/dashboard/dashboard-main')"
            class="p-2 -ml-2 rounded-lg text-slate-400 hover:text-blue-800 hover:bg-blue-50 transition-colors"
            :title="$t('profile.back_tooltip')"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" />
          </svg>
        </button>

        <div>
          <h2 class="text-xl font-bold text-slate-900 tracking-tight flex items-center gap-3">
            {{ $t('profile.title') }}
            <span v-if="currentUser?.role === 'ADMIN'" class="px-2.5 py-1 rounded-md bg-red-50 border border-red-100 text-red-700 text-[10px] font-bold uppercase tracking-wider">
              Admin
            </span>
            <span v-else-if="currentUser?.role === 'TECH_TEAM'" class="px-2.5 py-1 rounded-md bg-blue-50 border border-blue-100 text-blue-700 text-[10px] font-bold uppercase tracking-wider">
              Tech Team
            </span>
            <span v-else-if="currentUser?.role === 'CLIENT'" class="px-2.5 py-1 rounded-md bg-green-50 border border-green-100 text-green-700 text-[10px] font-bold uppercase tracking-wider">
              Client
            </span>
          </h2>
          <p class="text-sm text-slate-500 mt-1">{{ $t('profile.subtitle') }}</p>
        </div>
      </div>
    </div>

    <div v-if="error" class="p-4 rounded-xl bg-red-50 border border-red-200 text-sm text-red-700 font-medium flex items-center justify-between">
      {{ error }}
      <button @click="error = null" class="text-red-500 hover:text-red-700 font-bold">&times;</button>
    </div>

    <div v-if="successMessage" class="p-4 rounded-xl bg-green-50 border border-green-200 text-sm text-green-700 font-medium flex items-center justify-between">
      {{ successMessage }}
      <button @click="successMessage = ''" class="text-green-500 hover:text-green-700 font-bold">&times;</button>
    </div>

    <div v-if="loading" class="bg-white rounded-2xl border border-slate-200 p-12 flex flex-col items-center justify-center">
      <svg class="animate-spin h-8 w-8 text-blue-800 mb-4" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
      </svg>
      <p class="text-slate-500 font-medium text-sm">{{ $t('profile.loading') }}</p>
    </div>

    <div v-else class="bg-white rounded-2xl border border-slate-200 shadow-sm p-8">
      <div class="flex justify-center mb-10">
        <div class="w-24 h-24 rounded-full bg-blue-50 border-2 border-blue-100 flex items-center justify-center text-blue-800 text-3xl font-bold">
          {{ form.name?.charAt(0).toUpperCase() || 'U' }}
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-8 gap-y-6">

        <div>
          <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">{{ $t('profile.name_label') }}</label>
          <div class="flex gap-2">
            <input
                v-model="form.name"
                type="text"
                :placeholder="$t('profile.name_placeholder')"
                class="flex-1 rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
            />
            <button
                @click="updateUser('name')"
                class="px-3 py-2 bg-blue-50 text-blue-800 rounded-lg border border-blue-100 hover:bg-blue-800 hover:text-white transition-colors"
                :title="$t('profile.btn_save_tooltip')"
            >
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
            </button>
          </div>
        </div>

        <div>
          <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">{{ $t('profile.username_label') }}</label>
          <div class="flex gap-2">
            <input
                v-model="form.username"
                type="text"
                :placeholder="$t('profile.username_placeholder')"
                class="flex-1 rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
            />
            <button
                @click="updateUser('username')"
                class="px-3 py-2 bg-blue-50 text-blue-800 rounded-lg border border-blue-100 hover:bg-blue-800 hover:text-white transition-colors"
                :title="$t('profile.btn_save_tooltip')"
            >
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
            </button>
          </div>
        </div>

        <div>
          <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">{{ $t('profile.email_label') }}</label>
          <div class="flex gap-2">
            <input
                v-model="form.email"
                type="email"
                :placeholder="$t('profile.email_placeholder')"
                class="flex-1 rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
            />
            <button
                @click="updateUser('email')"
                class="px-3 py-2 bg-blue-50 text-blue-800 rounded-lg border border-blue-100 hover:bg-blue-800 hover:text-white transition-colors"
                :title="$t('profile.btn_save_tooltip')"
            >
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
            </button>
          </div>
        </div>

        <div>
          <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">{{ $t('profile.contact_label') }}</label>
          <div class="flex gap-2">
            <input
                v-model="form.contact"
                type="tel"
                :placeholder="$t('profile.contact_placeholder')"
                class="flex-1 rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
            />
            <button
                @click="updateUser('contact')"
                class="px-3 py-2 bg-blue-50 text-blue-800 rounded-lg border border-blue-100 hover:bg-blue-800 hover:text-white transition-colors"
                :title="$t('profile.btn_save_tooltip')"
            >
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
            </button>
          </div>
        </div>

        <div class="md:col-span-2 lg:col-span-2">
          <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">{{ $t('profile.address_label') }}</label>
          <div class="flex gap-2">
            <input
                v-model="form.address"
                type="text"
                :placeholder="$t('profile.address_placeholder')"
                class="flex-1 rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
            />
            <button
                @click="updateUser('address')"
                class="px-3 py-2 bg-blue-50 text-blue-800 rounded-lg border border-blue-100 hover:bg-blue-800 hover:text-white transition-colors"
                :title="$t('profile.btn_save_tooltip')"
            >
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
            </button>
          </div>
        </div>

        <div class="md:col-span-2 lg:col-span-3 border-t border-slate-100 pt-6 mt-2">
          <h3 class="text-sm font-bold text-slate-900 mb-4">{{ $t('profile.security') }}</h3>

          <div>
            <label class="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">{{ $t('profile.new_password_label') }}</label>
            <div class="flex gap-2 max-w-md">
              <input
                  v-model="form.password"
                  type="password"
                  :placeholder="$t('profile.new_password_placeholder')"
                  class="flex-1 rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-700/20"
              />
              <button
                  @click="updateUser('password')"
                  class="px-4 py-2 bg-slate-900 text-white rounded-lg border border-slate-900 hover:bg-slate-800 transition-colors text-sm font-semibold whitespace-nowrap"
              >
                {{ $t('profile.btn_update') }}
              </button>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>