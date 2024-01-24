<template>
  <Carousel v-bind="settings" :breakpoints="breakpoints">
    <Slide v-for="election in elections" :key="election.id">
      <ElectionCard :election="election"/>
    </Slide>
    <template #pagination="{ pagesCount, currentPage, setCurrentPage }">
      <div class="pagination">
        <button
          v-for="page in pagesCount"
          :key="page"
          :class="{ active: page === currentPage }"
          @click="setCurrentPage(page)"
        >
          {{ page }}
        </button>
      </div>
    </template>
    <template #addons="{ slidesCount }">
      <Navigation v-if="slidesCount > 1" />
    </template>
  </Carousel>
</template>

<script setup lang="ts">
import ElectionCard from "@/components/ElectionCardComponent.vue";
import { Carousel, Navigation, Slide } from 'vue3-carousel'
import 'vue3-carousel/dist/carousel.css'

interface Election {
  id: string,
  name: string,
  start: Date,
  end: Date,
  affluence: string,
}

defineProps<{
  elections: [Election]
}>()

const settings = {
  itemsToShow: 1,
  snapAlign: 'center',
  itemsToScroll: 1,
}

const breakpoints = {
  768: {
    itemsToShow: 2,
        snapAlign: 'center',
        itemsToScroll: 2,
  },
  992: {
    itemsToShow: 3,
        snapAlign: 'start',
        itemsToScroll: 3,
  },
  1200: {
    itemsToShow: 4,
        snapAlign: 'start',
        itemsToScroll: 4,
  },
}
</script>

<style>
.carousel__item {
  min-height: 200px;
  width: 100%;
  background-color: var(--vc-clr-primary);
  color: var(--vc-clr-white);
  font-size: 20px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.carousel__slide {
  padding: 10px;
}

.carousel__prev,
.carousel__next {
  box-sizing: content-box;
  margin-left: 0;
  margin-right: 0;
  border: none;
}
</style>