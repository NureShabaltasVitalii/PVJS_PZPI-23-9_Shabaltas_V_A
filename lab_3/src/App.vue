<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <RouterLink class="brand" to="/">Менеджер задач</RouterLink>

      <nav aria-label="Головна навігація">
        <RouterLink to="/">Задачі</RouterLink>
        <RouterLink to="/tasks/new">Створити</RouterLink>
        <RouterLink to="/stats">Статистика</RouterLink>
      </nav>
    </header>

    <main>
      <RouterView v-slot="{ Component }">
        <Transition name="page" mode="out-in">
          <component :is="Component" />
        </Transition>
      </RouterView>
    </main>
  </div>
</template>
