<script setup>
import { RouterLink } from 'vue-router'

defineProps({
  task: {
    type: Object,
    required: true,
  },
})

defineEmits(['toggle'])

const priorityLabels = {
  low: 'Низький',
  medium: 'Середній',
  high: 'Високий',
}
</script>

<template>
  <article class="task-card" :class="{ completed: task.completed }">
    <div class="task-card-main">
      <button
        class="status-button"
        type="button"
        :aria-label="task.completed ? 'Позначити активною' : 'Позначити виконаною'"
        @click="$emit('toggle', task.id)"
      >
        {{ task.completed ? '✓' : '' }}
      </button>

      <div>
        <RouterLink class="task-title" :to="`/tasks/${task.id}`">
          {{ task.title }}
        </RouterLink>
        <p class="task-description">{{ task.description || 'Без опису' }}</p>
      </div>
    </div>

    <div class="task-meta">
      <span>{{ task.category }}</span>
      <span :class="['priority', task.priority]">{{ priorityLabels[task.priority] }}</span>
      <span>До {{ task.deadline }}</span>
    </div>
  </article>
</template>
