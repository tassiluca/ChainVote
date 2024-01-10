<script setup lang="ts">

import { defineProps } from 'vue'

defineProps({
  label: {
    type: String,
    required: true
  },
  inputId: {
    type: String,
    required: true
  },
  helper : {
    type: String,
    required: true
  },
  classes: {
    type: String,
    required: false,
    default: 'form-group mb-2 mt-2'
  }
})
</script>

<template>
  <div :class="classes">
    <label :for=inputId class="form-label">{{ label }}</label>
    <div :id=inputId :aria-describedby="inputId + 'Help'">
      <slot/>
    </div>
    <div :id="inputId + 'Help'" class="form-text text-muted">{{ helper }}</div>
  </div>
</template>

<style scoped></style>