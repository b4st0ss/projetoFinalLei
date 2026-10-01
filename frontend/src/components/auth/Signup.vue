<script setup>

const emit = defineEmits(['close', 'switch-to-login'])

</script>

<template>
  <div @click.self="$emit('close')" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-xs p-4">
    <div
        class="w-full max-w-md rounded-2xl bg-white p-8 shadow-xl shadow-slate-200/50 border border-slate-200"
        @click.stop
    >

      <div class="text-center mb-6">
        <div class="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-xl bg-orange-50 border border-orange-100 text-orange-600">
          <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
        </div>
        <h2 class="text-2xl font-bold text-slate-900 tracking-tight">
          {{ $t('auth.signup_unavailable_title') }}
        </h2>
        <p class="text-sm text-slate-500 mt-2 leading-relaxed">
          {{ $t('auth.signup_unavailable_desc') }}
        </p>
      </div>

      <div class="mt-8 space-y-3">
        <button
            type="button"
            @click="$emit('switch-to-login')"
            class="w-full flex items-center justify-center rounded-lg bg-blue-800 py-2.5 px-4 text-sm font-semibold text-white shadow-sm hover:bg-blue-700 active:scale-[0.99] transition-all"
        >
          {{ $t('auth.already_have_account') }}
        </button>

        <button
            type="button"
            @click="$emit('close')"
            class="w-full flex items-center justify-center rounded-lg border border-slate-300 bg-white py-2.5 px-4 text-sm font-semibold text-slate-700 hover:bg-slate-50 hover:border-slate-400 active:scale-[0.99] transition-all"
        >
          {{ $t('auth.back_to_home') }}
        </button>
      </div>
    </div>
  </div>
</template>