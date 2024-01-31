<template>
  <div class="card">
    <div class="card-body">
      <h5 class="card-title"><a :href="`/elections/${election.id}`">{{ election.goal }}</a></h5>
      <hr class="solid"/>
      <ul class="election-props">
        <li>
          <strong>Start:</strong> {{formatDate(election.start, 'numeric')}} <br/> {{ formatTime(election.start) }}
        </li>
        <li>
          <strong>End:</strong> {{formatDate(election.end, 'numeric')}} <br/> {{ formatTime(election.end) }}
        </li>
      </ul>
      <div class="d-flex flex-column links">
        <a :href="`/elections/${election.id}`">See details</a>
        <a v-if="isOpen(election) && authStore.userRole !== Role.Admin" :href="`/vote/${election.id}`">Cast a vote</a>
        <a v-if="isOpen(election) && authStore.userRole !== Role.Admin" href="#" @click="$emit('openModal', election.id, election.goal)">Get code</a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

import type {Voting} from "@/stores/voting";
import {ref, defineEmits} from "vue";
import {formatDate, formatTime, getStatus, Role} from "@/commons/utils";
import {useAuthStore} from "@/stores/auth";

const props = defineProps<{
  election: Voting,
  time: number,
}>()

defineEmits<{
  openModal: [electionId: number, electionName: string]
}>();

const authStore = useAuthStore();

const now = ref(props.time);

function isOpen(election: Voting): boolean {
  return getStatus(election, now.value) === 'open';
}
</script>

<style scoped>

  li {
    margin: 2% 0;
  }

  ul {
    list-style-type: none;
  }

  a {
    color: #007bff;
    text-decoration: none;
  }

  div.links a {
    padding: 6px 0;
    margin: 4px 0;
    border-radius: 15px;
    background-color: #edede9;
  }

  div.links a:hover {
    font-weight: bold;
    box-shadow: 1px 2px 5px rgba(200, 200, 200, 0.82);
  }

  .card {
    border-radius: 15px;
    padding: 2%;
  }

  .card-title {
    font-weight: bold;
  }

  .election-props {
    padding: 0;
  }
</style>