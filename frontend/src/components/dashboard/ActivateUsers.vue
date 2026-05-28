<script setup>
import { useRouter } from 'vue-router'
import {onMounted, ref} from 'vue'
import ErrorAlert from "../alerts/ErrorAlert.vue";
import SuccessAlert from "../alerts/SuccessAlert.vue";

const props = defineProps({
  user: { type: Object, required: true }
})

const error = ref(null)
const successMessage = ref("")
const users = ref([])
const router = useRouter()


async function fetchUsers() {

  console.log(props.user)
  console.log(props.user.token)

  try{
    const response = await fetch('http://localhost:8080/user/all', {
      method: 'GET',
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${props.user.token}`
      }
    });

    const output = await response.json();

    if (!response.ok) {

      if (output.message === null){
        error.value = "Error fetching Users";
      } else {
        error.value = output.message;
      }

      return;
    }

    // If success
    users.value = output;

  } catch (err) {

    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Error fetching Users";
    }
  }
}


async function changeStatus(id, nStatus){

  try{
    const response = await fetch('http://localhost:8080/user/activate', {
      method: 'PUT',
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${props.user.token}`
      },
      body: JSON.stringify({
        userId: id,
        status: nStatus
      })
    });

    const output = await response.json();

    if (!response.ok) {

      if (output.message === null){
        error.value = "Failed to update status";
      } else {
        error.value = output.message;
      }

      return;
    }

    // If success
    successMessage.value = "Success Updating User";
    await fetchUsers();

  } catch (err) {

    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Failed to update status";
    }
  }
}

onMounted( () => {
  fetchUsers();
});

</script>

<template>
  <div class="flex-1 h-full flex flex-col gap-4 pl-4">
    <div class="text-white bg-neutral-800 w-full h-full rounded-2xl flex flex-col gap-5 p-5">
      <div class="flex justify-center">
        <div class="font-normal w-full bg-">
          Activate Users
        </div>

        <button v-on:click="router.push('dashboard-main')" class="cursor-pointer">
          <!-- https://heroicons.com/ -->
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <ErrorAlert v-if="error" :message="error" />
      <SuccessAlert v-if="successMessage" :message="successMessage" />

      <!-- https://preline.co/docs/components/custom-scrollbar.html -->
      <div class="font-normal w-full h-full bg-neutral-700/50 rounded-2xl overflow-y-auto
          [&::-webkit-scrollbar]:w-2
          [&::-webkit-scrollbar-track]:rounded-full
          [&::-webkit-scrollbar-track]:bg-stone-100
          [&::-webkit-scrollbar-thumb]:rounded-full
          [&::-webkit-scrollbar-thumb]:bg-stone-300
          dark:[&::-webkit-scrollbar-track]:bg-neutral-700
          dark:[&::-webkit-scrollbar-thumb]:bg-neutral-500">

        <div v-for="user in users" :key="user.id" class="w-full p-5 bg-white text-black rounded-2xl flex items-center justify-between gap-4 h-10 border hover:cursor-pointer">
          <div class="flex flex-box">
            {{user.username}} | {{user.email}} | {{user.role}}
          </div>
          <div class="flex gap-2">
            <button v-on:click="changeStatus(user.id,true)"  :disabled="user.status" class="hover: cursor-pointer hover:bg">
              <div v-if="!user.status" class="text-emerald-500">
                Activate
              </div>
              <div v-else>
                Activate
              </div>
            </button>
            <button v-on:click="changeStatus(user.id,false)"  :disabled="!user.status" class=" hover: cursor-pointer" >
              <div v-if="user.status" class="text-red-600">
                Deactivate
              </div>
              <div v-else>
                Deactivate
              </div>
            </button>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>

</style>