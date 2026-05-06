<script setup>
import { ref, onMounted } from 'vue'

const userName = ref('')

onMounted(() => {
    const savedUser = localStorage.getItem('user')
    if (savedUser) {
        const data = JSON.parse(savedUser)
        userName.value = data.name
    }
})
</script>

<template>
    <h1 class="text-2xl font-mono">
      Boas {{ userName }} !
    </h1>
</template>