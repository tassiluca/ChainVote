<script setup lang="ts">

defineProps({
  title: {
    type: String,
    required: true,
  },
  bigger: {
    type: String,
    required: false,
  },
  medium: {
    type: String,
    required: false,
  },
  smaller: {
    type: String,
    required: false,
  },
})

</script>

<template>
  <div class="d-flex flex-column justify-content-center tile">
    <span id="title">{{title}}</span>
    <span v-if="bigger" id="bigger">{{bigger}}</span>
    <span v-if="medium" id="medium">{{medium}}</span>
    <span><slot /></span>
    <span v-if="smaller" id="smaller">{{smaller}}</span>
  </div>
</template>

<style scoped>
div {
  border-radius: 15px;
  box-shadow: 1px 3px 15px rgba(200, 200, 200, 0.82);
  padding: 10px;
  margin: 10px 0;
  color: #0d6efd;

  span#title {
    font-weight: bold;
    font-size: 1.1em;
    color: #e6308a;
    text-transform: uppercase;
  }

  span#bigger {
    font-weight: bold;
    font-size: 1.5em;
    text-transform: uppercase;
  }

  span#medium {
    font-weight: bold;
    font-size: 1.2em;
    text-transform: uppercase;
  }

  span#smaller {
    font-size: 0.8em;
  }
}
</style>