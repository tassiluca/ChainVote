<template>
  <Breadcrumb :paths="[{name: 'Dashboard', link: '/dashboard'}]" />
  <PageTitle title="Dashboard" />
  <div v-for="qualifier in qualifiers" :class="`elections col-10 center mx-auto election election-${qualifier} bg-light`" :key="`div-${qualifier}`">
    <h2><a :href="`/elections?qualifier=${qualifier}`" class="election-link">{{ capitalizeFirstLetter(qualifier) }} Elections</a></h2>
    <hr v-if="getData(qualifier).length > 0"/>
    <Carousel :elections="sortElectionsByDate(getData(qualifier))"
              :time="now"
              @modalRaised="(id: number, name: string) => openModal(id, name)"
    />
    <RequestCodeModal :electionName="electionName" :electionId="electionId" :id="modalId" />
  </div>
</template>

<script setup lang="ts">
import Carousel from "@/components/CarouselComponent.vue";
import PageTitle from "@/components/PageTitleComponent.vue";
import Breadcrumb from "@/components/BreadcrumbComponent.vue";
import {computed, nextTick, onMounted, reactive, ref, type Ref} from "vue";
import router from "@/router";
import {useVotingStore, type Voting} from "@/stores/voting";
import {capitalizeFirstLetter, getStatus} from "@/commons/utils";
import * as bootstrap from "bootstrap";
import RequestCodeModal from "@/components/vote/RequestCodeModal.vue";

const votingStore = useVotingStore();
const data: Ref<Voting[] | null> = ref(null);

const modalId = ref("modal_vote")
const modal = ref()
const electionName = ref("")
const electionId = ref("")

onMounted(async () => {
  await getVotings();
  scheduleUpdateNow();
  await nextTick();
  modal.value = new bootstrap.Modal(`#${modalId.value}`, {})
});

function openModal(id: number, name: string) {
  electionName.value = name
  electionId.value = String(id)
  console.log(modal.value)
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

function sortElectionsByDate(elections: Voting[], prop: keyof Voting = 'start'): Voting[] {
  return elections.sort((a: Voting, b: Voting) => a[prop as keyof typeof a] - b[prop as keyof typeof b]);
}

const qualifiers = ['open', 'closed', 'soon'];

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

function getData(qualifier: string) {
  switch (qualifier) {
    case 'open':
      return getOpen.value;
    case 'closed':
      return getClosed.value;
    case 'soon':
      return getSoon.value;
    default:
      return [];
  }
}

</script>

<style>
  .elections {
    margin: 4% 0;
  }

  .elections h2 {
    font-size: 1.6em;
  }

  .election-link {
    color: black;
    text-decoration: none;
  }

  .election-link:hover {
    text-decoration: none;
  }

  hr {
    border: solid;
    border-color: inherit;
  }

  .election {
    margin: 2% 0;
    border-radius: 15px;
    box-shadow: 3px 3px 10px rgba(200, 200, 200, 0.82);
    padding: 2%;
    button {
      color: black;
    }
  }
</style>

<style lang="scss">
  $color-open: #009f00;
  .election-open {
    border: $color-open 2px solid;
    a:hover {
      color: $color-open;
    }
    button:hover {
      color: $color-open;
    }
  }
  $color-closed: #c70224;
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