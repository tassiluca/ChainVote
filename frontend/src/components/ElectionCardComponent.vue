<template>
  <div class="card">
    <div class="card-body">
      <h5 class="card-title"><a :href="`/elections/${election.id}`">{{ election.goal }}</a></h5>
      <hr class="solid"/>
      <ul class="election-props">
        <li>
          <strong>Start:</strong> {{ ("0" + election.start.getUTCDate()).slice(-2) }} {{ election.start.toLocaleString('default', { month: 'short' }) }} {{election.start.getFullYear() }} {{ election.start.getHours() }}:{{ ("0" + election.start.getMinutes()).slice(-2) }}
        </li>
        <li>
          <strong>End:</strong> {{ ("0" + election.end.getUTCDate()).slice(-2) }} {{ election.end.toLocaleString('default', { month: 'short' }) }} {{election.end.getFullYear() }} {{ election.end.getHours() }}:{{ ("0" + election.end.getMinutes()).slice(-2) }}
        </li>
      </ul>
      <div class="d-flex flex-column links">
        <a :href="`/elections/${election.id}`">See details</a>
        <a :href="`/vote/${election.id}`">Cast a vote</a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

import type {VotingWithStatus} from "@/stores/voting";

defineProps<{
  election: VotingWithStatus
}>()

function isOpen(election: VotingWithStatus): boolean {
  return election.status === 'open';
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