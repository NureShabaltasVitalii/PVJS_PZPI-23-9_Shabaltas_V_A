import { defineStore } from 'pinia'

const STORAGE_KEY = 'lab3-task-manager'

function dateAfter(days) {
  const date = new Date()
  date.setDate(date.getDate() + days)
  return date.toISOString().slice(0, 10)
}

function createInitialTasks() {
  return [
    {
      id: 1,
      title: 'Підготувати конспект',
      description: 'Описати основні поняття Pinia та Vue Router.',
      category: 'Навчання',
      priority: 'high',
      deadline: dateAfter(1),
      completed: false,
      createdAt: new Date().toISOString(),
    },
    {
      id: 2,
      title: 'Перевірити маршрути',
      description: 'Переконатися, що сторінки відкриваються без перезавантаження.',
      category: 'Розробка',
      priority: 'medium',
      deadline: dateAfter(3),
      completed: false,
      createdAt: new Date().toISOString(),
    },
    {
      id: 3,
      title: 'Завершити попереднє завдання',
      description: 'Приклад виконаної задачі для статистики.',
      category: 'Навчання',
      priority: 'low',
      deadline: dateAfter(-1),
      completed: true,
      createdAt: new Date().toISOString(),
    },
  ]
}

function readSavedTasks() {
  if (typeof localStorage === 'undefined') {
    return null
  }

  try {
    const saved = localStorage.getItem(STORAGE_KEY)
    return saved ? JSON.parse(saved) : null
  } catch {
    return null
  }
}

export const useTasksStore = defineStore('tasks', {
  state: () => ({
    tasks: readSavedTasks() || createInitialTasks(),
  }),

  getters: {
    categories: (state) => [...new Set(state.tasks.map((task) => task.category))].sort(),
    activeTasks: (state) => state.tasks.filter((task) => !task.completed),
    completedTasks: (state) => state.tasks.filter((task) => task.completed),
    overdueTasks: (state) => {
      const today = new Date().toISOString().slice(0, 10)
      return state.tasks.filter((task) => !task.completed && task.deadline < today)
    },
    upcomingTasks: (state) =>
      [...state.tasks]
        .filter((task) => !task.completed)
        .sort((first, second) => first.deadline.localeCompare(second.deadline))
        .slice(0, 5),
    taskById: (state) => (id) => state.tasks.find((task) => task.id === Number(id)),
    filteredTasks: (state) => (filters) => {
      const query = filters.query.trim().toLowerCase()

      return state.tasks.filter((task) => {
        const matchesQuery = !query || task.title.toLowerCase().includes(query)
        const matchesCategory = !filters.category || task.category === filters.category
        const matchesPriority = !filters.priority || task.priority === filters.priority

        return matchesQuery && matchesCategory && matchesPriority
      })
    },
    totalCount: (state) => state.tasks.length,
    activeCount() {
      return this.activeTasks.length
    },
    completedCount() {
      return this.completedTasks.length
    },
    overdueCount() {
      return this.overdueTasks.length
    },
    completionPercent() {
      if (this.totalCount === 0) {
        return 0
      }

      return Math.round((this.completedCount / this.totalCount) * 100)
    },
  },

  actions: {
    persist() {
      if (typeof localStorage !== 'undefined') {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(this.tasks))
      }
    },
    addTask(payload) {
      const newTask = {
        id: Date.now(),
        title: payload.title.trim(),
        description: payload.description.trim(),
        category: payload.category.trim(),
        priority: payload.priority,
        deadline: payload.deadline,
        completed: Boolean(payload.completed),
        createdAt: new Date().toISOString(),
      }

      this.tasks.push(newTask)
      this.persist()

      return newTask
    },
    updateTask(id, payload) {
      const task = this.taskById(id)

      if (!task) {
        return null
      }

      task.title = payload.title.trim()
      task.description = payload.description.trim()
      task.category = payload.category.trim()
      task.priority = payload.priority
      task.deadline = payload.deadline
      task.completed = Boolean(payload.completed)
      this.persist()

      return task
    },
    deleteTask(id) {
      this.tasks = this.tasks.filter((task) => task.id !== Number(id))
      this.persist()
    },
    toggleTask(id) {
      const task = this.taskById(id)

      if (task) {
        task.completed = !task.completed
        this.persist()
      }
    },
  },
})
