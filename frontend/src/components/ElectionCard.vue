<template>
  <div class="card" style="width: 18rem;">
    <div class="card-body">
      <h5 class="card-title">{{ election.name }}</h5>
      <p class="card-text">
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
        </ul>
      </p>
      <a href="#" class="btn btn-primary">Go somewhere</a>
    </div>
  </div>
</template>

<script lang="ts">
import {defineProps, toRefs} from 'vue'

interface Election {
  id: string,
  name: string,
  start: Date,
  end: Date,
  affluence: number,
}

const props = defineProps<{
  cardId: string,
  election: Election,
}>()

const { election } = toRefs(props);

export default {
  name: "ElectionCardComponent",
  data() {
    return {
      cardId: 'prova',
      election: {
        id: 'prova',
        name: 'prova1',
        start: new Date('01/03/23'),
        end: new Date('01/03/23'),
        affluence: 22,
      },
    }
  },
};
</script>

<style>
  .election-props {
    list-style-type: none;
    padding: 0;
  }
</style>