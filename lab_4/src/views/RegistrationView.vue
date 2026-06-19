<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppModal from '../components/AppModal.vue'
import { useEvents } from '../composables/useEvents'
import { useRegistrations } from '../composables/useRegistrations'

const props = defineProps({
  id: {
    type: String,
    required: true,
  },
})

const router = useRouter()
const { getEventById } = useEvents()
const { register } = useRegistrations()

const event = computed(() => getEventById(props.id))
const isSubmitting = ref(false)
const error = ref('')
const modalOpen = ref(false)

const form = reactive({
  name: '',
  email: '',
  phone: '',
  comment: '',
})

const errors = reactive({
  name: '',
  email: '',
  phone: '',
})

function validate() {
  errors.name = form.name.trim() ? '' : 'Введіть ім’я'
  errors.email = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)
    ? ''
    : 'Введіть коректний email'
  errors.phone = form.phone.trim().length >= 7 ? '' : 'Введіть телефон'

  return !errors.name && !errors.email && !errors.phone
}

async function submitRegistration() {
  error.value = ''

  if (!event.value || !validate()) {
    return
  }

  isSubmitting.value = true

  try {
    await register(event.value, form)
    modalOpen.value = true
  } catch (submitError) {
    error.value =
      submitError instanceof Error ? submitError.message : 'Не вдалося виконати реєстрацію'
  } finally {
    isSubmitting.value = false
  }
}

function closeModal() {
  modalOpen.value = false
  router.push(`/events/${event.value.id}`)
}
</script>

<template>
  <section class="page-section">
    <p v-if="!event" class="empty-state">Подію не знайдено</p>

    <template v-else>
      <div class="page-header">
        <div>
          <p class="eyebrow">Реєстрація</p>
          <h1>{{ event.title }}</h1>
        </div>
      </div>

      <form class="registration-form" @submit.prevent="submitRegistration">
        <label>
          <span>Ім’я</span>
          <input v-model="form.name" type="text" placeholder="Ваше ім’я" />
          <small v-if="errors.name">{{ errors.name }}</small>
        </label>

        <label>
          <span>Email</span>
          <input v-model="form.email" type="email" placeholder="name@example.com" />
          <small v-if="errors.email">{{ errors.email }}</small>
        </label>

        <label>
          <span>Телефон</span>
          <input v-model="form.phone" type="tel" placeholder="+380..." />
          <small v-if="errors.phone">{{ errors.phone }}</small>
        </label>

        <label>
          <span>Коментар</span>
          <textarea v-model="form.comment" rows="4" placeholder="Додаткова інформація"></textarea>
        </label>

        <p v-if="error" class="error-message">{{ error }}</p>

        <button class="primary-button" type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? 'Реєстрація...' : 'Зареєструватися' }}
        </button>
      </form>

      <AppModal :open="modalOpen" title="Реєстрацію виконано" @close="closeModal">
        <p>Дані збережено. Інтерфейс оновлено оптимістично, а запис додано до списку.</p>
        <button class="primary-button" type="button" @click="closeModal">Добре</button>
      </AppModal>
    </template>
  </section>
</template>
