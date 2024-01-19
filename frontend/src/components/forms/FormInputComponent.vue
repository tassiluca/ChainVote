<script setup lang="ts">

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
  },
  pre: {
    type: String,
    required: false,
    default: '',
  }
})
</script>

<template>
  <div :class="classes">
    <label :for=inputId class="form-label">{{ label }}</label>
    <div :id=inputId :aria-describedby="inputId + 'Help'">
      <div class="input-group mb-2">
        <div v-if="pre" class="input-group-prepend">
          <div class="input-group-text">{{ pre }}</div>
        </div>
        <slot/>
      </div>
    </div>
    <div :id="inputId + 'Help'" class="form-text text-muted">{{ helper }}</div>
  </div>
</template>

<style scoped></style>