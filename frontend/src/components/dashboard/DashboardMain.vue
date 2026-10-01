<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { useI18n } from 'vue-i18n'
import { projectApi } from '../../api/project.js'

const router = useRouter()
const store = useStore()
const { t } = useI18n()

const props = defineProps({
  user: { type: Object, required: true }
})

const projects = ref([])
const feedbacks = ref([])
const loading = ref(true)
const error = ref(null)

const token = computed(() => store.getters['auth/token'])

async function fetchProjects() {
  loading.value = true
  error.value = null

  try {
    projects.value = await projectApi.getAll(token.value)
  } catch (e) {
    error.value = e.message || t('dashboard.err_load_projects')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchProjects()
})
</script>

<template>
  <div class="flex flex-col gap-6 w-full max-w-7xl mx-auto">

    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
      <div>
        <h2 class="text-xl font-bold text-slate-900 tracking-tight flex items-center gap-3">
          {{ $t('dashboard.hello') }} {{ props.user?.name || $t('dashboard.fallback_user') }}

          <span v-if="props.user?.role === 'ADMIN'" class="px-2.5 py-1 rounded-md bg-red-50 border border-red-100 text-red-700 text-[10px] font-bold uppercase tracking-wider">
            Admin
          </span>
          <span v-else-if="props.user?.role === 'TECH_TEAM'" class="px-2.5 py-1 rounded-md bg-blue-50 border border-blue-100 text-blue-700 text-[10px] font-bold uppercase tracking-wider">
            Tech Team
          </span>
          <span v-else-if="props.user?.role === 'CLIENT'" class="px-2.5 py-1 rounded-md bg-green-50 border border-green-100 text-green-700 text-[10px] font-bold uppercase tracking-wider">
            Client
          </span>
        </h2>
      </div>

      <button
          v-if="props.user?.role === 'ADMIN'"
          @click="router.push('create-project')"
          class="bg-blue-800 text-white font-semibold px-5 py-2.5 rounded-lg flex items-center gap-2 hover:bg-blue-700 active:scale-[0.98] transition-all shadow-sm"
      >
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
        </svg>
        {{ $t('dashboard.new_project') }}
      </button>
    </div>

    <div class="flex flex-col gap-6">

      <section class="bg-white border border-slate-200 rounded-2xl shadow-sm flex flex-col overflow-hidden">
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <h3 class="font-bold text-slate-800">{{ $t('dashboard.recent_projects') }}</h3>
          <span class="text-xs font-semibold text-slate-500 bg-slate-100 px-2 py-1 rounded-md">
            {{ projects.length }}
          </span>
        </div>

        <div class="flex-1 max-h-[400px] overflow-y-auto custom-scrollbar">

          <div v-if="loading" class="flex flex-col items-center justify-center py-12 text-slate-400">
            <svg class="animate-spin h-8 w-8 text-blue-800 mb-4" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
            </svg>
            <p class="text-sm font-medium">{{ $t('dashboard.loading_projects') }}</p>
          </div>

          <div v-else-if="projects.length === 0" class="flex flex-col items-center justify-center py-16 px-4 text-center">
            <div class="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mb-4 border border-slate-100">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-8 h-8 text-slate-300">
                <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 9.776c.112-.017.227-.026.344-.026h15.812c.117 0 .232.009.344.026m-16.5 0a2.25 2.25 0 00-1.883 2.542l.857 6a2.25 2.25 0 002.227 1.932H19.05a2.25 2.25 0 002.227-1.932l.857-6a2.25 2.25 0 00-1.883-2.542m-16.5 0V6A2.25 2.25 0 016 3.75h3.879a1.5 1.5 0 011.06.44l2.122 2.12a1.5 1.5 0 001.06.44H18A2.25 2.25 0 0120.25 9v.776" />
              </svg>
            </div>
            <p class="text-slate-600 font-medium">{{ $t('dashboard.no_projects') }}</p>
            <p class="text-sm text-slate-400 mt-1 max-w-sm">{{ $t('dashboard.no_projects_desc') }}</p>
          </div>

          <div v-else class="divide-y divide-slate-100">
            <div
                v-for="project in projects"
                :key="project.id"
                @click="router.push({ name: 'AnswerProject', params: { projectId: project.id } })"
                class="p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:bg-blue-50/50 cursor-pointer transition-colors group"
            >
              <div class="flex-1">
                <h4 class="font-bold text-slate-900 group-hover:text-blue-800 transition-colors">
                  {{ project.title }}
                </h4>
                <p class="text-slate-500 text-sm mt-1 line-clamp-1">
                  {{ project.description }}
                </p>
              </div>

              <div class="flex items-center gap-1.5 text-xs font-bold text-amber-600 bg-amber-50 border border-amber-100 px-3 py-1.5 rounded-md whitespace-nowrap">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-4 h-4">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm.75-13a.75.75 0 00-1.5 0v5c0 .414.336.75.75.75h4a.75.75 0 000-1.5h-3.25V5z" clip-rule="evenodd" />
                </svg>
                {{ project.status || $t('dashboard.status_pending') }}
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="bg-white border border-slate-200 rounded-2xl shadow-sm flex flex-col overflow-hidden">
        <div class="px-6 py-4 border-b border-slate-100 bg-slate-50/50">
          <h3 class="font-bold text-slate-800">{{ $t('dashboard.latest_feedbacks') }}</h3>
        </div>

        <div class="flex-1 min-h-[200px] flex flex-col items-center justify-center p-8 text-center">
          <div class="w-12 h-12 bg-slate-50 rounded-full flex items-center justify-center mb-3 border border-slate-100">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 text-slate-300">
              <path stroke-linecap="round" stroke-linejoin="round" d="M7.5 8.25h9m-9 3H12m-9.75 1.51c0 1.6 1.123 2.994 2.707 3.227 1.129.166 2.27.293 3.423.379.35.026.67.21.865.501L12 21l2.755-4.133a1.14 1.14 0 01.865-.501 48.172 48.172 0 003.423-.379c1.584-.233 2.707-1.626 2.707-3.228V6.741c0-1.602-1.123-2.995-2.707-3.228A48.394 48.394 0 0012 3c-2.392 0-4.744.175-7.043.513C3.373 3.746 2.25 5.14 2.25 6.741v6.018z" />
            </svg>
          </div>
          <p class="text-slate-500 font-medium text-sm">{{ $t('dashboard.no_feedbacks') }}</p>
        </div>
      </section>

    </div>
  </div>
</template>

<style scoped>


</style>