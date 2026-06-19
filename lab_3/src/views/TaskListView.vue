<script setup>
import { computed, reactive } from 'vue'
import { RouterLink } from 'vue-router'
import TaskCard from '../components/TaskCard.vue'
import { useTasksStore } from '../stores/tasks'

const tasksStore = useTasksStore()

const filters = reactive({
  query: '',
  category: '',
  priority: '',
})

const filteredTasks = computed(() => tasksStore.filteredTasks(filters))
</script>

<template>
  <section class="page-section">
    <div class="page-header">
      <div>
        <p class="eyebrow">Лабораторна робота 3</p>
        <h1>Менеджер задач</h1>
      </div>
      <RouterLink class="primary-link" to="/tasks/new">Нова задача</RouterLink>
    </div>

    <div class="filters-panel">
      <label>
        <span>Пошук за назвою</span>
        <input v-model="filters.query" type="search" placeholder="Введіть назву" />
      </label>

      <label>
        <span>Категорія</span>
        <select v-model="filters.category">
          <option value="">Усі категорії</option>
          <option v-for="category in tasksStore.categories" :key="category" :value="category">
            {{ category }}
          </option>
        </select>
      </label>

      <label>
        <span>Пріоритет</span>
        <select v-model="filters.priority">
          <option value="">Усі пріоритети</option>
          <option value="low">Низький</option>
          <option value="medium">Середній</option>
          <option value="high">Високий</option>
        </select>
      </label>
    </div>

    <p v-if="filteredTasks.length === 0" class="empty-state">Задач не знайдено</p>

    <div v-else class="task-list">
      <TaskCard
        v-for="task in filteredTasks"
        :key="task.id"
        :task="task"
        @toggle="tasksStore.toggleTask"
      />
    </div>
  </section>
</template>
