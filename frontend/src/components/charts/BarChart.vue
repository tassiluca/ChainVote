<script setup lang="ts">
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, Colors } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, Colors)

const chartData = {
  labels: [ 'January', 'February', 'March' ],
  datasets: [
    {
      title: 'Data',
      label: 'Data',
      data: [40, 20, 12],
    },
    {
      title: 'Data 2',
      label: 'Data 2',
      data: [10, 30, 22],
    }
  ]
}
</script>

<template>
  <Bar :data="chartData" />
</template>
