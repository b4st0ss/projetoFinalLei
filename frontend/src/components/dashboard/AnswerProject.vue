<script setup>
import { useRouter } from 'vue-router'
import { onMounted, ref } from 'vue'
import Feedback from "./Feedback.vue";
import AssociateTech from "./AssociateTech.vue";
import {errorNotification, successNotification} from "../alerts/notifications.js";
import ErrorAlert from "../alerts/ErrorAlert.vue";


const title = ref('')
const description = ref('')
const router = useRouter()

const questions = ref([])
const answers = ref([])
const requirements = ref([])

const showFeedback = ref(false)
const showAssociate = ref(false)
const loading = ref(true)
const locked = ref(false)
const canFeedback = ref(false)
const canDraft = ref(false)
const successMessage = ref(null)
const error = ref(null)
const activeFeedback = ref(null);

const props = defineProps({
  user: { type: Object, required: true },
  projectId: { type: String, required:true }
})

// Add Requirement to Array
function addRequirement(){

  if (!title.value|| !description.value) {
    error.value = "Specify your requirements!";
    return;
  }

  // To add to array
  requirements.value.push({
    title: title.value,
    description: description.value
  })

  title.value = '';
  description.value = "";

  // For setting old errors
  error.value = null;

}

// Remove Requirement from array
function rmvRequirement(i){

  requirements.value.splice(i, 1);

}

// To add Answer to array
function addAnswer(){

  const answersList = []

  for (const question of questions.value) {

    if (!answers.value[question.id] || answers.value[question.id].trim() === ""){
      answers.value[question.id] = "";
    }

    answersList.push({
      formQuestionId: question.id,
      answerText: answers.value[question.id]

    })
  }

  return answersList;

}


function openFeedback(type, itemId) {
  if (!itemId) {
    error.value = "Save first!";
    return;
  }
  activeFeedback.value = { type, id: itemId };
}



async function sendAnswers(draft) {
  try{

    const info = {
      projectId: Number(props.projectId),
      answers: addAnswer(),
      requirements: requirements.value,
      draft: draft
    }

    // console.log(JSON.stringify(info, null, 2))

    const response = await fetch('http://localhost:8080/project/clientForm', {
      method: 'POST',
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${props.user.token}`
      },
      body: JSON.stringify(info)
    })

    const output = await response.text();

    // console.log(response.status);
    // console.log(output);

    if (!response.ok) {
      if (output.message){
        error.value = "Failed to submit project";
      } else {
        error.value = output.message;
      }
      return;
    }

    successNotification(output)
    router.push({ path: '/dashboard/dashboard-main' })

  } catch (err) {

    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Failed to submit project";
    }
  }

}



async function fetchGlobalQuestions() {
  try{
    const response = await fetch('http://localhost:8080/formquestion/getAll', {
      method: 'GET',
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${props.user.token}`
      }
    })

    const output = await response.json();

    if (!response.ok) {
      if (output.message){
        error.value = "Couldn't Fetch Questions!";
      } else {
        error.value = output.message;
      }
      return;
    }

    const questionsList = [];

    for (const out of output){
      questionsList.push({
        id: out.id,
        question: out.question
      })
    }

    questions.value = output;

  } catch (err) {

    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Failed to Fetch Questions";
    }

  }
}



