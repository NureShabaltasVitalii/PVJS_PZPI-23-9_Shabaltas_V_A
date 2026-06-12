<script setup>
import { computed, onMounted, ref } from 'vue'
import ImageCard from './ImageCard.vue'

const API_LIMIT = 20

const images = ref([])
const isLoading = ref(false)
const error = ref('')
const favorites = ref([])
const searchQuery = ref('')
const currentView = ref('all')
const currentPage = ref(1)

const viewModes = [
  { value: 'all', label: 'Усі' },
  { value: 'favorites', label: 'Обрані' },
]

const filteredImages = computed(() => {
  const query = searchQuery.value.trim().toLowerCase()
  const source = currentView.value === 'favorites' ? favorites.value : images.value

  if (!query) {
    return source
  }

  return source.filter((image) => image.author.toLowerCase().includes(query))
})

const favoriteIds = computed(() => favorites.value.map((image) => image.id))

async function loadImages(page = 1, append = false) {
  isLoading.value = true
  error.value = ''

  try {
    const response = await fetch(
      `https://picsum.photos/v2/list?page=${page}&limit=${API_LIMIT}`,
    )

    if (!response.ok) {
      throw new Error('Не вдалося отримати зображення')
    }

    const data = await response.json()

    if (append) {
      images.value = [...images.value, ...data]
    } else {
      images.value = data
    }

    currentPage.value = page
  } catch (requestError) {
    error.value =
      requestError instanceof Error
        ? requestError.message
        : 'Сталася помилка під час завантаження'
  } finally {
    isLoading.value = false
  }
}

function retryLoading() {
  loadImages(currentPage.value)
}

function loadNextPage() {
  loadImages(currentPage.value + 1, true)
}

function isFavorite(imageId) {
  return favorites.value.some((image) => image.id === imageId)
}

function toggleFavorite(image) {
  if (isFavorite(image.id)) {
    favorites.value = favorites.value.filter((favorite) => favorite.id !== image.id)
    return
  }

  favorites.value.push(image)
}

onMounted(() => {
  loadImages()
})
</script>

<template>
  <section class="gallery-app" aria-labelledby="gallery-title">
    <header class="app-header gallery-header">
      <div>
        <p class="eyebrow gallery-eyebrow">Лабораторна робота 2</p>
        <h2 id="gallery-title">Галерея Picsum</h2>
      </div>
      <p class="gallery-count">Обрані: {{ favorites.length }}</p>
    </header>

    <div class="gallery-controls">
      <label class="sr-only" for="gallery-search">Пошук за автором</label>
      <input
        id="gallery-search"
        v-model="searchQuery"
        type="search"
        placeholder="Пошук за автором"
        autocomplete="off"
      />

      <div class="view-switch" aria-label="Режим галереї">
        <button
          v-for="mode in viewModes"
          :key="mode.value"
          type="button"
          :class="{ active: currentView === mode.value }"
          @click="currentView = mode.value"
        >
          {{ mode.label }}
        </button>
      </div>
    </div>

    <div v-if="isLoading && images.length === 0" class="loading-state">
      Завантаження...
    </div>

    <div v-else-if="error && images.length === 0" class="error-state" role="alert">
      <p>{{ error }}</p>
      <button type="button" @click="retryLoading">Спробувати ще раз</button>
    </div>

    <template v-else>
      <div v-if="error" class="error-state inline" role="alert">
        <p>{{ error }}</p>
        <button type="button" @click="retryLoading">Повторити</button>
      </div>

      <p v-if="filteredImages.length === 0" class="empty-state gallery-empty-state">
        Зображень не знайдено
      </p>

      <div v-else class="image-grid">
        <ImageCard
          v-for="image in filteredImages"
          :key="image.id"
          :image="image"
          :is-favorite="favoriteIds.includes(image.id)"
          @toggle-favorite="toggleFavorite"
        />
      </div>

      <button
        v-if="currentView === 'all'"
        class="load-more-button"
        type="button"
        :disabled="isLoading"
        @click="loadNextPage"
      >
        {{ isLoading ? 'Завантаження...' : 'Завантажити ще' }}
      </button>
    </template>
  </section>
</template>
