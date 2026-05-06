<script setup>
import { ref } from 'vue'
import axios from 'axios'

const username = ref('')
const password = ref('')
const name = ref('')
const email = ref('')
const error = ref(null)
const emit = defineEmits(['signup-success', 'close'])

const signup = async () => {
      try {
          const response = await axios.post('http://localhost:8080/auth/register', {
              name: name.value,
              username: username.value,
              email: email.value,
              password: password.value
          })

          console.log(response.data)

          // guarda user/token
          localStorage.setItem('user', JSON.stringify(response.data))

          emit('signup-success', response.data)

          emit('close')
    } catch (err) {
        console.log(err)
        // error.value = err.response?.data || "Signup failed"
    }
}

</script>

<template>
    <div class="fixed inset-0 flex items-center justify-center bg-black/50 z-50">
        <div class="bg-white text-black p-6 rounded-lg w-80">
            <h2 class="text-xl mb-4">Signup</h2>

            <input v-model="name" placeholder="Name" class="w-full mb-2 p-2 border" />
            <input v-model="username" placeholder="Username" class="w-full mb-2 p-2 border" />
            <input v-model="email" placeholder="Email" class="w-full mb-2 p-2 border" />
            <input v-model="password" type="password" placeholder="Password" class="w-full mb-2 p-2 border" />

            <button @click="signup" class="bg-black text-white px-4 py-2 w-full">
                Signup
            </button>

            <button @click="$emit('close')" class="mt-2 text-sm text-gray-500 w-full">
                Cancel
            </button>

            <p v-if="error" class="text-red-500 mt-2">{{ error }}</p>
        </div>
    </div>
</template>