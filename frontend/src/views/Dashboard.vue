<template>
  <div class="elections">
    <h2>Open Elections</h2>
    <div class="col-10 center mx-auto election election-open">
      <Carousel elections=""/>
    </div>
  </div>
  <div class="elections">
    <h2>Closed Elections</h2>
    <div class="col-10 center mx-auto election election-closed">
      <Carousel elections=""/>
    </div>
  </div>
  <div class="elections">
    <h2>Elections soon...</h2>
    <div class="col-10 center mx-auto election election-soon">
      <Carousel elections=""/>
    </div>
  </div>
</template>

<style>
  body {
    align-items: center;
  }

  .elections {
    text-align: center;
    margin: 4% 0;
  }

  .election {
    margin: 2% 0;
    border-radius: 15px;
    box-shadow: 1px 3px 10px rgba(200, 200, 200, 0.82);
    padding: 2%;
    background-color: grey;
  }

  .election-open {
    border: green 2px solid;
  }

  .election-closed {
    border: red 2px solid;
  }

  .election-soon {
    border: blue 2px solid;
  }
</style>

<script setup lang="ts">
import Carousel from "@/components/Carousel.vue";
</script>