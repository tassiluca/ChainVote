<script setup lang="ts">
import CodeInsertion from '@/components/vote/CodeInsertion.vue'
</script>

<template>
  <section class="container-fluid bg-body-tertiary d-block">
    <div class="row justify-content-center">
      <div class="col-12 col-md-6 col-lg-4" style="min-width: 500px;">
        <CodeInsertion :digit-number="10" />
      </div>
    </div>
  </section>
</template>

<style scoped>
  .otp-field {
    flex-direction: row;
    column-gap: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
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