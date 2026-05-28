<script setup>
import { useRouter } from 'vue-router'
import {onMounted, ref} from 'vue'
import ErrorAlert from "../alerts/ErrorAlert.vue";
import SuccessAlert from "../alerts/SuccessAlert.vue";


const error = ref(null)
const successMessage = ref("")
const router = useRouter()
const profile = ref(null)

const props = defineProps({
  user: { type: Object, required: true }
})

async function fetchUser() {
  error.value = null;
  successMessage.value = "";

  try{
    const response = await fetch('http://localhost:8080/user/profile', {
      method: 'GET',
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${props.user.token}`
      }
    })

    const output = await response.json();

    if (!response.ok) {
      if (output.message){
        error.value = output.message;
      } else {
        error.value = "Coudn't find the User!";
      }
      return;
    }

    // If success
    profile.value = output;

  } catch (err) {

    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Coudn't find the User!";
    }

  }
}

async function updateUser(field, newInfo) {
  error.value = null;
  successMessage.value = "";

  try{
    const response = await fetch('http://localhost:8080/user/update', {
      method: 'PUT',
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${props.user.token}`
      },
      body: JSON.stringify({
        info: field,
        newInfo: newInfo
      })
    })

    const output = await response.json();

    if (!response.ok) {
      if (output.message){
        error.value = "Coudn't update User!";
      } else {
        error.value = output.message;
      }
      return;
    }

    // If success
    successMessage.value = output.message;

    if (profile.value) {
      profile.field = newInfo;
    }

  } catch (err) {
    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Failed to Update User!";
    }
  }
}

onMounted( () => {
  fetchUser();
})

</script>

<template>
  <div class="flex-1 h-full flex flex-col gap-4 pl-4">
    <div class="text-white bg-neutral-800 w-full h-full rounded-2xl flex flex-col gap-5 p-5 border">
      <div class="flex justify-center">
        <div class="font-normal w-full">
          <b v-if="props.user.role === 'CLIENT'" class="text-green-600">{{props.user.name}}</b>
          <b v-if="props.user.role === 'TECH_TEAM'" class="text-blue-700">{{props.user.name}}</b>
          <b v-if="props.user.role === 'ADMIN'" class="text-red-800">{{props.user.name}}</b>

          Profile
        </div>

        <button v-on:click="router.push('/dashboard/dashboard-main')" class="cursor-pointer">
          <!-- https://heroicons.com/ -->
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <div class="font-normal w-full h-full  rounded-2xl gap-6">
        <div class="flex justify-center ">
          <!-- https://heroicons.com/outline -->
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="max-w-50">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17.982 18.725A7.488 7.488 0 0 0 12 15.75a7.488 7.488 0 0 0-5.982 2.975m11.963 0a9 9 0 1 0-11.963 0m11.963 0A8.966 8.966 0 0 1 12 21a8.966 8.966 0 0 1-5.982-2.275M15 9.75a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
          </svg>
        </div>


        <div class="flex items-center justify-between pt-15">
          <div class="text-sm flex items-center gap-2 border rounded-2xl p-1">
            <div class="p-2 text-white/90">Name:</div>
            <input ref="nameIn" :placeholder="profile?.name" class="flex-1 p-2 bg-transparent text-white text-sm outline-none border-none"/>

            <button v-on:click="updateUser('name', $refs.nameIn.value)" class="cursor-pointer">
              <!-- https://heroicons.com/outline -->
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10" />
              </svg>
            </button>
          </div>

          <div class="text-sm flex items-center gap-2 border rounded-2xl p-1">
            <div class="p-2 text-white/90">Username:</div>
            <input ref="usernameIn"  :placeholder="profile?.username" class="flex-1 p-2 bg-transparent text-white text-sm outline-none border-none"/>

            <button v-on:click="updateUser('username', $refs.usernameIn.value)" class="cursor-pointer">
              <!-- https://heroicons.com/outline -->
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10" />
              </svg>
            </button>
          </div>

          <div class="text-sm flex items-center gap-2 border rounded-2xl p-1">
            <div class="p-2 text-white/90">Password:</div>
            <input ref="passwordIn" type="password"  :placeholder="profile?.password" class="flex-1 p-2 bg-transparent text-white text-sm outline-none border-none"/>

            <button v-on:click="updateUser('password', $refs.passwordIn.value)" class="cursor-pointer">
              <!-- https://heroicons.com/outline -->
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10" />
              </svg>
            </button>
          </div>

        </div>

        <div class="flex items-center justify-between p-8 ">

          <div class="text-sm flex items-center gap-2 border rounded-2xl p-1">
            <div class="p-2 text-white/90">Email:</div>
            <input ref="emailIn" :placeholder="profile?.email" class="flex-1 p-2 bg-transparent text-white text-sm outline-none border-none"/>

            <button v-on:click="updateUser('email', $refs.emailIn.value)" class="cursor-pointer">
              <!-- https://heroicons.com/outline -->
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10" />
              </svg>
            </button>
          </div>

          <div class="text-sm flex items-center gap-2 border rounded-2xl p-1">
            <div class="p-2 text-white/90">Contact:</div>
            <input ref="contactIn" :placeholder="profile?.contact" class="flex-1 p-2 bg-transparent text-white text-sm outline-none border-none"/>

            <button v-on:click="updateUser('contact', $refs.contactIn.value)" class="cursor-pointer">
              <!-- https://heroicons.com/outline -->
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10" />
              </svg>
            </button>
          </div>

          <div class="text-sm flex items-center gap-2 border rounded-2xl p-1">
            <div class="p-2 text-white/90">Address:</div>
            <input ref="addressIn" :placeholder="profile?.address" class="flex-1 p-2 bg-transparent text-white text-sm outline-none border-none"/>

            <button v-on:click="updateUser('email', $refs.addressIn.value)" class="cursor-pointer">
              <!-- https://heroicons.com/outline -->
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10" />
              </svg>
            </button>
          </div>
        </div>

        <ErrorAlert v-if="error" :message="error" />
        <SuccessAlert v-if="successMessage" :message="successMessage" />

      </div>
    </div>
  </div>
</template>

<style scoped>

</style>