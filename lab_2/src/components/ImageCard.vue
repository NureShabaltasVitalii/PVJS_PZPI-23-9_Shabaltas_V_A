<script setup>
const props = defineProps({
  image: {
    type: Object,
    required: true,
  },
  isFavorite: {
    type: Boolean,
    required: true,
  },
})

defineEmits(['toggle-favorite'])

const previewUrl = `https://picsum.photos/id/${props.image.id}/640/420`
</script>

<template>
  <article class="image-card">
    <img :src="previewUrl" :alt="`Фото автора ${image.author}`" loading="lazy" />

    <div class="image-card-body">
      <div>
        <p class="image-author">{{ image.author }}</p>
        <a :href="image.download_url" target="_blank" rel="noreferrer">
          Відкрити оригінал
        </a>
      </div>

      <button
        class="favorite-button"
        :class="{ active: isFavorite }"
        type="button"
        :title="isFavorite ? 'Прибрати з обраного' : 'Додати в обране'"
        :aria-label="isFavorite ? 'Прибрати з обраного' : 'Додати в обране'"
        @click="$emit('toggle-favorite', image)"
      >
        <span aria-hidden="true">{{ isFavorite ? '♥' : '♡' }}</span>
      </button>
    </div>
  </article>
</template>
