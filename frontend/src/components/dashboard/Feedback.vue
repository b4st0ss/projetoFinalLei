<script setup>
import {onMounted, ref} from 'vue'
import { useRouter } from 'vue-router'
import ErrorAlert from "../alerts/ErrorAlert.vue";

const props = defineProps({
  user: { type: Object, required: true },
  object: { type: Object, required: true }
})

const feedbacks = ref([])
const message = ref('')
const error = ref(null)
const emit = defineEmits(['close'])

async function fetchFeedbacks(){
  error.value = null;

  try {
    let end = "";

    if (props.object.type === 'answer') {
      end = `http://localhost:8080/feedback/answer/${props.object.id}`
    } else {
      end = `http://localhost:8080/feedback/requirement/${props.object.id}`
    }

    const response = await fetch(end, {
      method: 'GET',
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${props.user.token}`
      },
    });

    const output = await response.json();

    if (!response.ok) {
      if (output.message) {
        error.value = output.message;
      } else {
        error.value = "Failed to fetch feedbacks";
      }
      return;
    }

    feedbacks.value = output;

  } catch (err) {
    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Failed to fetch feedbacks";
    }
  }
}

async function send(){
  error.value = null;

  if (message.value.trim() === ""){
    error.value = "Can't give empty feedback";
    return;
  }

  try{

    const info = {
      message: message.value,
      answerId: null,
      requirementId: null
    }

    if (props.object.type === 'answer') {
      info.answerId = props.object.id;
    } else if (props.object.type === 'requirement') {
      info.requirementId = props.object.id;
    }

    const response = await fetch('http://localhost:8080/feedback/create', {
      method: 'POST',
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${props.user.token}`
      },
      body: JSON.stringify(info)
    })

    const output = await response.json();

    if (!response.ok) {
      if (output.message){
        error.value = output.message;
      } else {
        error.value = "Failed to submit feedback";
      }
      return;
    }

    // refresh
    message.value = "";
    await fetchFeedbacks();

  } catch (err) {

    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Failed to submit feedback";
    }

  }
}

onMounted(() => {
  fetchFeedbacks();
});

</script>

<template>
  <div class="fixed inset-0 flex items-center justify-center bg-neutral-900/10 z-50">
    <div class="bg-neutral-900 text-black p-6 rounded-lg w-120 border">

      <div class="flex items-center justify-between mb-4">
        <h2 class="text-white font-titulo text-xl mb-4">Feedbacks</h2>

        <button v-on:click="emit('close')" class="text-neutral-400 hover:text-white cursor-pointer">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <ErrorAlert v-if="error" :message="error" />

      <!-- https://preline.co/docs/components/custom-scrollbar.html -->
      <div class="bg-neutral-800 w-full h-100 rounded-2xl mb-4 text-white overflow-y-auto
          [&::-webkit-scrollbar]:w-2
          [&::-webkit-scrollbar-track]:rounded-full
          [&::-webkit-scrollbar-track]:bg-stone-100
          [&::-webkit-scrollbar-thumb]:rounded-full
          [&::-webkit-scrollbar-thumb]:bg-stone-300
          dark:[&::-webkit-scrollbar-track]:bg-neutral-700
          dark:[&::-webkit-scrollbar-thumb]:bg-neutral-500">
        <div v-for="feedback in feedbacks" :key="feedback.id" class="p-3">
          <div v-if="feedback.role === 'TECH_TEAM'" class="flex flex-box text-sm text-blue-700">
            <!-- https://heroicons.com/ -->
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z" />
            </svg>
            <div class="pt-1 ">
              {{feedback.username}} <div class=" text-sm"> {{feedback.creation_data}}</div>
            </div>
          </div>
          <div v-if="feedback.role === 'CLIENT'" class="flex flex-box text-sm text-green-600">
            <!-- https://heroicons.com/ -->
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z" />
            </svg>
            <div class="pt-1 ">
              {{feedbacks.username}}
            </div>
          </div>
          <div v-if="feedback.role === 'ADMIN'" class="flex flex-box text-sm text-red-800">
            <!-- https://heroicons.com/ -->
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z" />
            </svg>
            <div class="pt-1 ">
              {{feedbacks.username}}
            </div>
          </div>
          <div class="font-stretch-normal">
            {{feedback.message}}
          </div>
        </div>
      </div>

      <div class="flex flex-box text-white rounded-xl border">
        <input v-model="message" placeholder="Write your feedback" class="w-full p-2 bg-transparent  text-sm outline-none border-none" />

        <button v-on:click="send" class="cursor-pointer p-1">
          <!-- https://heroicons.com/ -->
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 12 3.269 3.125A59.769 59.769 0 0 1 21.485 12 59.768 59.768 0 0 1 3.27 20.875L5.999 12Zm0 0h7.5" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>