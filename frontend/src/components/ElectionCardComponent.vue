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
        <li>
          <div class="card links mx-auto">
            <ul>
              <li>
                <a :href="`/elections/${election.id}`">See details</a>
              </li>
              <li v-if="isOpen(election)">
                <a :href="`/vote/${election.id}`">Cast a vote</a>
              </li>
            </ul>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">

import type {Voting} from "@/stores/voting";
import {ref} from "vue";
import {formatDate, formatTime, getStatus} from "@/commons/utils";

defineProps<{
  election: Voting
}>()

const now = ref(new Date().getTime());

function scheduleUpdateNow() {
  setTimeout(updateNow, 1000);
}

function updateNow() {
  now.value = new Date().getTime();
  scheduleUpdateNow();
}

scheduleUpdateNow();

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

  div.links {
    display: inline-block;
    padding: 4%;
  }

  .links ul {
    margin-left: 0;
    padding-left: 0;
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