<script setup>
import { useTasksStore } from '../stores/tasks'

const tasksStore = useTasksStore()
</script>

<template>
  <section class="page-section">
    <div class="page-header">
      <div>
        <p class="eyebrow">Статистика</p>
        <h1>Стан задач</h1>
      </div>
    </div>

    <div class="stats-grid">
      <article>
        <span>Усього</span>
        <strong>{{ tasksStore.totalCount }}</strong>
      </article>
      <article>
        <span>Активні</span>
        <strong>{{ tasksStore.activeCount }}</strong>
      </article>
      <article>
        <span>Виконані</span>
        <strong>{{ tasksStore.completedCount }}</strong>
      </article>
      <article>
        <span>Прострочені</span>
        <strong>{{ tasksStore.overdueCount }}</strong>
      </article>
    </div>

    <div class="progress-panel">
      <div class="progress-header">
        <span>Прогрес виконання</span>
        <strong>{{ tasksStore.completionPercent }}%</strong>
      </div>
      <div class="progress-track">
        <div class="progress-fill" :style="{ width: `${tasksStore.completionPercent}%` }"></div>
      </div>
    </div>

    <div class="upcoming-panel">
      <h2>Найближчі дедлайни</h2>
      <p v-if="tasksStore.upcomingTasks.length === 0" class="empty-state">
        Активних задач немає
      </p>
      <ul v-else>
        <li v-for="task in tasksStore.upcomingTasks" :key="task.id">
          <span>{{ task.title }}</span>
          <strong>{{ task.deadline }}</strong>
        </li>
      </ul>
    </div>
  </section>
</template>
