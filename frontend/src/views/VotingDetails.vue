<script setup lang="ts">

import Breadcrumb from '@/components/BreadcrumbComponent.vue'
import PageTitle from '@/components/PageTitleComponent.vue'
import Tile from '@/components/tiles/TileComponent.vue'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faCircleInfo, faSquarePollHorizontal } from '@fortawesome/free-solid-svg-icons'

library.add(faCircleInfo, faSquarePollHorizontal);
</script>

<template>
  <Breadcrumb :paths="[{name: 'Dashboard', link: '/dashboard'}, {name: 'Details', link: '/voting/details'}]" />
  <div class="container-sm col-md-7 text-center">
    <PageTitle title="Voting details" />
    <div class="row">
      <div class="col">
        <Tile title="Opening Date" bigger="05 gen 24" medium="09:00 AM" />
        <Tile title="Closing Date" bigger="06 gen 24" medium="18:00 PM" />
        <Tile title="Turnout" bigger="42%" smaller="Turnout is updated real-time based on the number of eligible voters." />
      </div>
      <div class="col">
        <Tile title="Choices">
          <ul>
            <li>Choice 1</li>
            <li>Choice 2</li>
            <li>Choice 3</li>
            <li>Choice 4</li>
          </ul>
        </Tile>
        <Tile title="Results">
          Voting is still open, results will be made available after closing date.
        </Tile>
      </div>
    </div>
    <div class="row small">
      <p><font-awesome-icon icon="circle-info" /> All displayed times are based on the Rome, Italy timezone (GMT+1). </p>
    </div>
  </div>
</template>

<style scoped>
ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

div.col:first-of-type {
  .tile:nth-child(even) {
    background-color: rgba(0, 191, 125, 0.2);
  }

  .tile:nth-child(odd) {
    background-color: rgba(225, 218, 180, 0.2);
  }
}

div.col:last-of-type {
  .tile:nth-child(even) {
    background-color: rgba(225, 218, 180, 0.2);
  }

  .tile:nth-child(odd) {
    background-color: rgba(0, 191, 125, 0.2);
  }
}
</style>