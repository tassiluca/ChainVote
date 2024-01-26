<script setup lang="ts">
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, Colors } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, Colors)

const props = defineProps({
  labels: {
    type: Array<String>,
    required: true
  },
  values: {
    type: Array<{ "title": string, "label": string, "data": number[] }>,
    required: true
  }
})

const chartData = {
  labels: props.labels,
  datasets: props.values
}
</script>

<template>
  <Bar :data="chartData" />
</template>
