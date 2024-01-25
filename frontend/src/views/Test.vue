<script setup lang="ts">
import BarChart from "@/components/charts/BarChart.vue";
import PieChart from "@/components/charts/PieChart.vue";
import {useAuthStore} from "@/stores/auth";

const authStore = useAuthStore();
</script>

<template>
  <h1>{{ authStore.isLogged }}</h1>
  <p>{{ authStore.accessToken ? authStore.accessToken : "null" }}</p>
<!--  <BarChart />-->
<!--  <PieChart />-->
</template>

<style scoped>

</style>