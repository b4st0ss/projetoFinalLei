<script setup>
import { ref, onMounted } from 'vue'
import ErrorAlert from "../alerts/ErrorAlert.vue";
import SuccessAlert from "../alerts/SuccessAlert.vue";

const props = defineProps({
  user: { type: Object, required: true },
  projectId: { type: String, required:true }
})

const error = ref(null)
const selectedTechs = ref([])
const successMessage = ref("")
const techEmail = ref("")
const emit = defineEmits(['close'])


async function fetchAssociated() {
  try{
    const response = await fetch(`http://localhost:8080/project/${props.projectId}/getTechs`,{
      method: 'GET',
      headers: {
        "Authorization": `Bearer ${props.user.token}`
      }
    })

    const output = await response.json();

    if (!response.ok) {
      if (output.message){
        error.value = output.message;
      } else {
        error.value = "Failed to fetch Associated";
      }
      return;
    }

    selectedTechs.value = output;

  } catch (err) {

    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Failed to fetch Associated";
    }
  }
}

async function addTech(){

  error.value = null;

  // Check Inputs
  if (techEmail.value === null){
    error.value = "Need to Insert The Email!";
    return;
  }

  try{
    const response = await fetch('http://localhost:8080/project/associateTech', {
      method: 'POST',
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${props.user.token}`
      },
      body: JSON.stringify({
        projectId: Number(props.projectId),
        email: techEmail.value
      })
    })

    const output = await response.json();

    if (!response.ok) {
      if (response.message){
        error.value = "Failed to associate Tech";
      } else {
        error.value = response.message;
      }
      return;
    }

    // refesh
    successMessage.value = output.message;
    techEmail.value = '';
    await fetchAssociated();

  } catch (err) {

    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Failed to associate Tech";
    }

  }

}


async function removeTech(id){
  error.value = null;

  try{
    const response = await fetch('http://localhost:8080/project/disassociateTech', {
      method: 'DELETE',
      headers: {
        "Authorization": `Bearer ${props.user.token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        projectId: Number(props.projectId),
        techId: Number(id)
      })
    })

    const output = await response.json();

    if (!response.ok) {
      if (output.message){
        error.value = output.message;
      } else {
        error.value = "Failed to remove Tech";
      }
      return;
    }

    successMessage.value = output.message;
    await fetchAssociated();

  } catch (err) {

    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Failed to remove Tech";
    }
  }

}

onMounted(() => {
  fetchAssociated();
})

</script>

<template>
  <div class="fixed inset-0 flex items-center justify-center bg-neutral-950/70 z-50 combined-blur-sm">
    <div class="bg-neutral-900 text-black p-6 rounded-lg w-120 border ">

      <div class="flex items-center justify-between mb-4">
        <h2 class="text-white font-titulo text-xl mb-4">Associate Tech</h2>

        <button v-on:click="emit('close')" class="text-neutral-400 hover:text-white cursor-pointer">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <ErrorAlert v-if="error" :message="error" />
      <SuccessAlert v-if="successMessage" :message="successMessage" />

      <!-- https://preline.co/docs/components/custom-scrollbar.html -->
      <div class="bg-neutral-800 w-full h-40 rounded-2xl mb-4 text-white overflow-y-auto
          [&::-webkit-scrollbar]:w-2
          [&::-webkit-scrollbar-track]:rounded-full
          [&::-webkit-scrollbar-track]:bg-stone-100
          [&::-webkit-scrollbar-thumb]:rounded-full
          [&::-webkit-scrollbar-thumb]:bg-stone-300
          dark:[&::-webkit-scrollbar-track]:bg-neutral-700
          dark:[&::-webkit-scrollbar-thumb]:bg-neutral-500">

        <div class="p-3 flex flex-col gap-2">

          <div v-for="tech in selectedTechs" :key="tech.id" class="flex items-center justify-between bg-gray-700/70 rounded-2xl">
              {{tech.name}}
              {{tech.email}}

            <button v-on:click="removeTech(tech.id)" class="cursor-pointer">
              <!-- https://heroicons.com/ -->
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0" />
              </svg>
            </button>
          </div>

        </div>
      </div>


      <div class="flex flex-box text-white rounded-xl border">
        <input v-model="techEmail" placeholder="Tech Email" class="w-full p-2 bg-transparent  text-sm outline-none border-none" />

        <button v-on:click="addTech" class="cursor-pointer p-1">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 12 3.269 3.125A59.769 59.769 0 0 1 21.485 12 59.768 59.768 0 0 1 3.27 20.875L5.999 12Zm0 0h7.5" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>