async function fetchProject() {
  try {
    const response = await fetch(`http://localhost:8080/project/${props.projectId}/details`, {
      method: 'GET',
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${props.user.token}`
      }
    })

    const output = await response.json();

    if (!response.ok) {
      if (output.message){
        error.value = "Couldn't Fetch Questions!";
      } else {
        error.value = output.message;
      }
      return false;
    }

    const role = props.user.role;
    const status = output.status;

    switch (status){
      case 'DRAFT':
      case 'WAITING_FOR_CLIENT':
        // Client is the only one that can change info in this steps
        if (role === 'CLIENT'){
          locked.value = false; // Client can write
          canDraft.value = true;
        } else {
          locked.value = true; // Tech and Admin can't
          canDraft.value = false
        }
        canFeedback.value = false;
        break;

      case 'MORE_INFO_NEDDED':
        if (role === 'CLIENT'){
          locked.value = false;
          canDraft.value = true;
        } else {
          locked.value = true;
          canDraft.value = false;
        }
        canFeedback.value = true;
        break;

      case 'UNDER_TECHNICAL_REVIEW':
        // Client submit, project went to tec hands
        if (role === 'CLIENT'){
          locked.value = true; // Client can't change
        } else {
          locked.value = false; // Tec and Admin can change and give feedbacks
        }
        canDraft.value = false;
        canFeedback.value = true;

        break;

      case 'COMPLETED':
        // Project closed, can't change
        locked.value = true;
        canDraft.value = false;
        canFeedback.value = true;
        break;

      default:
        locked.value = true;
        canDraft.value = false;
        canFeedback.value = false;
        break;
    }

    const actualQuestions = [];

    if (status !== 'WAITING_FOR_CLIENT'){
      if (output.answers){

        for (const savedAnswer of output.answers) {

          actualQuestions.push({
            answerId: savedAnswer.answerId,
            id:savedAnswer.formQuestionId,
            question:savedAnswer.questionText
          });

          answers.value[savedAnswer.formQuestionId] = savedAnswer.answerText;
        }
        questions.value = actualQuestions;

      }
    }


    if (output.requirements) {
      const requirementsList = [];

      for (const savedRequirement of output.requirements) {
        requirementsList.push({
          id: savedRequirement.requirementId,
          title: savedRequirement.title,
          description: savedRequirement.description
        });
      }

      requirements.value = requirementsList;
    }


    // Project status for OneMounted
    return status;

  } catch (err) {

    if (err.message) {
      error.value = err.message;
    } else {
      error.value = "Error restoring project draft.";
    }
  }
}



onMounted( async() => {
  loading.value = true;
  error.value = null;

  try{
    const status = await fetchProject();

    if (status === 'WAITING_FOR_CLIENT'){
      await fetchGlobalQuestions();
    }

  } catch (err) {
    errorNotification("Error restoring project draft!");
    router.push("/dashboard/dashboard-main")
  } finally {
    loading.value = false
  }
})


</script>

<template>
  <div class="flex-1 h-full flex flex-col gap-4 pl-4">

    <div v-if="loading" class="text-white bg-neutral-800 w-full h-full rounded-2xl flex flex-col gap-5 p-5">
      <div class="flex flex-col items-center gap-2">
        <span class="italic text-neutral-400">Loading project details...</span>
      </div>
    </div>

    <div v-else class="text-white bg-neutral-800 w-full h-full rounded-2xl flex flex-col gap-5 p-5">
      <div class="flex items-center justify-between w-full">
        <div class="font-normal text-lg">
          Answer Project
        </div>

        <div class="flex items-center gap-3">
          <button v-if="user.role === 'ADMIN'" v-on:click="showAssociate = true" class="flex items-center justify-between hover:bg-blue-700 w-33 border rounded-xl p-1 transition-colors duration-200">

            <span class="text-sm">Associate Tech</span>
            <!-- https://heroicons.com/ -->
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="M18 7.5v3m0 0v3m0-3h3m-3 0h-3m-2.25-4.125a3.375 3.375 0 1 1-6.75 0 3.375 3.375 0 0 1 6.75 0ZM3 19.235v-.11a6.375 6.375 0 0 1 12.75 0v.109A12.318 12.318 0 0 1 9.374 21c-2.331 0-4.512-.645-6.374-1.766Z" />
            </svg>
          </button>


          <AssociateTech v-if="showAssociate" :user="user" :projectId="projectId"    v-on:close="showAssociate = false" />


          <button v-if="canDraft" v-on:click="sendAnswers(true)" class="flex items-center justify-between hover:bg-green-700 w-33 border rounded-xl p-1 transition-colors duration-200">
            <span class="text-sm">Save Draft</span>

            <!-- https://heroicons.com/ -->
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h3.75M9 15h3.75M9 18h3.75m3 .75H18a2.25 2.25 0 0 0 2.25-2.25V6.108c0-1.135-.845-2.098-1.976-2.192a48.424 48.424 0 0 0-1.123-.08m-5.801 0c-.065.21-.1.433-.1.664 0 .414.336.75.75.75h4.5a.75.75 0 0 0 .75-.75 2.25 2.25 0 0 0-.1-.664m-5.8 0A2.251 2.251 0 0 1 13.5 2.25H15c1.012 0 1.867.668 2.15 1.586m-5.8 0c-.376.023-.75.05-1.124.08C9.095 4.01 8.25 4.973 8.25 6.108V8.25m0 0H4.875c-.621 0-1.125.504-1.125 1.125v11.25c0 .621.504 1.125 1.125 1.125h9.75c.621 0 1.125-.504 1.125-1.125V9.375c0-.621-.504-1.125-1.125-1.125H8.25ZM6.75 12h.008v.008H6.75V12Zm0 3h.008v.008H6.75V15Zm0 3h.008v.008H6.75V18Z" />
            </svg>
          </button>

          <button v-on:click="router.push('/dashboard/dashboard-main')" class="cursor-pointer">
            <!-- https://heroicons.com/ -->

            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      <!-- https://preline.co/docs/components/custom-scrollbar.html -->
      <div class="font-normal w-full h-full  rounded-2xl gap-6 overflow-y-auto
          [&::-webkit-scrollbar]:w-2
          [&::-webkit-scrollbar-track]:rounded-full
          [&::-webkit-scrollbar-track]:bg-stone-100
          [&::-webkit-scrollbar-thumb]:rounded-full
          [&::-webkit-scrollbar-thumb]:bg-stone-300
          dark:[&::-webkit-scrollbar-track]:bg-neutral-700
          dark:[&::-webkit-scrollbar-thumb]:bg-neutral-500">

        <div v-if="questions.length === 0" class="flex font-titulo text-5xl text-white/50 w-full italic justify-center items-center">
          No Questions Saved
        </div>

        <div v-for="question in questions" :key="question.id" class="text-sm gap-2.5">
          <div class="p-2 text-white/90 flex flex-box gap-2.5">

            {{ question.question}}

            <!-- https://heroicons.com/outline -->
            <svg v-if="canFeedback && question.answerId" v-on:click="openFeedback('answer', question.answerId)" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="M20.25 8.511c.884.284 1.5 1.128 1.5 2.097v4.286c0 1.136-.847 2.1-1.98 2.193-.34.027-.68.052-1.02.072v3.091l-3-3c-1.354 0-2.694-.055-4.02-.163a2.115 2.115 0 0 1-.825-.242m9.345-8.334a2.126 2.126 0 0 0-.476-.095 48.64 48.64 0 0 0-8.048 0c-1.131.094-1.976 1.057-1.976 2.192v4.286c0 .837.46 1.58 1.155 1.951m9.345-8.334V6.637c0-1.621-1.152-3.026-2.76-3.235A48.455 48.455 0 0 0 11.25 3c-2.115 0-4.198.137-6.24.402-1.608.209-2.76 1.614-2.76 3.235v6.226c0 1.621 1.152 3.026 2.76 3.235.577.075 1.157.14 1.74.194V21l4.155-4.155" />
            </svg>

          </div>
          <textarea :disabled="locked"  v-model="answers[question.id]" class="w-full mb-2 p-2 border bg-neutral-800 text-white/50 rounded-xl" />
        </div>



        <div class="w-full  h-5 rounded-xl p-2">
          <div class=" text-white/90 text-sm rounded-xl bg-neutral-800">
            Any Additional Requirements?
          </div>
        </div>


        <div v-if="!locked" class="flex flex-box w-full h-13 mt-7">

          <input v-model="title" placeholder="Title" class="w-2/6 mb-2 p-2 border bg-neutral-800 text-white/50 rounded-xl" />
          <textarea v-model="description" placeholder="Description" class="w-3/6 mb-2 p-2 border bg-neutral-800 text-white/50 rounded-xl" />

          <button v-on:click="addRequirement" class="w-1/6 h-11 flex items-center justify-between hover:bg-white/90 border rounded-xl p-1 transition-colors duration-200  text-white/90 hover:text-black">

            <!-- https://heroicons.com/ -->
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
            </svg>

            <span class="text-sm">Add Requirement</span>

          </button>
        </div>

        <div v-if="requirements.length === 0" class="flex font-titulo text-2xl text-white/50 w-full italic justify-center items-center mb-5">
          No Requirements Saved
        </div>


        <div v-for="(requirement, i) in requirements" :key="i" class="text-sm gap-2.5 mt-5">
          <div class="flex items-center justify-between p-3 bg-neutral-700/50 rounded-xl">
            <div class="text-white/90 font-medium">
              {{requirement.title}}
            </div>

            <div class="text-white/90 font-medium">
              {{requirement.description}}
            </div>


            <div class="flex">
              <!-- Remove -->
              <!-- https://heroicons.com/outline -->
              <svg v-if="!locked"  v-on:click="rmvRequirement(i)" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0" />
              </svg>

              <!-- Feedback -->
              <!-- https://heroicons.com/outline -->
              <svg  v-if="canFeedback && requirement.id"   v-on:click="openFeedback('requirement', requirement.id)" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M20.25 8.511c.884.284 1.5 1.128 1.5 2.097v4.286c0 1.136-.847 2.1-1.98 2.193-.34.027-.68.052-1.02.072v3.091l-3-3c-1.354 0-2.694-.055-4.02-.163a2.115 2.115 0 0 1-.825-.242m9.345-8.334a2.126 2.126 0 0 0-.476-.095 48.64 48.64 0 0 0-8.048 0c-1.131.094-1.976 1.057-1.976 2.192v4.286c0 .837.46 1.58 1.155 1.951m9.345-8.334V6.637c0-1.621-1.152-3.026-2.76-3.235A48.455 48.455 0 0 0 11.25 3c-2.115 0-4.198.137-6.24.402-1.608.209-2.76 1.614-2.76 3.235v6.226c0 1.621 1.152 3.026 2.76 3.235.577.075 1.157.14 1.74.194V21l4.155-4.155" />
              </svg>

            </div>
          </div>
        </div>


        <Feedback v-if="activeFeedback" :object="activeFeedback" :user="user"  v-on:close="activeFeedback = null" />
        <ErrorAlert v-if="error" :message="error" />


        <div class="flex justify-center w-full mt-5">
          <button v-if="!locked" v-on:click="sendAnswers(false)" class="text-white w-3/10 p-3 rounded-xl justify-center flex text-xs font-normal transition-colors border hover:bg-white hover:text-black duration-300 hover:cursor-pointer">
            Submit
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>