<script setup>
import { RouterLink } from 'vue-router'

defineProps({
  event: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <article class="event-card">
    <img :src="event.image" :alt="event.title" />

    <div class="event-card-body">
      <span class="category">{{ event.category }}</span>
      <h2>{{ event.title }}</h2>
      <p>{{ event.description }}</p>

      <dl>
        <div>
          <dt>Дата</dt>
          <dd>{{ event.date }} о {{ event.time }}</dd>
        </div>
        <div>
          <dt>Місце</dt>
          <dd>{{ event.place }}</dd>
        </div>
        <div>
          <dt>Учасники</dt>
          <dd>{{ event.registeredCount }} / {{ event.capacity }}</dd>
        </div>
      </dl>

      <RouterLink class="primary-link" :to="`/events/${event.id}`">Детальніше</RouterLink>
    </div>
  </article>
</template>
