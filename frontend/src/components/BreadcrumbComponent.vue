<script setup lang="ts">
import type { PropType } from 'vue'

const props = defineProps({
  paths: {
    type: Array as PropType<{ name: string, link: string }[]>,
    required: true
  }
})
</script>

<template>
  <div class="container">
    <nav aria-label="breadcrumb">
      <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="/">Home</a></li>
        <li v-for="(path, index) in props.paths" :key="path.name" class="breadcrumb-item">
          <a :href="path.link" :class="{ 'active': index === paths.length - 1 }">{{ path.name }}</a>
        </li>
      </ol>
    </nav>
  </div>
</template>

<style scoped>
nav {
  margin: 20px 0;
  background-color: #f2f2f2;
  border-radius: 15px;
  ol {
    padding: 0.5em 0.5em 0.5em 1em;
    a {
      color: gray;
      text-decoration: none;
    }
    .active {
      font-weight: bold;
    }
    a:hover {
      text-decoration: underline;
    }
  }
}
</style>
