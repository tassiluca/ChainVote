<script setup lang="ts">
import CodeInsertion from '@/components/vote/CodeInsertion.vue'
</script>

<template>
  <section class="container mt-5">
    <CodeInsertion :digit-number="10" />
  </section>
</template>

<style scoped>
  .otp-field {
    flex-direction: row;
    column-gap: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow-inline: auto;
  }

  .otp-field input {
    height: 45px;
    width: 42px;
    border-radius: 6px;
    outline: none;
    font-size: 1.125rem;
    text-align: center;
    border: 1px solid #ddd;
  }
  .otp-field input:focus {
    border-color: #0073E6;
    box-shadow: 0 1px 0 rgba(0, 0, 0, 0.1);
  }
</style>