<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import Login from './auth/Login.vue'
import Signup from './auth/Signup.vue'

const { locale } = useI18n()

const showLogin = ref(false)
const showSignup = ref(false)

function openSignup() {
  showLogin.value = false
  showSignup.value = true
}

function openLogin() {
  showSignup.value = false
  showLogin.value = true
}

// Small function to change language
function toggleLanguage() {
  locale.value = locale.value === 'pt' ? 'en' : 'pt'
}
</script>

<template>
  <div class="flex flex-col min-h-screen w-full bg-slate-50 text-slate-900 font-sans antialiased selection:bg-blue-200">

    <!-- Navbar -->
    <header class="sticky top-0 z-40 border-b border-slate-200 bg-white/80 backdrop-blur-md">
      <div class="mx-auto flex max-w-6xl items-center justify-between px-6 py-4 md:px-10">
        <div class="flex items-center gap-3 select-none">
          <span class="text-lg font-bold tracking-tight text-slate-900">Getter</span>
        </div>

        <nav class="flex items-center gap-3">
          <!-- Test button to change -->
          <button
              @click="toggleLanguage"
              class="text-xs font-bold text-slate-400 hover:text-blue-800 uppercase px-2"
          >
            {{ locale }}
          </button>

          <button
              type="button"
              class="text-sm font-semibold text-slate-600 transition-colors hover:text-slate-900 px-2 py-2"
              @click="openSignup"
          >
            {{ $t('auth.register') }}
          </button>
          <button
              type="button"
              class="rounded-lg bg-blue-800 px-4 py-2 text-sm font-semibold text-white shadow-sm transition-all hover:bg-blue-700 active:scale-[0.98]"
              @click="openLogin"
          >
            {{ $t('auth.login_btn') }}
          </button>
        </nav>
      </div>
    </header>

    <main class="flex-1 w-full mx-auto max-w-6xl px-6 md:px-10">
      <!-- Hero Section -->
      <section class="grid items-center gap-12 py-20 md:grid-cols-2 md:py-28">
        <div>
          <h1 class="text-4xl font-extrabold leading-[1.1] tracking-tight text-slate-900 sm:text-5xl md:text-6xl text-balance">
            {{ $t('home.title') }}
            <br />
            <span class="text-blue-800">{{ $t('home.title_highlight') }}</span>
          </h1>

          <p class="mt-6 max-w-md text-lg leading-relaxed text-slate-600 text-balance">
            {{ $t('home.subtitle') }}
          </p>
        </div>

        <!-- Hero Image -->
        <div class="rounded-2xl border border-slate-200 bg-white p-2 shadow-xl shadow-slate-200/50">
          <img
              src="../assets/desk.jpg"
              alt="Estação de trabalho com computador"
              class="aspect-[4/3] w-full rounded-xl object-cover bg-slate-100"
          />
        </div>
      </section>
    </main>

    <!-- Footer agora empurrado para o fundo pelo flex-1 do main -->
    <footer class="border-t border-slate-200 bg-white mt-auto">
      <div class="mx-auto flex max-w-6xl flex-col items-center justify-between gap-4 px-6 py-8 text-sm text-slate-500 sm:flex-row md:px-10">
        <span class="font-medium">&copy; {{ new Date().getFullYear() }}</span>

        <span class="hidden font-semibold uppercase tracking-wider text-slate-400 sm:block text-xs">
          We define. You submit. Tech team builds.
        </span>

        <a href="mailto:diogorbbastos@gmail.com" class="font-medium transition-colors hover:text-blue-800">
          diogorbbastos@gmail.com
        </a>
      </div>
    </footer>

    <!-- Modais -->
    <Login
        v-if="showLogin"
        @close="showLogin = false"
        @switch-to-signup="openSignup"
    />

    <Signup
        v-if="showSignup"
        @close="showSignup = false"
        @switch-to-login="openLogin"
    />
  </div>
</template>