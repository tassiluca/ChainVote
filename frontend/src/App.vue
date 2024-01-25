<script setup lang="ts">
import { RouterView } from 'vue-router'
import NavBar from '@/components/NavbarComponent.vue'
import Footer from '@/components/FooterComponent.vue'
import io from 'socket.io-client'
import { apiEndpoints } from '@/commons/globals'
import { onMounted } from 'vue'
import { useToast } from 'vue-toast-notification'
import { useAuthStore } from '@/stores/auth'
import { useNotificationsStore } from '@/stores/notificationsStore'

const authStore = useAuthStore();
const notificationsStore = useNotificationsStore();
const socket = io(apiEndpoints.API_SERVER)
const toast = useToast();

onMounted(() => {
  if (authStore.isLogged) {
    socket.on("new-notification", (body: string) => {
      notificationsStore.unreadNotifications++;
      displayNotification(body)
      console.log(notificationsStore.unreadNotifications);
    });
    // TODO: user specific notifications
    // socket.join("user-" + authStore.user.id);
    // socket.on("new-notification", (body: string) => displayNotification(body));
  }
})

function displayNotification(body: string) {
  toast.info(body, {
    duration: 0
  });
}
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
