<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  initialTask: {
    type: Object,
    default: null,
  },
  submitLabel: {
    type: String,
    default: 'Зберегти',
  },
})

const emit = defineEmits(['submit'])

const form = reactive({
  title: '',
  description: '',
  category: '',
  priority: 'medium',
  deadline: '',
  completed: false,
})

const errors = reactive({
  title: '',
  category: '',
  priority: '',
  deadline: '',
})

function fillForm(task) {
  form.title = task?.title || ''
  form.description = task?.description || ''
  form.category = task?.category || ''
  form.priority = task?.priority || 'medium'
  form.deadline = task?.deadline || ''
  form.completed = Boolean(task?.completed)
}

watch(
  () => props.initialTask,
  (task) => fillForm(task),
  { immediate: true },
)

function validate() {
  errors.title = form.title.trim() ? '' : 'Введіть назву задачі'
  errors.category = form.category.trim() ? '' : 'Введіть категорію'
  errors.priority = form.priority ? '' : 'Оберіть пріоритет'
  errors.deadline = form.deadline ? '' : 'Оберіть дедлайн'

  return !errors.title && !errors.category && !errors.priority && !errors.deadline
}

function submitForm() {
  if (!validate()) {
    return
  }

  emit('submit', { ...form })
}
</script>

<template>
  <form class="task-form-panel" @submit.prevent="submitForm">
    <label>
      <span>Назва</span>
      <input v-model="form.title" type="text" placeholder="Наприклад: підготувати звіт" />
      <small v-if="errors.title">{{ errors.title }}</small>
    </label>

    <label>
      <span>Опис</span>
      <textarea v-model="form.description" rows="4" placeholder="Короткий опис задачі"></textarea>
    </label>

    <div class="form-grid">
      <label>
        <span>Категорія</span>
        <input v-model="form.category" type="text" placeholder="Навчання" />
        <small v-if="errors.category">{{ errors.category }}</small>
      </label>

      <label>
        <span>Пріоритет</span>
        <select v-model="form.priority">
          <option value="low">Низький</option>
          <option value="medium">Середній</option>
          <option value="high">Високий</option>
        </select>
        <small v-if="errors.priority">{{ errors.priority }}</small>
      </label>

      <label>
        <span>Дедлайн</span>
        <input v-model="form.deadline" type="date" />
        <small v-if="errors.deadline">{{ errors.deadline }}</small>
      </label>
    </div>

    <label class="checkbox-row">
      <input v-model="form.completed" type="checkbox" />
      <span>Задача виконана</span>
    </label>

    <button class="primary-button" type="submit">{{ submitLabel }}</button>
  </form>
</template>
