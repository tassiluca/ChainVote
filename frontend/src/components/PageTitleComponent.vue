<script setup lang="ts">
defineProps({
  title: {
    type: String,
    required: true
  }
})
</script>

<template>
  <div class="row">
    <div class="col-12">
      <h1 class="text-primary">{{ title }}</h1>
    </div>
  </div>
</template>
