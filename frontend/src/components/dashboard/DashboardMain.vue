<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import {successNotification} from "../alerts/notifications.js";

const router = useRouter()
const projects = ref([])
const feedbacks = ref([])
const error = ref(null)

const props = defineProps({
  user:{ type: Object, required: true}
})

async function fetchProjects() {
  try{
    const response = await fetch('http://localhost:8080/project/getProjects', {
      method: 'GET',
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${props.user.token}`
      }
    })

    const output = await response.json();

    if (!response.ok) {

      if (output.message === null){
        error.value = "Couldn't Fetch Projects!";
      } else {
        error.value = output.message;
      }

      return;
    }

    // If success
    projects.value = output;
  } catch (err) {
    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Error creating project";
    }
  }
}

onMounted( () => {
  fetchProjects();
});


</script>

<template>
  <div class="flex-1 h-full flex flex-col gap-4 pl-4">

    <div class="text-white  flex justify-between items-center py-2">
      <h2 class="font-normal text-lg pl-4">Hello
        <b v-if="props.user.role === 'CLIENT'" class="text-green-600">{{props.user.role}}</b>
        <b v-if="props.user.role === 'TECH_TEAM'" class="text-blue-700">{{props.user.role}}</b>
        <b v-if="props.user.role === 'ADMIN'" class="text-red-800">{{props.user.role}}</b>
        {{props.user.name}}
      </h2>

      <button v-if="props.user.role === 'ADMIN'" v-on:click="router.push('create-project')" class="bg-neutral-800 font-normal px-4 py-2 rounded-xl flex items-center gap-2 border hover:bg-white hover:text-zinc-800 hover:cursor-pointer hover:transition-colors duration-300">

        <!-- https://heroicons.com/ -->
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
        </svg>

        Create Project

      </button>
    </div>

    <div class="text-white bg-neutral-800 w-full h-1/2 rounded-2xl flex flex-col gap-5 p-5 max-h-[50%] ">

      <div class="font-normal w-full bg-">
        Projects
      </div>

      <!-- https://preline.co/docs/components/custom-scrollbar.html -->
      <div class="font-normal w-full h-full bg-neutral-700/50 rounded-2xl overflow-y-auto
          [&::-webkit-scrollbar]:w-2
          [&::-webkit-scrollbar-track]:rounded-full
          [&::-webkit-scrollbar-track]:bg-stone-100
          [&::-webkit-scrollbar-thumb]:rounded-full
          [&::-webkit-scrollbar-thumb]:bg-stone-300
          dark:[&::-webkit-scrollbar-track]:bg-neutral-700
          dark:[&::-webkit-scrollbar-thumb]:bg-neutral-500">

        <div v-if="projects.length === 0" class="flex font-titulo text-5xl text-white/50 w-full italic h-full justify-center items-center">
            No Projects Found
        </div>

        <div v-on:click="router.push({ name: 'AnswerProject', params: { projectId: project.id } })" v-for="project in projects" :key="project.id"
             class="w-full p-5 bg-white text-black rounded-2xl flex items-center justify-between gap-4 h-15 border hover:cursor-pointer">

          <div class="w-1/4 font-extrabold text-neutral-900 uppercase">
            {{ project.title }}
          </div>
          <div class="flex-1 text-neutral-600 font-normal max-w-[50%]">
            {{ project.description }}
          </div>
          <div class="w-1/4 flex justify-end items-center gap-1.5 font-bold text-sm text-amber-300">

            <!-- https://heroicons.com/ -->
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 6v6h4.5m4.5 0a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
            </svg>

            {{ project.status }}
          </div>

        </div>

      </div>
    </div>
    <div class="text-white bg-neutral-800 w-full h-1/2 rounded-2xl flex flex-col gap-5 p-5 max-h-[50%]">
      <div class="font-normal w-full">
        Feedbacks
      </div>

      <div class="font-normal w-full h-full bg-neutral-700/50 rounded-2xl flex flex-col gap-2">

        <div v-if="feedbacks.length === 0" class="flex font-titulo text-5xl text-white/50 w-full italic h-full justify-center items-center">
          No Feedbacks Found
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>

</style>