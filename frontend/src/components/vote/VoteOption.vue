<script setup lang="ts">
defineProps<{
    optId: string;
    name: string;
}>()

const model = defineModel()
</script>

<template>

    <div class="form-check">
        <input class="form-check-input" type="radio" :name="name" :id="optId" :value="name" v-model="model">
        <label class="form-check-label" :for="optId">{{name}}</label>
    </div>
</template>


