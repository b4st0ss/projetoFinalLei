<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { userApi } from '../../api/user.js'

const router = useRouter()
const { t } = useI18n()

const users = ref([])
const loading = ref(true)
const error = ref(null)
const successMessage = ref("")

// Ref para a barra de pesquisa
const searchQuery = ref("")

// Computed Property para filtrar utilizadores localmente (muito mais rápido para o utilizador)
const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value

  const query = searchQuery.value.toLowerCase()
  return users.value.filter(user =>
      user.name?.toLowerCase().includes(query) ||
      user.email?.toLowerCase().includes(query) ||
      user.username?.toLowerCase().includes(query)
  )
})

async function fetchUsers() {
  loading.value = true
  error.value = null

  try {
    // API centralizada já injeta o token
    users.value = await userApi.getAll()
  } catch (e) {
    error.value = e.message || "Erro ao carregar lista de utilizadores."
  } finally {
    loading.value = false
  }
}

async function toggleStatus(user) {
  error.value = null
  successMessage.value = ""

  const originalStatus = user.status
  const newStatus = !originalStatus

  // 1. Optimistic Update: Mudamos na interface imediatamente para ser instantâneo
  user.status = newStatus

  try {
    // 2. Fazemos o pedido à API em background
    const output = await userApi.changeStatus(user.id, newStatus)
    successMessage.value = output.message || t('activate_users.msg_updated')

    // Repara que NÃO fazemos fetchUsers() aqui. Poupamos um pedido pesado ao servidor!
  } catch (e) {
    // 3. Se a API falhar, revertemos a alteração visual e mostramos o erro
    user.status = originalStatus
    error.value = e.message || t('activate_users.err_update')
  }
}

onMounted(() => {
  fetchUsers()
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
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" />
          </svg>
        </button>

        <div>
          <h2 class="text-xl font-bold text-slate-900 tracking-tight">
            {{ $t('activate_users.title') }}
          </h2>
          <p class="text-sm text-slate-500 mt-1">
            {{ $t('activate_users.subtitle') }}
          </p>
        </div>
      </div>
    </div>

    <!-- Alertas -->
    <div v-if="error" class="p-4 rounded-xl bg-red-50 border border-red-200 text-sm text-red-700 font-medium flex items-center justify-between shadow-sm">
      {{ error }}
      <button @click="error = null" class="text-red-500 hover:text-red-700 font-bold">&times;</button>
    </div>
    <div v-if="successMessage" class="p-4 rounded-xl bg-green-50 border border-green-200 text-sm text-green-700 font-medium flex items-center justify-between shadow-sm">
      {{ successMessage }}
      <button @click="successMessage = ''" class="text-green-500 hover:text-green-700 font-bold">&times;</button>
    </div>

    <!-- Content Card -->
    <div class="bg-white rounded-2xl border border-slate-200 shadow-sm flex flex-col overflow-hidden">

      <!-- Toolbar (Pesquisa) -->
      <div class="p-4 border-b border-slate-200 bg-slate-50/50 flex items-center justify-between">
        <div class="relative w-full max-w-md">
          <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
            <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
          <input
              v-model="searchQuery"
              type="text"
              :placeholder="$t('activate_users.search_placeholder')"
              class="w-full rounded-lg border border-slate-300 bg-white py-2 pl-10 pr-4 text-sm text-slate-900 placeholder-slate-400 focus:border-blue-700 focus:outline-none focus:ring-1 focus:ring-blue-700"
          />
        </div>
      </div>

      <!-- Tabela -->
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm text-slate-600">
          <thead class="bg-slate-50 text-xs uppercase text-slate-500 border-b border-slate-200">
          <tr>
            <th scope="col" class="px-6 py-4 font-semibold">{{ $t('activate_users.table_user') }}</th>
            <th scope="col" class="px-6 py-4 font-semibold">{{ $t('activate_users.table_role') }}</th>
            <th scope="col" class="px-6 py-4 font-semibold text-center">{{ $t('activate_users.table_status') }}</th>
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

          <!-- Empty State (Sem resultados na pesquisa) -->
          <tr v-else-if="filteredUsers.length === 0">
            <td colspan="3" class="px-6 py-12 text-center text-slate-500">
              {{ $t('activate_users.no_users_found') }}
            </td>
          </tr>

          <!-- Linhas de Utilizadores -->
          <tr v-else v-for="user in filteredUsers" :key="user.id" class="hover:bg-slate-50/80 transition-colors">

            <!-- Info do Utilizador -->
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div class="h-9 w-9 rounded-full bg-blue-100 text-blue-800 flex items-center justify-center font-bold text-xs shrink-0">
                  {{ user.name?.charAt(0).toUpperCase() || 'U' }}
                </div>
                <div>
                  <div class="font-bold text-slate-900">{{ user.name || user.username }}</div>
                  <div class="text-xs text-slate-500">{{ user.email }}</div>
                </div>
              </div>
            </td>

            <!-- Role (Badges) -->
            <td class="px-6 py-4">
              <span v-if="user.role === 'ADMIN'" class="px-2.5 py-1 rounded-md bg-red-50 border border-red-100 text-red-700 text-[10px] font-bold uppercase tracking-wider">Admin</span>
              <span v-else-if="user.role === 'TECH_TEAM'" class="px-2.5 py-1 rounded-md bg-blue-50 border border-blue-100 text-blue-700 text-[10px] font-bold uppercase tracking-wider">Tech Team</span>
              <span v-else class="px-2.5 py-1 rounded-md bg-green-50 border border-green-100 text-green-700 text-[10px] font-bold uppercase tracking-wider">Client</span>
            </td>

            <!-- Toggle Status -->
            <td class="px-6 py-4 text-center">
              <label class="relative inline-flex items-center cursor-pointer justify-center" :title="user.status ? 'Desativar acesso' : 'Ativar acesso'">
                <input
                    type="checkbox"
                    class="sr-only peer"
                    :checked="user.status"
                    @change="toggleStatus(user)"
                />
                <!-- Fundo do Toggle -->
                <div class="w-11 h-6 bg-slate-200 rounded-full peer peer-checked:bg-blue-600 transition-colors duration-300"></div>
                <!-- Bolinha do Toggle -->
                <div class="absolute left-[2px] top-[2px] bg-white border border-slate-300 rounded-full h-5 w-5 transition-transform duration-300 peer-checked:translate-x-full peer-checked:border-white shadow-sm"></div>
              </label>
              <!-- Legenda do estado -->
              <div class="text-[10px] font-bold mt-1 uppercase tracking-wider" :class="user.status ? 'text-blue-700' : 'text-slate-400'">
                {{ user.status ? $t('activate_users.status_active') : $t('activate_users.status_inactive') }}
              </div>
            </td>

          </tr>
          </tbody>
        </table>
      </div>

    </div>
  </div>
</template>