import { createRouter, createWebHistory } from 'vue-router'
import EventListView from '../views/EventListView.vue'
import EventDetailsView from '../views/EventDetailsView.vue'
import RegistrationView from '../views/RegistrationView.vue'
import RegistrationsView from '../views/RegistrationsView.vue'

const routes = [
  {
    path: '/',
    name: 'events',
    component: EventListView,
  },
  {
    path: '/events/:id',
    name: 'event-details',
    component: EventDetailsView,
    props: true,
  },
  {
    path: '/events/:id/register',
    name: 'event-register',
    component: RegistrationView,
    props: true,
  },
  {
    path: '/registrations',
    name: 'registrations',
    component: RegistrationsView,
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
