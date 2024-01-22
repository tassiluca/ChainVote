<template>
  <div class="card">
    <div class="card-body">
      <h5 class="card-title"><a :href="`/election/details/${election.id}`">{{ election.name }}</a></h5>
      <hr class="solid"/>
      <ul class="election-props">
        <li>
          <strong>Start:</strong> {{ election.start }}
<!--          <strong>Start:</strong> {{ election.start.getDay() }}/{{ election.start.getMonth() }}/{{ election.start.getFullYear() }} {{ election.start.getHours() }}:{{ election.start.getMinutes() }}-->
        </li>
        <li>
          <strong>End:</strong> {{ election.end.getDay() }}/{{ election.end.getMonth() }}/{{ election.end.getFullYear() }} {{ election.end.getHours() }}:{{ election.end.getMinutes() }}
        </li>
        <li>
          <strong>Affluence:</strong> {{ election.affluence }}
        </li>
        <li>
          <div class="card links mx-auto">
            <ul>
              <li>
                <a :href="`/election/details/${election.id}`">See details</a>
              </li>
              <li>
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