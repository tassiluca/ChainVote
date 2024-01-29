<script setup lang="ts">
import { library } from "@fortawesome/fontawesome-svg-core";
import { faEnvelope, faBars, faUser, faRightToBracket, faRightFromBracket, faEnvelopeOpen, faEnvelopeOpenText } from "@fortawesome/free-solid-svg-icons";
import { useAuthStore } from "@/stores/auth";
import { useNotificationsStore } from '@/stores/notificationsStore'
import { onMounted } from 'vue'

const authStore = useAuthStore();
const notificationsStore = useNotificationsStore();

onMounted(async () => {
  if (authStore.isLogged) {
    try {
      await notificationsStore.getAllNotifications();
    } catch (e) {
      console.error(e);
    }
  }
});

library.add(faEnvelope, faEnvelopeOpen, faEnvelopeOpenText, faBars, faUser, faRightToBracket, faRightFromBracket);
</script>

<template>
  <nav class="navbar navbar-expand-md navbar-dark bg-primary">
    <div class="container-fluid">
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <font-awesome-icon icon="bars" size="2x" />
      </button>
      <a class="navbar-brand d-md-none" href="/">
        <img alt="ChainVote logo" class="logo" src="@/assets/logo.svg" width="100" height="100" />
      </a>
      <a v-if="!authStore.isLogged" class="navbar-brand d-md-none" href="/login">
        <font-awesome-icon icon="right-to-bracket" size="2x" />
      </a>
      <a v-else class="navbar-brand d-md-none position-relative" href="/user/notifications">
        <font-awesome-icon v-if="notificationsStore.unreadNotifications === 0" icon="fa-solid fa-envelope-open" size="2x" />
        <div v-else>
          <font-awesome-icon icon="fa-solid fa-envelope-open-text" size="2x" />
          <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" >
            {{ notificationsStore.unreadNotifications }}
            <span class="visually-hidden">unread messages</span>
          </span>
        </div>
      </a>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav mx-auto">
          <li class="nav-item">
            <a class="nav-link active" aria-current="page" href="/">Home</a>
          </li>
          <li v-if="authStore.isLogged" class="nav-item">
            <a class="nav-link" href="/dashboard">Dashboard</a>
          </li>
          <a class="navbar-brand d-none d-md-block" href="/">
            <img alt="Vue logo" class="logo" src="@/assets/logo.svg" width="125" height="125" />
          </a>
          <li v-if="authStore.isLogged" class="nav-item">
            <a class="nav-link" href="/user">User Area</a>
          </li>
          <li v-if="authStore.isLogged" class="nav-item">
            <a class="nav-link" href="/login" @click="authStore.logout()">
              <font-awesome-icon icon="right-from-bracket" size="2x" />
            </a>
          </li>
          <a v-if="!authStore.isLogged" class="nav-link d-none d-md-block" href="/login">
            <font-awesome-icon icon="right-to-bracket" size="2x" />
          </a>
          <a v-else class="nav-link d-none d-md-block position-relative" href="/user/notifications">
            <font-awesome-icon v-if="notificationsStore.unreadNotifications === 0" icon="fa-solid fa-envelope-open" size="2x" />
            <div v-else>
              <font-awesome-icon icon="fa-solid fa-envelope-open-text" size="2x" />
              <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" >
                {{ notificationsStore.unreadNotifications }}
                <span class="visually-hidden">unread messages</span>
              </span>
            </div>
          </a>
        </ul>
      </div>
    </div>
  </nav>
</template>

<style scoped>
nav {
  border-radius: 0 0 15px 15px;
  box-shadow: 1px 3px 10px rgba(200, 200, 200, 0.82);
}
nav > div > button {
  border: none;
}
.navbar-nav {
  display: flex;
  align-items: center;
}
.navbar-nav a {
  margin: 0 10px;
  font-size: 1.2em;
}
</style>
