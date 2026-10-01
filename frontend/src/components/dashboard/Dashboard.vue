<script setup>
import { computed } from 'vue'
import { useRouter, RouterView } from 'vue-router'
import { useStore } from 'vuex'

const router = useRouter()
const store = useStore()

const user = computed(() => store.getters['auth/currentUser'])

function logout() {
  store.dispatch('auth/logout')
  router.push('/')
}
</script>

<template>
  <div class="bg-slate-50 w-full min-h-screen flex text-slate-900 font-sans antialiased">
    <aside class="w-64 bg-white border-r border-slate-200 flex flex-col justify-between shadow-sm z-10">
      <div class="flex flex-col gap-8 p-6">
        <h1 class="text-blue-800 font-extrabold text-2xl tracking-tight select-none">
          Getter
        </h1>

        <nav class="flex flex-col gap-6 font-semibold text-sm">

          <!-- SECÇÃO 1: Principal / Projetos -->
          <div class="flex flex-col gap-1.5">
            <div class="px-3 text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1">
              {{ $t('sidebar.section_main') }}
            </div>

            <RouterLink
                to="/dashboard/dashboard-main"
                class="px-3 py-2.5 rounded-lg text-slate-600 hover:text-blue-800 hover:bg-slate-50 transition-colors"
                active-class="bg-blue-50 text-blue-800"
            >
              {{ $t('sidebar.dashboard') }}
            </RouterLink>

            <RouterLink
                to="/dashboard/list-projects"
                class="px-3 py-2.5 rounded-lg text-slate-600 hover:text-blue-800 hover:bg-slate-50 transition-colors"
                active-class="bg-blue-50 text-blue-800"
            >
              {{ $t('sidebar.projects') }}
            </RouterLink>
          </div>

          <!-- SECÇÃO 2: Administração (Apenas ADMIN) -->
          <div v-if="user?.role === 'ADMIN'" class="flex flex-col gap-1.5">
            <div class="px-3 text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1 pt-4 border-t border-slate-100">
              {{ $t('sidebar.section_admin') }}
            </div>

            <RouterLink
                to="/dashboard/activate-users"
                class="px-3 py-2.5 rounded-lg text-slate-600 hover:text-blue-800 hover:bg-slate-50 transition-colors"
                active-class="bg-blue-50 text-blue-800"
            >
              {{ $t('sidebar.activate_users') }}
            </RouterLink>

            <RouterLink
                to="/dashboard/create-users"
                class="px-3 py-2.5 rounded-lg text-slate-600 hover:text-blue-800 hover:bg-slate-50 transition-colors"
                active-class="bg-blue-50 text-blue-800"
            >
              {{ $t('sidebar.create_users') }}
            </RouterLink>

            <RouterLink
                to="/dashboard/manage-questions"
                class="px-3 py-2.5 rounded-lg text-slate-600 hover:text-blue-800 hover:bg-slate-50 transition-colors"
                active-class="bg-blue-50 text-blue-800"
            >
              {{ $t('sidebar.manage_questions') }}
            </RouterLink>
          </div>

        </nav>
      </div>

      <div class="p-6 flex flex-col gap-4 border-t border-slate-100">
        <div class="flex flex-col gap-2 font-medium text-slate-400 text-xs px-3">
          <a href="#" class="hover:text-slate-700 transition-colors">{{ $t('sidebar.support') }}</a>
          <a href="#" class="hover:text-slate-700 transition-colors">{{ $t('sidebar.terms') }}</a>
        </div>

        <div class="flex gap-2 w-full mt-2">
          <button
              @click="router.push('/dashboard/profile')"
              class="flex-1 flex items-center justify-between px-3 py-2.5 bg-white border border-slate-200 rounded-lg text-slate-700 hover:border-slate-300 hover:bg-slate-50 transition-colors group"
          >
            <div class="flex items-center gap-2">
              <div class="w-6 h-6 rounded-full bg-blue-100 text-blue-800 flex items-center justify-center font-bold text-xs">
                {{ user?.name?.charAt(0).toUpperCase() || 'U' }}
              </div>
              <span class="text-xs font-bold truncate max-w-[80px]">{{ user?.name || $t('sidebar.profile') }}</span>
            </div>
          </button>

          <button
              @click="logout"
              class="flex items-center justify-center px-3 py-2.5 bg-white border border-slate-200 rounded-lg text-slate-500 hover:text-red-600 hover:border-red-200 hover:bg-red-50 transition-colors"
              :title="$t('sidebar.logout_tooltip')"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15m3 0l3-3m0 0l-3-3m3 3H9" />
            </svg>
          </button>
        </div>
      </div>
    </aside>

    <main class="flex-1 h-screen overflow-y-auto p-8">
      <RouterView :user="user" />
    </main>
  </div>
</template>