<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const username = ref('')
const password = ref('')
const error = ref(null)
const emit = defineEmits(['login-success', 'close'])
const router = useRouter()

async function login(){

    if (!username.value || !password.value) {
      error.value = 'Cant login with no credentials'
      return
    }

    try {
        const response = await axios.post('http://localhost:8080/auth/login', {
            username: username.value,
            password: password.value
        })
        console.log(response.data)
        localStorage.setItem('user', JSON.stringify(response.data))

        emit('login-success', response.data)
        emit('close')

        router.push('/dashboard')
    } catch (e) {
        error.value = e.response?.data?.message || "Login failed"
    }
}

</script>

<template>
  <div v-on:click.self="$emit('close')" class="fixed inset-0 flex items-center justify-center bg-black/50 z-50">
    <div class="bg-neutral-900 text-black p-6 rounded-lg w-80 border">

      <div class="flex align-middle justify-center">
        <h2 class="text-white font-titulo text-xl mb-4">Login</h2>
      </div>

      <div v-if="error" class="flex items-center gap-2 bg-red-950 border border-red-400 rounded-xl p-2 mb-4 text-xs text-red-400">
        <!-- https://heroicons.com/outline -->
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z" />
        </svg>

        {{ error }}
      </div>

      <div class="pb-4">
        <input v-model="username" placeholder="Username" class="w-full mb-2 p-2 border bg-neutral-800 text-white rounded-xl" />
        <input v-model="password" type="password" placeholder="Password" class="w-full mb-2 p-2 border bg-neutral-800 text-white rounded-xl" />
      </div>

      <div class="flex justify-center w-full">
        <button v-on:click="login" class="text-white w-1/2 p-3 rounded-xl justify-center flex text-xs font-normal transition-colors border hover:bg-white hover:text-black duration-300 hover:cursor-pointer">
            Sign In
        </button>
      </div>
    </div>
  </div>
</template>