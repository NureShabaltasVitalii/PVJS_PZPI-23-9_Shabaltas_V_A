<script setup>
defineProps({
  open: {
    type: Boolean,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
})

defineEmits(['close'])
</script>

<template>
  <Teleport to="body">
    <div v-if="open" class="modal-backdrop">
      <section class="modal-window" role="dialog" aria-modal="true" :aria-label="title">
        <header>
          <h2>{{ title }}</h2>
          <button type="button" aria-label="Закрити" @click="$emit('close')">×</button>
        </header>
        <slot />
      </section>
    </div>
  </Teleport>
</template>
