<script setup lang="ts">

import Breadcrumb from '@/components/BreadcrumbComponent.vue'
import PageTitle from '@/components/PageTitleComponent.vue'
import {useRoute} from "vue-router";
import {computed, nextTick, onMounted, reactive, type Ref, ref} from 'vue'
import ElectionComponent from "@/components/ElectionComponent.vue";
import {useVotingStore, type Voting} from "@/stores/voting";
import router from "@/router";
import {capitalizeFirstLetter, getStatus} from "@/commons/utils";
import * as bootstrap from "bootstrap";
import RequestCodeModal from "@/components/vote/RequestCodeModal.vue";

const votingStore = useVotingStore();
const data: Ref<Voting[] | null> = ref(null);

const modalId = ref("modal_vote_view")
const modal = ref()
const electionName = ref("")
const electionId = ref("")
const refKey = ref(0)

onMounted(async () => {
  await getVotings();
  scheduleUpdateNow();
  await nextTick();
  modal.value = new bootstrap.Modal(`#${modalId.value}`, {});
})

function openModal(id: string, name: string) {
  electionName.value = name
  electionId.value = id
  if (modal.value) {
    modal.value.show()
  }
}

const now = ref(new Date().getTime());

function scheduleUpdateNow() {
  setTimeout(updateNow, 1000);
}

function updateNow() {
  now.value = new Date().getTime();
  scheduleUpdateNow();
}

async function getVotings() {
  try {
    data.value = await votingStore.getVotings();
  } catch (e: any) {
    console.error(e);
    await router.push({name: "not-found"})
  }
}
// read meta parameters from the router
const route = useRoute();
const picked = ref('all');

if (route.query.qualifier && ['all', 'open', 'closed', 'soon'].includes(route.query.qualifier as string)) {
  picked.value = route.query.qualifier as string;
}

function sortElectionsByDate(elections: Voting[], prop: keyof Voting = 'start'): Voting[] {
  return elections.sort((a: Voting, b: Voting) => a[prop as keyof typeof a] - b[prop as keyof typeof b]);
}

const getAll = computed(() => {
  if (reactiveVotings.value) {
    return reactiveVotings.value
  } else {
    return []
  }
});

const reactiveVotings = computed(() => {
  if (!data.value) return [];
  return data.value.map(voting => reactive(voting));
});

const getOpen = computed(() => {
  if (reactiveVotings.value) {
    return reactiveVotings.value.filter((election: Voting) => getStatus(election, now.value) === 'open')
  } else {
    return []
  }
});

const getClosed = computed(() => {
  if (reactiveVotings.value) {
    return reactiveVotings.value.filter((election: Voting) => getStatus(election, now.value) === 'closed')
  } else {
    return []
  }
});

const getSoon = computed(() => {
  if (reactiveVotings.value) {
    return reactiveVotings.value.filter((election: Voting) => getStatus(election, now.value) === 'soon')
  } else {
    return []
  }
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
  return sortElectionsByDate(getData.value).slice(start, end);
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
  <div class="container-sm col-12 col-md-8 text-center">
    <div v-if="displayedElections.length > 0">
      <div v-for="election in displayedElections" :key="String(election.id)" class="row election">
        <ElectionComponent :election="election" :time="now"  @openModal="(id: string, name: string) => openModal(id, name)"
        />
        <RequestCodeModal :electionName="electionName" :electionId="electionId" :id="modalId"/>
      </div>
      <div class="pagination-buttons" v-if="totalPages>1">
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
.election {
  background-color: #f8f9fa;
}
</style>