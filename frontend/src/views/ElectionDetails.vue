<script setup lang="ts">
import router from "@/router";
import io from "socket.io-client";
import { library } from '@fortawesome/fontawesome-svg-core'
import { faCircleInfo, faSquarePollHorizontal } from '@fortawesome/free-solid-svg-icons'
import { onMounted, onUnmounted, type Ref, ref } from "vue";
import { useVotingStore, type Voting} from "@/stores/voting";
import { useAuthStore } from "@/stores/auth";
import { useRoute } from "vue-router";
import { formatDate, formatTime, highestOf } from "@/commons/utils";
import { apiEndpoints } from "@/commons/globals";
import Breadcrumb from '@/components/BreadcrumbComponent.vue'
import PageTitle from '@/components/PageTitleComponent.vue'
import Tile from '@/components/tiles/TileComponent.vue'
import BarChart from "@/components/charts/BarChart.vue";
import PieChart from "@/components/charts/PieChart.vue";

const socket = io(apiEndpoints.API_SERVER)
const votingStore = useVotingStore();
const authStore = useAuthStore();
const voting: Ref<Voting | null> = ref(null);
const route = useRoute();

library.add(faCircleInfo, faSquarePollHorizontal);

onMounted(async () => {
  if (!authStore.isLogged) {
    await router.push({name: "login"});
  } else {
    await getVotingDetails(route.params.id.toString());
    socket.emit("joinRoom", "election-" + voting.value?.id);
    socket.on("updateTurnout", (turnout: string) => {
      if (voting.value) {
        voting.value = {
          ...voting.value, // Shallow copy of the original object
          turnout: turnout,
        };
      }
    });
  }
});

onUnmounted(() => {
  socket.disconnect();
})

async function getVotingDetails(id: string) {
  try {
    voting.value = await votingStore.getVotingBy(id);
  } catch (e: any) {
    console.error(e);
    await router.push({name: "not-found"})
  }
}
</script>

<template>
  <Breadcrumb :paths="[{name: 'Dashboard', link: '/dashboard'}, {name: 'Details', link: '/voting/details'}]" />
  <div v-if="voting" class="container-sm col-md-7 text-center">
    <PageTitle title="Voting details" />
    <div class="row">
      <h2>
        Goal: {{ voting.goal }}
        <span v-if="Object.keys(voting.results).length === 0" class="badge bg-success small-badge">Open</span>
        <span v-else class="badge bg-dark">Closed</span>
      </h2>
      <p>Voting id: {{ voting.id }}</p>
    </div>
    <div class="row">
      <div class="col">
        <Tile title="Opening Date" :bigger="formatDate(voting.start)" :medium="formatTime(voting.start)" />
        <Tile title="Turnout" :bigger="voting.turnout" smaller="Turnout is updated real-time based on the number of eligible voters."/>
      </div>
      <div class="col">
        <Tile title="Closing Date" :bigger="formatDate(voting.end)" :medium="formatTime(voting.end)"/>
        <Tile title="Choices">
          <ul v-for="choice in voting.choices" :key="choice.name">
            <li>{{ choice.name }}</li>
          </ul>
        </Tile>
      </div>
    </div>
    <div class="row">
      <div class="col">
        <Tile title="Results">
          <template #default>
            <p v-if="Object.keys(voting.results).length !== 0">
              The option '<strong>{{ highestOf(voting.results).key }}</strong>' has collected the highest number of votes.
            </p>
            <p v-else>Voting is still open, results will be made available after closing date.</p>
          </template>
          <template #details v-if="Object.keys(voting.results).length !== 0">
            <p class="text-black">Results are here presented: </p>
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>Choice</th>
                  <th>Number of Votes</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(votes, choice) in voting.results" :key="choice">
                  <td>{{ choice }}</td>
                  <td>{{ votes }}</td>
                </tr>
              </tbody>
            </table>
            <BarChart
                :labels="Object.keys(voting.results)"
                :values="[{ 'title': 'Number of votes', 'label': 'votes no.', 'data': Object.values(voting.results) }]"
            />
            <PieChart
                :labels="Object.keys(voting.results)"
                :values="Object.values(voting.results)"
            />
          </template>
        </Tile>
      </div>
    </div>
    <div class="row small">
      <p><font-awesome-icon icon="circle-info" /> All displayed times are based on the Rome, Italy timezone (GMT+1). </p>
    </div>
  </div>
</template>

<style scoped>
ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

div.col:first-of-type {
  .tile:nth-child(even) {
    background-color: rgba(0, 191, 125, 0.2);
  }

  .tile:nth-child(odd) {
    background-color: rgba(225, 218, 180, 0.2);
  }
}

div.col:last-of-type {
  .tile:nth-child(even) {
    background-color: rgba(225, 218, 180, 0.2);
  }

  .tile:nth-child(odd) {
    background-color: rgba(0, 191, 125, 0.2);
  }
}
</style>
