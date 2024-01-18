<script setup lang="ts">
import { RouterView } from 'vue-router'
import NavBar from '@/components/NavbarComponent.vue'
import Footer from '@/components/FooterComponent.vue'
import {useAuthStore} from "./stores/auth";

const authStore = useAuthStore();
</script>

<template>
  <NavBar :key="$route.fullPath" />
  <main>
    <RouterView />
  </main>
  <Footer />
</template>

<style scoped>
main {
  margin: 25px 0;
}
</style>
