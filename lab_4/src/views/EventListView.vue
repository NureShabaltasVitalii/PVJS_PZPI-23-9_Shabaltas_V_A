<script setup>
import EventCard from '../components/EventCard.vue'
import { useEvents } from '../composables/useEvents'

const { events } = useEvents()
</script>

<template>
  <section class="page-section">
    <div class="page-header">
      <div>
        <p class="eyebrow">Лабораторна робота 4</p>
        <h1>Події для реєстрації</h1>
      </div>
    </div>

    <div class="events-grid">
      <EventCard v-for="event in events" :key="event.id" :event="event" />
    </div>
  </section>
</template>
