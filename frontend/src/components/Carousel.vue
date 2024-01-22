<template>
  <carousel v-bind="settings" :breakpoints="breakpoints">
    <slide v-for="election in elections" :key="election.id">
      <election-card-component
          :election="election"
      />
    </slide>
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
      <navigation v-if="slidesCount > 1" />
    </template>
  </carousel>
</template>

<script setup lang="ts">

defineProps<{
  elections: Election[]
}>()
</script>

<script lang="ts">
import {defineComponent} from 'vue'
import { Carousel, Navigation, Slide } from 'vue3-carousel'
import ElectionCardComponent from "@/components/ElectionCard.vue";
import 'vue3-carousel/dist/carousel.css'

interface Election {
  id: string,
  name: string,
  start: Date,
  end: Date,
  affluence: string,
}

export default defineComponent({
  name: 'CarouselComponent',
  components: {
    ElectionCardComponent,
    Carousel,
    Slide,
    Navigation,
  },
  data: () => ({
    // carousel settings
    settings: {
      itemsToShow: 1,
      snapAlign: 'center',
      itemsToScroll: 1,
    },
    // breakpoints are mobile first
    // any settings not specified will fallback to the carousel settings
    breakpoints: {
      // 700px and up
      768: {
        itemsToShow: 2,
        snapAlign: 'center',
        itemsToScroll: 2,
      },
      // 1024 and up
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
    },
  })
})
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