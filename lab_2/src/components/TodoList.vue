<script setup>
import { computed } from 'vue'
import TodoItem from './TodoItem.vue'

const props = defineProps({
  tasks: {
    type: Array,
    required: true,
  },
  filter: {
    type: String,
    required: true,
  },
})

defineEmits(['toggle-task', 'delete-task'])

const emptyMessage = computed(() => {
  if (props.filter === 'active') {
    return 'Активних задач немає'
  }

  if (props.filter === 'completed') {
    return 'Виконаних задач немає'
  }

  return 'Поки що немає задач'
})
</script>

<template>
  <p v-if="tasks.length === 0" class="empty-state">
    {{ emptyMessage }}
  </p>

  <ul v-else class="todo-list">
    <TodoItem
      v-for="task in tasks"
      :key="task.id"
      :task="task"
      @toggle-task="$emit('toggle-task', $event)"
      @delete-task="$emit('delete-task', $event)"
    />
  </ul>
</template>
