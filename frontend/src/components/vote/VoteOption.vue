<script setup lang="ts">
defineProps<{
    optId: string;
    name: string;
}>()

const model = defineModel()
</script>

<template>
    <input type="radio" :id="optId" :name="name" :value="name" v-model="model" />
    <label :for="optId">{{name}}</label>
</template>


