<script setup>
import { ref } from 'vue'
import Login from './auth/Login.vue'
import Signup from "./auth/Signup.vue";

const showLoginModal = ref(false)
const showSignupModal = ref(false)
const loggedUser = ref(null)
</script>


<template>
  <div class="min-h-screen w-full flex flex-col bg-gradient-to-br from-neutral-900 via-neutral-800 to-neutral-700 text-gray-200 font-sans relative overflow-hidden">
    <nav class="flex justify-between items-center p-6 md:p-8 uppercase text-sm tracking-widest fixed top-0 left-0 w-full z-50">
      <div class="font-titulo z-50 relative">Getter</div>

      <div class="md:flex gap-8">
        <a href="#" v-on:click="showLoginModal = true" class="hover:text-white font-mono">Login</a>
        <a href="#" v-on:click="showSignupModal = true" class="hover:text-white font-mono font-bold">Sign In</a>
      </div>

      <Login v-if="showLoginModal" v-on:close="showLoginModal = false" @login-success="loggedUser = $event"/>
      <Signup v-if="showSignupModal" v-on:close="showSignupModal = false" @signup-success="loggedUser = $event"/>
    </nav>

    <div class="hidden md:block absolute left-8 md:left-24 top-[20%] font-mono text-xs md:text-sm text-gray-200 z-10">
      &lt;body&gt; <br>
      &nbsp;&nbsp;&lt;div style="color: #FFFFFF; font-size: 20px;"&gt; <br><br>
      &nbsp;&nbsp;&nbsp;&nbsp;<b>System Status: <a class="text-amber-300"> In Progess</a> </b>   &lt;br&gt;<br><br>
      &nbsp;&nbsp;&lt;/div&gt; <br>
      &lt;/body&gt;
    </div>

    <div class="hidden md:block absolute top-[20%] right-[7%] font-mono md:text-sm rotate-15 w-1/4 blur-[1px]">
      <img src="../assets/desk.jpg" alt="pc photo" class="w-full h-auto rounded-lg shadow-lg">
    </div>

    <div class="flex-grow flex items-center justify-center text-center px-6 pt-20 pb-20">

      <h1 class="text-4xl sm:text-5xl md:text-6xl font-bold tracking-tighter mb-4 text-white z-10 text-shadow-[0_35px_35px_rgb(0_0_0_/_0.25)]">
        LET'S TALK ABOUT YOUR PROJECT
      </h1>

    </div>

    <footer class="flex justify-between items-center p-6 md:p-8 uppercase tracking-widest fixed bottom-0 left-0 w-full">

      <div class="text-xs md:text-sm font-mono">@2026</div>
      <div class="text-xs md:text-sm font-bold font-mono hidden md:block">We Define. You Submit. Tech Team Build.</div>
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