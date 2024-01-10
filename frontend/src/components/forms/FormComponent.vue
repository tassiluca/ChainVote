<script setup lang="ts">

import { defineProps } from 'vue'

defineProps({
  submitBtnName: {
    type: String,
    required: false,
    default: 'Submit'
  },
})
</script>

<template>
  <form>
    <div class="col-sm">
      <slot name="body"/>
      <button type="submit" class="btn btn-primary mt-3 mb-3">
        {{ submitBtnName }}
        <!-- TODO: display only when necessary -->
        <span class="spinner-border spinner-border-sm text-light"></span>
      </button>
    </div>
    <slot name="footer"/>
    <!-- TODO: display only when necessary / change to success -->
    <div class="alert alert-danger mt-3 mb-3" role="alert">
      <strong>
        Lorem ipsum dolor sit amet, consectetur adipiscing elit.
      </strong>
    </div>
  </form>
</template>

<style scoped>

</style>
