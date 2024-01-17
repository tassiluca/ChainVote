<template>
  <div class="card">
    <div class="card-body">
      <h5 class="card-title">{{ election.name }}</h5>
      <ul class="election-props">
        <li>
          <strong>Start:</strong> {{ election.start.getDay() }}/{{ election.start.getMonth() }}/{{ election.start.getFullYear() }} {{ election.start.getHours() }}:{{ election.start.getMinutes() }}
        </li>
        <li>
          <strong>End:</strong> {{ election.end.getDay() }}/{{ election.end.getMonth() }}/{{ election.end.getFullYear() }} {{ election.end.getHours() }}:{{ election.end.getMinutes() }}
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
  affluence: string,
}

export default {
  name: "ElectionCardComponent",
};
</script>

<style>
  a {
    color: #007bff;
    text-decoration: none;
  }

  .card {
    border-radius: 15px;
    padding: 2%;
  }

  .card-title {
    font-weight: bold;
  }

  .election-props {
    list-style-type: none;
    padding: 0;
  }
</style>