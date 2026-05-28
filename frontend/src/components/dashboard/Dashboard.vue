<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { RouterView } from 'vue-router'

const router = useRouter()

// To check user login
const auth = localStorage.getItem('user')
if (!auth) router.push("/")
const user = ref(JSON.parse(auth))

function logout(){
  localStorage.removeItem('user')
  router.push('/')
}
</script>

<template>
  <div class="bg-neutral-900 w-screen h-screen flex p-4">

    <div class="bg-neutral-800 w-1/4 h-full rounded-2xl p-6 flex flex-col justify-between">

      <div class="flex flex-col gap-10">
        <h1 class="text-white font-titulo text-xl w-full">GETTER</h1>

        <div class="flex flex-col gap-4 font-normal text-zinc-400 text-sm tracking-wider">
          <RouterLink to="/dashboard/dashboard-main" class="hover:text-white transition-colors">Dashboard</RouterLink>
          <RouterLink to="/dashboard/create-project" class="hover:text-white transition-colors">Projects</RouterLink>
          <RouterLink to="/dashboard/activate-users" href="#" v-if="user.role === 'ADMIN'" class="hover:text-white transition-colors">Activate Users</RouterLink>
        </div>
      </div>


      <div class="flex flex-col gap-6">
        <div class="flex flex-col gap-2 font-normal text-zinc-500 text-xs tracking-wide pl-1">
          <a href="#" class="hover:text-white transition-colors">Support</a>
          <a href="#" class="hover:text-white transition-colors">Terms & Conditions</a>
        </div>

        <div class="flex w-full gap-2">
          <button v-on:click="router.push('/dashboard/profile')" class="text-white w-full p-4 rounded-xl flex items-center justify-between text-xs font-normal transition-colors border hover:bg-neutral-900 duration-300 hover:cursor-pointer">
            <div class="flex items-center gap-3">

              <!-- https://heroicons.com/ -->
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17.982 18.725A7.488 7.488 0 0 0 12 15.75a7.488 7.488 0 0 0-5.982 2.975m11.963 0a9 9 0 1 0-11.963 0m11.963 0A8.966 8.966 0 0 1 12 21a8.966 8.966 0 0 1-5.982-2.275M15 9.75a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
              </svg>

              My Profile

            </div>

            <!-- https://heroicons.com/ -->
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="m8.25 4.5 7.5 7.5-7.5 7.5" />
            </svg>
          </button>

          <button v-on:click="logout" class="text-white p-4 rounded-xl transition-colors border hover:bg-red-600 duration-300 hover:cursor-pointer">
            <!-- https://heroicons.com/ -->
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="M8.25 9V5.25A2.25 2.25 0 0 1 10.5 3h6a2.25 2.25 0 0 1 2.25 2.25v13.5A2.25 2.25 0 0 1 16.5 21h-6a2.25 2.25 0 0 1-2.25-2.25V15m-3 0-3-3m0 0 3-3m-3 3H15" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <RouterView :user="user" />

  </div>
</template>

<style>
body {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}


</style>