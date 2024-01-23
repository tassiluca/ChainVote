<script setup lang="ts">

import Breadcrumb from '@/components/BreadcrumbComponent.vue'
import PageTitle from '@/components/PageTitleComponent.vue'
import {useRoute} from "vue-router";

// read meta parameters from the router
const route = useRoute();
const data: any = route.meta.data;
const qualifier = route.params.qualifier;
console.log(JSON.stringify(data))
;

</script>

<template>
  <Breadcrumb :paths="[{name: 'Elections', link: '/'}, {name: 'Elections ' + qualifier, link: '/elections/' + qualifier}]" />
  <div class="container-sm col-md-7 text-center">
    <PageTitle :title="`Elections ${qualifier}`"  />

    <div class="row notification">
      <div class="col-1 d-flex flex-column align-items-center justify-content-center">
        <img alt="New notification" src="@/assets/notifications/circle-new-notification.svg" width="30" height="30" />
      </div>
      <div class="date col-3 d-flex flex-column justify-content-center" aria-label="05 January 2024">
        <span>05</span>
        <span>GEN 24</span>
      </div>
      <div class="msg col d-flex flex-column justify-content-center">
        <p>CLOSING ELECTION</p>
        <p>The `KITTEN OR PUPPY` election will close at 6:00 pm. If you have not already voted, please do so as soon as possible.</p>
      </div>
    </div>

    <div class="row notification">
      <div class="col-1 d-flex flex-column align-items-center justify-content-center">
      </div>
      <div class="date col-3 d-flex flex-column justify-content-center" aria-label="05 January 2024">
        <span>05</span>
        <span>GEN 24</span>
      </div>
      <div class="msg col d-flex flex-column justify-content-center">
        <p>CLOSING ELECTION</p>
        <p>The `KITTEN OR PUPPY` election will close at 6:00 pm. If you have not already voted, please do so as soon as possible.</p>
      </div>
    </div>
  </div>
</template>

<style scoped>

div.notification {
  margin: 10px 0;
  padding: 10px;
  border-radius: 15px;
  box-shadow: 2px 5px 15px rgba(200, 200, 200, 0.82);
}

div.notification:nth-child(even) {
  background-color: rgba(0, 115, 230, 0.2);
}

div.notification:nth-child(odd) {
  background-color: rgba(217, 217, 217, 0.4);
}

div.date {
  color: #0d6efd;
  span:first-of-type {
    font-weight: bold;
    font-size: 2.5em;
  }
  span:nth-of-type(2) {
    font-weight: bold;
    font-size: 1.1em;
  }
}

div.msg {
  p:first-of-type {
    font-weight: bold;
    font-size: 1.1em;
    color: #e6308a;
    margin: 10px 0;
  }
  p:nth-of-type(2) {
    margin: 0 0 10px 0;
    text-align: left !important;
  }
}

</style>