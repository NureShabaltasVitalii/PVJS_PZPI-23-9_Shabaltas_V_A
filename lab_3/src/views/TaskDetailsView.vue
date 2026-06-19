<script setup>
import { computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useTasksStore } from '../stores/tasks'

const props = defineProps({
  id: {
    type: String,
    required: true,
  },
})

const router = useRouter()
const tasksStore = useTasksStore()

const task = computed(() => tasksStore.taskById(props.id))

function deleteCurrentTask() {
  tasksStore.deleteTask(props.id)
  router.push('/')
}
</script>

<template>
  <section class="page-section">
    <div v-if="!task" class="empty-state">
      Задачу не знайдено.
      <RouterLink to="/">Повернутися до списку</RouterLink>
    </div>

    <template v-else>
      <div class="page-header">
        <div>
          <p class="eyebrow">Деталі задачі</p>
          <h1>{{ task.title }}</h1>
        </div>
        <div class="actions-row">
          <RouterLink class="secondary-link" :to="`/tasks/${task.id}/edit`">Редагувати</RouterLink>
          <button class="danger-button" type="button" @click="deleteCurrentTask">
            Видалити
          </button>
        </div>
      </div>

      <div class="details-grid">
        <div>
          <span>Опис</span>
          <p>{{ task.description || 'Без опису' }}</p>
        </div>
        <div>
          <span>Категорія</span>
          <p>{{ task.category }}</p>
        </div>
        <div>
          <span>Пріоритет</span>
          <p>{{ task.priority }}</p>
        </div>
        <div>
          <span>Дедлайн</span>
          <p>{{ task.deadline }}</p>
        </div>
        <div>
          <span>Стан</span>
          <p>{{ task.completed ? 'Виконана' : 'Активна' }}</p>
        </div>
      </div>
    </template>
  </section>
</template>
