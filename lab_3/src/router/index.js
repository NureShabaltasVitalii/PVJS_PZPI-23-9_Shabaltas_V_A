import { createRouter, createWebHistory } from 'vue-router'
import TaskListView from '../views/TaskListView.vue'
import TaskDetailsView from '../views/TaskDetailsView.vue'
import TaskFormView from '../views/TaskFormView.vue'
import StatsView from '../views/StatsView.vue'

const routes = [
  {
    path: '/',
    name: 'tasks',
    component: TaskListView,
  },
  {
    path: '/tasks/new',
    name: 'task-create',
    component: TaskFormView,
  },
  {
    path: '/tasks/:id',
    name: 'task-details',
    component: TaskDetailsView,
    props: true,
  },
  {
    path: '/tasks/:id/edit',
    name: 'task-edit',
    component: TaskFormView,
    props: true,
  },
  {
    path: '/stats',
    name: 'stats',
    component: StatsView,
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
]

export default createRouter({
  history: createWebHistory(),
  routes,
})
