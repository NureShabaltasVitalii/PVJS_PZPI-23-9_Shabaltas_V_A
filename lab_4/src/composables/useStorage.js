import { ref, watch } from 'vue'

export function useStorage(key, initialValue) {
  const storedValue = ref(initialValue)

  if (typeof localStorage !== 'undefined') {
    const saved = localStorage.getItem(key)

    if (saved) {
      try {
        storedValue.value = JSON.parse(saved)
      } catch {
        storedValue.value = initialValue
      }
    }
  }

  watch(
    storedValue,
    (value) => {
      if (typeof localStorage !== 'undefined') {
        localStorage.setItem(key, JSON.stringify(value))
      }
    },
    { deep: true },
  )

  return storedValue
}
