<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import ErrorAlert from "../alerts/ErrorAlert.vue";

const username = ref('')
const password = ref('')
const error = ref(null)
const emit = defineEmits(['login-success', 'close'])
const router = useRouter()

async function login() {
  if (!username.value || !password.value) {
    error.value = 'Cant login with no credentials';
    return;
  }

  try{
    const response = await fetch('http://localhost:8080/auth/login', {
      method: 'POST',
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username: username.value,
        password: password.value
      })
    })

    const output = await response.json();


    if (!response.ok) {
      if (output.message){
        error.value = output.message;
      } else {
        error.value = "Login failed";
      }
      return;
    }


    // If success
    localStorage.setItem('user', JSON.stringify(output));
    emit('login-success', output);
    emit('close');
    router.push('/dashboard');

  } catch (err) {

    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Login failed";
    }
  }
}

</script>

<template>
  <div v-on:click.self="$emit('close')" class="fixed inset-0 flex items-center justify-center bg-black/50 z-50">
    <div class="bg-neutral-900 text-black p-6 rounded-lg w-80 border">

      <div class="flex align-middle justify-center">
        <h2 class="text-white font-titulo text-xl mb-4">Login</h2>
      </div>

      <ErrorAlert v-if="error" :message="error" />

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