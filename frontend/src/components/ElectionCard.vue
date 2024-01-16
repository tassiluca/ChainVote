<template>
  <div class="card">
    <div class="card-body">
      <h5 class="card-title">{{ election.name }}</h5>
      <ul class="election-props">
        <li>
          <strong>Start:</strong> {{ election.start }}
        </li>
        <li>
          <strong>End:</strong> {{ election.end }}
        </li>
        <li>
          <strong>Affluence:</strong> {{ election.affluence }}
        </li>
        <li>
          <a :href="`/election/details/${election.id}`">See details</a>
        </li>
        <li>
          <a :href="`/election/vote/${election.id}`">Cast a vote</a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import {defineProps} from "vue";

defineProps<{
  election: Election,
}>()
</script>


<script lang="ts">

interface Election {
  id: string,
  name: string,
  start: Date,
  end: Date,
  affluence: number,
}

export default {
  name: "ElectionCardComponent",
};
</script>

<style>

  !important a {
    color: black;
  }

  .card {
    border-radius: 15px;
    padding: 2%;
  }

  .election-props {
    list-style-type: none;
    padding: 0;
  }
</style>