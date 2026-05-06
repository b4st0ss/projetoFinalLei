<script setup>
import { ref } from 'vue'
import Login from './Login.vue'
import Signup from "./Signup.vue";

const isMenuOpen = ref(false)
const showLoginModal = ref(false)
const showSignupModal = ref(false)
const loggedUser = ref(null)

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}
</script>


<template>
  <div class="min-h-screen w-full flex flex-col bg-gradient-to-br from-[#212121] via-[#4d4d4d] to-[#8a8a8a] text-gray-200 font-sans relative overflow-hidden">
    <nav class="flex justify-between items-center p-6 md:p-8 uppercase text-sm tracking-widest fixed top-0 left-0 w-full z-50">
      <div class="font-bold z-50 relative">Getter</div>

      <div class="hidden md:flex gap-8">
        <a href="#" class="hover:text-white font-mono">About the Dev</a>
        <a href="#" @click="showLoginModal = true" class="hover:text-white font-mono">Login</a>
        <a href="#" @click="showSignupModal = true" class="hover:text-white font-mono font-bold">Sign In</a>
      </div>

      <Login v-if="showLoginModal"
             @close="showLoginModal = false"
             @login-success="loggedUser = $event"
      />

      <Signup v-if="showSignupModal"
              @close="showSignupModal = false"
              @signup-success="loggedUser = $event"
      />

      <button
          @click="toggleMenu"
          class="md:hidden z-50 relative text-gray-200 hover:text-white focus:outline-none"
      >
        <svg v-if="!isMenuOpen" class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
        </svg>
        <svg v-else class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
        </svg>
      </button>

      <div
          v-if="isMenuOpen"
          class="absolute top-0 left-0 w-full h-screen bg-[#212121]/95 backdrop-blur-sm flex flex-col items-center justify-center gap-10 md:hidden z-40 transition-all duration-300"
      >
        <a href="#" @click="toggleMenu" class="text-xl hover:text-white font-mono tracking-widest">About the Dev</a>
        <a href="#" @click="toggleMenu,showLoginModal = true" class="text-xl hover:text-white font-mono tracking-widest">Login</a>
        <a href="#" @click="toggleMenu,showSignupModal = true" class="text-xl hover:text-white font-mono font-bold tracking-widest">Sign In</a>
      </div>
    </nav>

    <div class="hidden md:block absolute left-8 md:left-24 top-[20%] font-mono text-xs md:text-sm text-gray-200 z-10">
      &lt;body&gt; <br>
      &nbsp;&nbsp;&lt;div style="color: #FFFFFF; font-size: 20px;"&gt; <br>
      &nbsp;&nbsp;&nbsp;&nbsp;<b>System Status: <a class="text-amber-300"> In Progess</a> </b>   &lt;br&gt;<br><br>
      &nbsp;&nbsp;&nbsp;&nbsp;<b>Active Projects: 0 </b><br>
      &nbsp;&nbsp;&nbsp;&nbsp;<b>Devs Online: 0</b> <br>
      &nbsp;&nbsp;&lt;/div&gt; <br>
      &lt;/body&gt;
    </div>

    <div class="hidden md:block absolute top-[20%] right-[7%] font-mono md:text-sm rotate-15 w-1/4 blur-[1px]">
      <img src="../assets/desk.jpg" alt="pc photo" class="w-full h-auto rounded-lg shadow-lg">
    </div>

    <div class="flex-grow flex items-center justify-center text-center px-6 pt-20 pb-20">
      <h1 class="text-4xl sm:text-5xl md:text-6xl font-bold tracking-tighter mb-4 text-white z-10 text-shadow-[0_35px_35px_rgb(0_0_0_/_0.25)] break-words">
        LET'S TALK ABOUT YOUR PROJECT
      </h1>
    </div>

    <footer class="flex justify-between items-center p-6 md:p-8 text-[10px] uppercase tracking-widest fixed bottom-0 left-0 w-full z-50">
      <div class="text-xs md:text-sm font-mono">@2026</div>
      <div class="text-xs md:text-sm font-bold font-mono hidden md:block">We Define. You Submit. Devs Build.</div>
      <div class="text-xs md:text-sm font-mono text-right">diogorbbastos@gmail.com</div>
    </footer>
  </div>
</template>



<style>
body {
  font-family: sans-serif;
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>