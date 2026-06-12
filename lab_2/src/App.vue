<script setup>
import { computed, ref } from 'vue'
import ImageGallery from './components/ImageGallery.vue'
import TodoList from './components/TodoList.vue'

let nextTaskId = 1

const tasks = ref([])
const newTaskText = ref('')
const currentFilter = ref('all')

const filters = [
  { value: 'all', label: 'Усі' },
  { value: 'active', label: 'Активні' },
  { value: 'completed', label: 'Виконані' },
]

const filteredTasks = computed(() => {
  if (currentFilter.value === 'active') {
    return tasks.value.filter((task) => !task.completed)
  }

  if (currentFilter.value === 'completed') {
    return tasks.value.filter((task) => task.completed)
  }

  return tasks.value
})

const activeCount = computed(() => tasks.value.filter((task) => !task.completed).length)
const completedCount = computed(() => tasks.value.filter((task) => task.completed).length)

function addTask() {
  const text = newTaskText.value.trim()

  if (!text) {
    return
  }

  tasks.value.push({
    id: nextTaskId++,
    text,
    completed: false,
  })

  newTaskText.value = ''
}

function toggleTask(taskId) {
  const task = tasks.value.find((item) => item.id === taskId)

  if (task) {
    task.completed = !task.completed
  }
}

function deleteTask(taskId) {
  tasks.value = tasks.value.filter((task) => task.id !== taskId)
}
</script>

<template>
  <main class="page-shell">
    <section class="todo-app" aria-labelledby="app-title">
      <header class="app-header">
        <p class="eyebrow">Лабораторна робота 1</p>
        <h1 id="app-title">Список задач</h1>
      </header>

      <form class="task-form" @submit.prevent="addTask">
        <label class="sr-only" for="task-input">Нова задача</label>
        <input
          id="task-input"
          v-model="newTaskText"
          type="text"
          placeholder="Введіть нову задачу"
          autocomplete="off"
        />
        <button type="submit">Додати</button>
      </form>

      <div class="filter-bar" aria-label="Фільтр задач">
        <button
          v-for="filter in filters"
          :key="filter.value"
          type="button"
          :class="{ active: currentFilter === filter.value }"
          @click="currentFilter = filter.value"
        >
          {{ filter.label }}
        </button>
      </div>

      <TodoList
        :tasks="filteredTasks"
        :filter="currentFilter"
        @toggle-task="toggleTask"
        @delete-task="deleteTask"
      />

      <footer class="task-summary" aria-live="polite">
        <span>Активні: {{ activeCount }}</span>
        <span>Виконані: {{ completedCount }}</span>
        <span>Усього: {{ tasks.length }}</span>
      </footer>
    </section>

    <ImageGallery />
  </main>
</template>
