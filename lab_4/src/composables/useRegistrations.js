import { computed, readonly } from 'vue'
import { useStorage } from './useStorage'

const registrations = useStorage('lab4-registrations', [])

function wait(ms) {
  return new Promise((resolve) => {
    setTimeout(resolve, ms)
  })
}

function shouldFail(email) {
  return email.toLowerCase().includes('fail')
}

export function useRegistrations() {
  const confirmedRegistrations = computed(() =>
    registrations.value.filter((registration) => registration.status !== 'failed'),
  )

  function getRegistrationsByEvent(eventId) {
    return confirmedRegistrations.value.filter(
      (registration) => registration.eventId === Number(eventId),
    )
  }

  async function register(event, payload) {
    const optimisticRegistration = {
      id: Date.now(),
      eventId: event.id,
      eventTitle: event.title,
      name: payload.name.trim(),
      email: payload.email.trim(),
      phone: payload.phone.trim(),
      comment: payload.comment.trim(),
      createdAt: new Date().toISOString(),
      status: 'pending',
    }

    registrations.value.push(optimisticRegistration)

    try {
      await wait(900)

      if (shouldFail(payload.email)) {
        throw new Error('Сервер тимчасово не прийняв реєстрацію')
      }

      optimisticRegistration.status = 'confirmed'
      return optimisticRegistration
    } catch (error) {
      registrations.value = registrations.value.filter(
        (registration) => registration.id !== optimisticRegistration.id,
      )
      throw error
    }
  }

  function removeRegistration(id) {
    registrations.value = registrations.value.filter(
      (registration) => registration.id !== Number(id),
    )
  }

  return {
    registrations: readonly(registrations),
    confirmedRegistrations,
    getRegistrationsByEvent,
    register,
    removeRegistration,
  }
}
