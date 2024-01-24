<script setup lang="ts">

import Breadcrumb from '@/components/BreadcrumbComponent.vue'
import PageTitle from '@/components/PageTitleComponent.vue'
import {useRoute} from "vue-router";
import {computed, ref} from "vue";
import ElectionComponent from "@/components/ElectionComponent.vue";

// read meta parameters from the router
const route = useRoute();
const data: any = route.meta.data;

const qualifier: string = route.query.qualifier as string;
const picked = ref('all');

if (qualifier && ['all', 'open', 'closed', 'soon'].includes(qualifier)) {
  picked.value = qualifier;
}

function capitalizeFirstLetter(str: string) {
  if (str === '') {
    return str;
  }
  return str.charAt(0).toUpperCase() + str.slice(1);
}

interface Election {
  id: string,
  name: string,
  start: Date,
  end: Date,
  affluence: string,
  choices: [string]
}

function isOpen(election: Election): boolean {
  const now = new Date();
  return now >= election.start && now < election.end;
}

function isClosed(election: Election): boolean {
  const now = new Date();
  return now >= election.end;
}

function isSoon(election: Election): boolean {
  const now = new Date();
  return now < election.start;
}

const getAll = computed(() => {
  return Object.assign([], data);
});

const getOpen = computed(() => {
  return Object.assign([], data).filter((election: Election) => isOpen(election));
});

const getClosed = computed(() => {
  return Object.assign([], data).filter((election: Election) => isClosed(election));
});

const getSoon = computed(() => {
  return Object.assign([], data).filter((election: Election) => isSoon(election));
});

const query: string[] = ["all", "open", "closed", "soon"]

const getData = computed(() => {
  switch (picked.value) {
    case 'all':
      return getAll.value;
    case 'open':
      return getOpen.value;
    case 'closed':
      return getClosed.value;
    case 'soon':
      return getSoon.value;
    default:
      return getAll.value;
  }
});

const itemsPerPage = 10; // Set your desired number of items per page
const currentPage = ref(1);
const totalPages = computed(() => {
  return Math.ceil(getData.value.length / itemsPerPage);
})

const displayedElections = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return getData.value.slice(start, end);
});

function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
}

function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
}

function resetPage() {
  currentPage.value = 1;
}

</script>

<template>
  <Breadcrumb :paths="[{name: 'Elections', link: '/elections'}]" />
  <PageTitle title="Elections"/>
  <div class="radio-button my-3">
    <ul class="list-group list-group-horizontal">
      <li v-for="item in query" :key="`li-${item}`" class="list-group-item">
        <input type="radio" :id="item" :value="item" :aria-selected="`${picked===item}`" @click="resetPage" v-model="picked" />
        <label :for="item" class="px-2">{{capitalizeFirstLetter(item)}}</label>
      </li>
    </ul>
  </div>
  <div class="container-sm col-10 col-md-8 text-center">
    <div v-if="displayedElections.length > 0">
      <div v-for="election in displayedElections" :key="election.id" class="row election">
        <ElectionComponent :election="election"/>
      </div>
      <div class="pagination-buttons" v-if="totalPages>2">
        <button @click="prevPage" class="btn btn-primary" :disabled="currentPage === 1">&lt;</button>
        <button @click="nextPage" class="btn btn-primary" :disabled="currentPage === totalPages">&gt;</button>
        <br/>
        <p>{{currentPage}} / {{totalPages}}</p>
      </div>
    </div>
    <div v-else>
      <p class="no-election">No {{picked}} elections found.</p>
    </div>
  </div>
</template>

<style scoped>
div.radio-button {
  display: flex;
  justify-content: center;
}
.pagination-buttons {
  margin-top: 10px;
}
.no-election {
  font-weight: bold;
}
</style>