<script setup lang="ts">
import { Pie } from 'vue-chartjs'
import { Chart as ChartJS, ArcElement, Tooltip, Title, Legend, Colors } from 'chart.js'

ChartJS.register(ArcElement, Tooltip, Title, Legend, Colors)

const props = defineProps({
  labels: {
    type: Array<String>,
    required: true
  },
  values: {
    type: Array<Number>,
    required: true
  }
})

const data = {
  labels: props.labels,
  datasets: [
    {
      data: props.values
    }
  ]
}

const options = {
  responsive: true,
  maintainAspectRatio: true
}
</script>

<template>
  <div class="container-sm">
    <Pie :data="data" :options="options" />
  </div>
</template>

<style scoped>
div {
  width: 80%;
}

@media screen and (min-width: 768px) {
  div {
    width: 60%;
  }
}
</style>
