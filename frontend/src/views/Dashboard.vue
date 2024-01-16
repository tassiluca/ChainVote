<template>
  <div class="elections">
    <div class="col-10 center mx-auto election election-open bg-primary">
      <a href="#" class="election-link">Open Elections</a>
      <Carousel :elections="elections"/>
    </div>
  </div>
  <div class="elections">
    <div class="col-10 center mx-auto election election-closed bg-primary">
      <a href="#" class="election-link">Closed Elections</a>
      <Carousel :elections="elections"/>
    </div>
  </div>
  <div class="elections">
    <div class="col-10 center mx-auto election election-soon bg-primary">
      <a href="#" class="election-link">Elections soon</a>
      <Carousel :elections="elections"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import Carousel from "@/components/Carousel.vue";

const elections = [
  {id: 'prova1', name: 'prova1', start: new Date('01/03/23'), end: new Date('03/03/23'), affluence: 2201,},
  {id: 'prova2', name: 'prova2', start: new Date('02/03/23'), end: new Date('04/03/23'), affluence: 25,},
  {id: 'prova3', name: 'prova3', start: new Date('03/03/23'), end: new Date('05/03/23'), affluence: 2200,},
  {id: 'prova4', name: 'prova4', start: new Date('03/03/23'), end: new Date('05/03/23'), affluence: 2200,},
  //{id: 'prova5', name: 'prova5', start: new Date('03/03/23'), end: new Date('05/03/23'), affluence: 2200,},
  //{id: 'prova6', name: 'prova6', start: new Date('03/03/23'), end: new Date('05/03/23'), affluence: 2200,},
]
</script>

<style>
  .election-link {
    color: white;
    font-weight: bold;
    text-decoration: none;
  }

  .election-link:hover {
    color: white;
    text-decoration: none;
  }

  .elections {
    margin: 4% 0;
  }

  .election {
    margin: 2% 0;
    border-radius: 15px;
    box-shadow: 1px 3px 10px rgba(200, 200, 200, 0.82);
    padding: 2%;
  }

  .election-closed {
    border: red 2px solid;
  }

  .election-soon {
    border: blue 2px solid;
  }
</style>

<style lang="scss">
  $color-open: #66FF99;
  .election-open {
    border: $color-open 2px solid;
    a:hover {
      color: $color-open;
    }
  }
  $color-closed: red;
  .election-closed {
    border: $color-closed 2px solid;
    a:hover {
      color: $color-closed;
    }
  }
  $color-soon: blue;
  .election-soon {
    border: $color-soon 2px solid;
    a:hover {
      color: $color-soon;
    }
  }
</style>