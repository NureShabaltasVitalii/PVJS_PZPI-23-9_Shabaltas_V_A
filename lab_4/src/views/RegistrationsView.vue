<script setup>
import RegistrationList from '../components/RegistrationList.vue'
import { useRegistrations } from '../composables/useRegistrations'

const { confirmedRegistrations, removeRegistration } = useRegistrations()
</script>

<template>
  <section class="page-section">
    <div class="page-header">
      <div>
        <p class="eyebrow">Реєстрації</p>
        <h1>Зареєстровані користувачі</h1>
      </div>
    </div>

    <RegistrationList
      :registrations="confirmedRegistrations"
      removable
      @remove="removeRegistration"
    />
  </section>
</template>
