import { computed } from 'vue'
import { events } from '../data/events'
import { useRegistrations } from './useRegistrations'

export function useEvents() {
  const { registrations } = useRegistrations()

  const eventList = computed(() =>
    events.map((event) => ({
      ...event,
      registeredCount: registrations.value.filter(
        (registration) => registration.eventId === event.id && registration.status !== 'failed',
      ).length,
    })),
  )

  function getEventById(id) {
    return eventList.value.find((event) => event.id === Number(id))
  }

  return {
    events: eventList,
    getEventById,
  }
}
