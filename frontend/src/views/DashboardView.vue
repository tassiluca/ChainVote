<template>
  <Breadcrumb :paths="[{name: 'Dashboard', link: '/dashboard'}]" />
  <PageTitle title="Dashboard" />
  <div v-for="qualifier in qualifiers" :class="`elections col-10 center mx-auto election election-${qualifier} bg-light`" :key="`div-${qualifier}`">
    <a :href="`/elections?qualifier=${qualifier}`" class="election-link">{{ capitalizeFirstLetter(qualifier) }} Elections</a>
    <hr v-if="getData(qualifier).length > 0"/>
    <Carousel :elections="getData(qualifier)"/>
  </div>
</template>

<script setup lang="ts">
import Carousel from "@/components/CarouselComponent.vue";
import PageTitle from "@/components/PageTitleComponent.vue";
import Breadcrumb from "@/components/BreadcrumbComponent.vue";
import {computed, onMounted, ref, type Ref} from "vue";
import router from "@/router";
import {useVotingStore, type Voting} from "@/stores/voting";

const votingStore = useVotingStore();
const data: Ref<Voting[] | null> = ref(null);

onMounted(async () => {
  await getVotings();
});

async function getVotings() {
  try {
    data.value = await votingStore.getVotings();
  } catch (e: any) {
    console.error(e);
    await router.push({name: "not-found"})
  }
}

function capitalizeFirstLetter(str: string) {
  if (str === '') {
    return str;
  }
  return str.charAt(0).toUpperCase() + str.slice(1);
}

function sortElectionsByDate(elections: Voting[], prop: keyof Voting = 'start'): Voting[] {
  return elections.sort((a: Voting, b: Voting) => a[prop as keyof typeof a] - b[prop as keyof typeof b]);
}

function isOpen(election: Voting): boolean {
  const now = new Date();
  return now >= election.start && now < election.end;
}

function isClosed(election: Voting): boolean {
  const now = new Date();
  return now >= election.end;
}

function isSoon(election: Voting): boolean {
  const now = new Date();
  return now < election.start;
}

const qualifiers = ['open', 'closed', 'soon'];

const getOpen = computed(() => {
  return sortElectionsByDate(Object.assign([], data.value).filter((election: Voting) => isOpen(election)));
});

const getClosed = computed(() => {
  return sortElectionsByDate(Object.assign([], data.value).filter((election: Voting) => isClosed(election)));
});

const getSoon = computed(() => {
  return sortElectionsByDate(Object.assign([], data.value).filter((election: Voting) => isSoon(election)));
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
  .election-link {
    color: black;
    font-weight: bold;
    text-decoration: none;
  }

  .election-link:hover {
    text-decoration: none;
  }

  hr {
    border: solid;
    border-color: inherit;
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