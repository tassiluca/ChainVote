<script setup lang="ts">
import { Pie } from 'vue-chartjs'
import { Chart as ChartJS, ArcElement, Tooltip, Title, Legend, Colors } from 'chart.js'

ChartJS.register(ArcElement, Tooltip, Title, Legend, Colors)

const data = {
  labels: ['VueJs', 'EmberJs', 'ReactJs', 'AngularJs'],
  datasets: [
    {
      data: [40, 20, 80, 10]
    }
  ]
}

const options = {
  responsive: true,
  maintainAspectRatio: true
}
</script>

<template>
  <div class="container d-flex align-items-center justify-content-center">
    <div class="content">
      <Pie :data="data" :options="options" />
    </div>
  </div>
</template>

<style scoped>
.content {
  position: relative;
  width: 100%;
}
</style>