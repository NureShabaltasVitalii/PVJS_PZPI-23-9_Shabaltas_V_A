<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <RouterLink class="brand" to="/">Реєстрація на події</RouterLink>

      <nav class="main-nav" aria-label="Головна навігація">
        <RouterLink to="/">Події</RouterLink>
        <RouterLink to="/registrations">Реєстрації</RouterLink>
      </nav>
    </header>

    <main class="content">
      <RouterView v-slot="{ Component }">
        <Transition name="page" mode="out-in">
          <component :is="Component" />
        </Transition>
      </RouterView>
    </main>
  </div>
</template>
