<script setup>
defineProps({
  task: {
    type: Object,
    required: true,
  },
})

defineEmits(['toggle-task', 'delete-task'])
</script>

<template>
  <li class="todo-item" :class="{ completed: task.completed }">
    <label class="task-check">
      <input
        type="checkbox"
        :checked="task.completed"
        @change="$emit('toggle-task', task.id)"
      />
      <span class="checkmark" aria-hidden="true"></span>
      <span class="task-text">{{ task.text }}</span>
    </label>

    <button
      class="delete-button"
      type="button"
      title="Видалити задачу"
      aria-label="Видалити задачу"
      @click="$emit('delete-task', task.id)"
    >
      x
    </button>
  </li>
</template>
