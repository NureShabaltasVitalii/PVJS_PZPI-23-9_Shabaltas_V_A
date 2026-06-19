<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import TaskForm from '../components/TaskForm.vue'
import { useTasksStore } from '../stores/tasks'

const props = defineProps({
  id: {
    type: String,
    default: '',
  },
})

const router = useRouter()
const tasksStore = useTasksStore()

const isEditing = computed(() => Boolean(props.id))
const task = computed(() => (isEditing.value ? tasksStore.taskById(props.id) : null))

function saveTask(payload) {
  if (isEditing.value) {
    const updatedTask = tasksStore.updateTask(props.id, payload)
    router.push(updatedTask ? `/tasks/${updatedTask.id}` : '/')
    return
  }

  const newTask = tasksStore.addTask(payload)
  router.push(`/tasks/${newTask.id}`)
}
</script>

<template>
  <section class="page-section">
    <div v-if="isEditing && !task" class="empty-state">Задачу для редагування не знайдено.</div>

    <template v-else>
      <div class="page-header">
        <div>
          <p class="eyebrow">{{ isEditing ? 'Редагування' : 'Створення' }}</p>
          <h1>{{ isEditing ? 'Редагувати задачу' : 'Нова задача' }}</h1>
        </div>
      </div>

      <TaskForm
        :initial-task="task"
        :submit-label="isEditing ? 'Зберегти зміни' : 'Створити задачу'"
        @submit="saveTask"
      />
    </template>
  </section>
</template>
