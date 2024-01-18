<script setup lang="ts">
import { library } from "@fortawesome/fontawesome-svg-core";
import {faEnvelope, faBars, faUser, faRightToBracket, faRightFromBracket} from "@fortawesome/free-solid-svg-icons";
import { useAuthStore } from "@/stores/auth";
import { onMounted, ref } from "vue";

library.add(faEnvelope, faBars, faUser, faRightToBracket, faRightFromBracket);

const authStore = useAuthStore();

const logged = ref(false)

onMounted(() => {
  logged.value = authStore.isLogged();
});
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
      <a class="navbar-brand d-md-none" href="/login" @click="logged ? authStore.logout() : null">
        <font-awesome-icon v-if="!logged" icon="right-to-bracket" size="2x" />
        <font-awesome-icon v-if="logged" icon="right-from-bracket" size="2x" />
      </a>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav mx-auto">
          <!-- TODO: to customize according to user role -->
          <li class="nav-item">
            <a class="nav-link active" aria-current="page" href="/home">Home</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="/user">User Area</a>
          </li>
          <a class="navbar-brand d-none d-md-block" href="/">
            <img alt="Vue logo" class="logo" src="@/assets/logo.svg" width="125" height="125" />
          </a>
          <li class="nav-item">
            <a class="nav-link" href="/">Dashboard</a>
          </li>
          <a class="navbar-brand d-none d-md-block" href="/login">
            <font-awesome-icon v-if="!logged" icon="right-to-bracket" size="2x" />
            <font-awesome-icon v-if="logged" icon="right-from-bracket" size="2x" />
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
