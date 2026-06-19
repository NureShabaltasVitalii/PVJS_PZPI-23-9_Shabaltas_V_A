<script setup>
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import RegistrationList from '../components/RegistrationList.vue'
import { useEvents } from '../composables/useEvents'
import { useRegistrations } from '../composables/useRegistrations'

const props = defineProps({
  id: {
    type: String,
    required: true,
  },
})

const { getEventById } = useEvents()
const { getRegistrationsByEvent } = useRegistrations()

const event = computed(() => getEventById(props.id))
const registrations = computed(() => getRegistrationsByEvent(props.id))
</script>

<template>
  <section class="page-section">
    <p v-if="!event" class="empty-state">Подію не знайдено</p>

    <template v-else>
      <div class="details-hero">
        <img :src="event.image" :alt="event.title" />
        <div>
          <span class="category">{{ event.category }}</span>
          <h1>{{ event.title }}</h1>
          <p>{{ event.description }}</p>
          <RouterLink class="primary-link" :to="`/events/${event.id}/register`">
            Зареєструватися
          </RouterLink>
        </div>
      </div>

      <div class="info-grid">
        <article>
          <span>Дата і час</span>
          <strong>{{ event.date }} о {{ event.time }}</strong>
        </article>
        <article>
          <span>Місце</span>
          <strong>{{ event.place }}</strong>
        </article>
        <article>
          <span>Місця</span>
          <strong>{{ event.registeredCount }} / {{ event.capacity }}</strong>
        </article>
      </div>

      <section class="subsection">
        <h2>Зареєстровані користувачі</h2>
        <RegistrationList :registrations="registrations" />
      </section>
    </template>
  </section>
</template>
