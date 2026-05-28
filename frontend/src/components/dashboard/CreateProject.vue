<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { successNotification } from "../alerts/notifications.js";

const title = ref('')
const description = ref('')
const email = ref('')
const error = ref(null)
const emit = defineEmits(['close'])
const router = useRouter()

const props = defineProps({
  user: { type: Object, required: true }
})

async function create() {
  if (!title.value || !description.value || !email.value) {
    error.value = 'Must specify all fields';
    return;
  }

  try{
    const response = await fetch('http://localhost:8080/project/create', {
      method: 'POST',
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${props.user.token}`
      },
      body: JSON.stringify({
        title: title.value,
        description: description.value,
        email: email.value
      })
    })

    const output = await response.json();

    if (!response.ok) {
      if (output.message){
        error.value = output.message;
      } else {
        error.value = "Failed to create Project";
      }
      return;
    }

    // If success
    successNotification(output.message)
    emit('close')
    router.push({ path: '/dashboard/dashboard-main' })

  } catch (err) {

    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Error creating project";
    }
  }
}

</script>

<template>
  <div class="flex-1 h-full flex flex-col gap-4 pl-4">
    <div class="text-white bg-neutral-800 w-full h-full rounded-2xl flex flex-col gap-5 p-5">
      <div class="flex justify-center">
        <div class="font-normal w-full bg-">
          Create Project
        </div>

        <button v-on:click="router.push('dashboard-main')" class="cursor-pointer">
          <!-- https://heroicons.com/ -->
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <div class="font-normal w-full h-full  rounded-2xl gap-6">
        <div class="text-sm gap-2.5">
          <div class="p-2 text-white/90">Client Info</div>
          <input v-model="email" placeholder="Email" class="w-full mb-2 p-2 border bg-neutral-800 text-white/50 rounded-xl" />
          <div class="p-2 text-white/90">Project Info</div>
          <input v-model="title" placeholder="Title" class="w-full mb-2 p-2 border bg-neutral-800 text-white/50 rounded-xl" />
          <input v-model="description" placeholder="Description" class="w-full mb-2 p-2 border bg-neutral-800 text-white/50 rounded-xl" />
        </div>

        <div v-if="error" class="flex items-center gap-2 bg-red-950 border border-red-400 rounded-xl p-2 mb-4 text-xs text-red-400">
          <!-- https://heroicons.com/outline -->
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z" />
          </svg>

          {{ error }}
        </div>

        <div class="flex justify-center w-full">
          <button v-on:click="create" class="text-white w-3/10 p-3 rounded-xl justify-center flex text-xs font-normal transition-colors border hover:bg-white hover:text-black duration-300 hover:cursor-pointer">
            Submit
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>