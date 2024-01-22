<script setup lang="ts">
import { RouterView } from 'vue-router'
import NavBar from '@/components/NavbarComponent.vue'
import Footer from '@/components/FooterComponent.vue'
</script>

<template>
  <NavBar :key="$route.fullPath" />
  <main class="text-center">
    <RouterView />
  </main>
  <Footer />
</template>

<style scoped>
main {
  margin: 25px 0;
}
</style>
