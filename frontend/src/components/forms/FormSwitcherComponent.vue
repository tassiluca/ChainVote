<script setup lang="ts">

import { ref, watch } from 'vue'

const props = defineProps({
  left: {
    type: String,
    required: true
  },
  right: {
    type: String,
    required: true
  }
})

const leftFormIsActive = ref(true)

</script>

<template>
  <div id="switcher" class="row">
    <div class="col-sm" :class="{'active': leftFormIsActive}">
      <h2><a @click="leftFormIsActive = true" href="#">{{ props.left }}</a></h2>
    </div>
    <div class="col-sm" :class="{'active': !leftFormIsActive}">
      <h2><a @click="leftFormIsActive = false" href="#">{{ props.right }}</a></h2>
    </div>
  </div>
  <div class="row form-container" :class="{ 'd-none': !leftFormIsActive }">
    <slot name="left" id="form-left" />
  </div>
  <div class="row form-container" :class="{ 'd-none': leftFormIsActive }">
    <slot name="right"/>
  </div>
</template>

<style scoped>
@import 'bootstrap/scss/bootstrap.scss';

div#switcher {
  a {
    text-decoration: none;
    display: block;
  }

  a:hover {
    cursor: pointer;
  }

  h2 {
    padding: 5px;
    margin: 0;
    text-align: center;
    height: 100%;
    width: 100%;
  }

  div {
    border-radius: 15px 15px 0 0;
  }

  div.active {
    background-color: #0d6efd;
    a {
      color: white;
    }
  }

  div:not(.active) {
    background-color: lightgray;
  }
}

div.form-container {
  padding: 20px;
  border-radius: 0 0 15px 15px;
  box-shadow: 2px 5px 15px rgba(200, 200, 200, 0.82);
}

</style>