<script setup>
defineProps({
  registrations: {
    type: Array,
    required: true,
  },
  removable: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['remove'])
</script>

<template>
  <p v-if="registrations.length === 0" class="empty-state">Зареєстрованих користувачів немає</p>

  <ul v-else class="registration-list">
    <li v-for="registration in registrations" :key="registration.id">
      <div>
        <strong>{{ registration.name }}</strong>
        <span>{{ registration.email }}</span>
        <small>{{ registration.eventTitle }}</small>
      </div>
      <button
        v-if="removable"
        class="ghost-button"
        type="button"
        @click="$emit('remove', registration.id)"
      >
        Видалити
      </button>
    </li>
  </ul>
</template>
