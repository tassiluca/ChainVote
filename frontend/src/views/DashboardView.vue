<template>
  <Breadcrumb :paths="[{name: 'Dashboard', link: '/dashboard'}]" />
  <PageTitle title="Dashboard" />
  <div class="elections">
    <div class="col-10 center mx-auto election election-open bg-light">
      <a href="/elections?qualifier=open" class="election-link">Open Elections</a>
      <carousel :elections="elections"/>
    </div>
  </div>
  <div class="elections">
    <div class="col-10 center mx-auto election election-closed bg-light">
      <a href="/elections?qualifier=closed" class="election-link">Closed Elections</a>
      <carousel :elections="elections"/>
    </div>
  </div>
  <div class="elections">
    <div class="col-10 center mx-auto election election-soon bg-light">
      <a href="/elections?qualifier=soon" class="election-link">Elections soon</a>
      <carousel :elections="elections"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import Carousel from "@/components/Carousel.vue";
import PageTitle from "@/components/PageTitleComponent.vue";
import Breadcrumb from "@/components/BreadcrumbComponent.vue";

const elections = [
  {id: 'prova1', name: 'Elezione di prova presidente consiglio dei ministri', start: new Date(2023, 3, 1, 12, 0).toISOString().slice(0,16), end: new Date('03/03/23'), affluence: "20%",},
  {id: 'prova2', name: 'prova2', start: new Date('02/03/23'), end: new Date('04/03/23'), affluence: "20%",},
  {id: 'prova3', name: 'prova3', start: new Date('03/03/23'), end: new Date('05/03/23'), affluence: "20%",},
  {id: 'prova4', name: 'prova4', start: new Date('03/03/23'), end: new Date('05/03/23'), affluence: "20%",},
  {id: 'prova5', name: 'prova5', start: new Date('03/03/23'), end: new Date('05/03/23'), affluence: "20%",},
  {id: 'prova6', name: 'prova6', start: new Date('03/03/23'), end: new Date('05/03/23'), affluence: "20%",},
]
</script>

<style>
  .election-link {
    color: black;
    font-weight: bold;
    text-decoration: none;
  }

  .election-link:hover {
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
    button {
      color: black;
    }
  }
</style>

<style lang="scss">
  $color-open: #66FF99;
  .election-open {
    border: $color-open 2px solid;
    a:hover {
      color: $color-open;
    }
    button:hover {
      color: $color-open;
    }
  }
  $color-closed: red;
  .election-closed {
    border: $color-closed 2px solid;
    a:hover {
      color: $color-closed;
    }
    button:hover {
      color: $color-closed;
    }
  }
  $color-soon: blue;
  .election-soon {
    border: $color-soon 2px solid;
    a:hover {
      color: $color-soon;
    }
    button:hover {
      color: $color-soon;
    }
  }
</style>