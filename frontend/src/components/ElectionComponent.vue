<template>
  <div class="row">
    <div class="col msg text-center">
      <div class="row">
        <p class="name"><a :href="`/elections/${election.id}`" class="name">{{ election.goal }}</a></p>
      </div>
      <div class="d-flex justify-content-center flex-wrap">
        <a :href="`/elections/${election.id}`" class="useful-link">Details</a>
        <a :href="`/insert-code/${election.id}`" class="useful-link" v-if="isOpen(election) && authStore.userRole !== Role.Admin">Vote</a>
        <a class="useful-link" v-if="isOpen(election) && authStore.userRole !== Role.Admin" @click="$emit('openModal', election.id, election.goal)" href="#">Get code</a>
      </div>
    </div>
    <div class="col date my-auto" aria-label="start date">
      <span class="first">
        <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" class="bi bi-hourglass-top" viewBox="0 0 16 16">
          <path d="M2 14.5a.5.5 0 0 0 .5.5h11a.5.5 0 1 0 0-1h-1v-1a4.5 4.5 0 0 0-2.557-4.06c-.29-.139-.443-.377-.443-.59v-.7c0-.213.154-.451.443-.59A4.5 4.5 0 0 0 12.5 3V2h1a.5.5 0 0 0 0-1h-11a.5.5 0 0 0 0 1h1v1a4.5 4.5 0 0 0 2.557 4.06c.29.139.443.377.443.59v.7c0 .213-.154.451-.443.59A4.5 4.5 0 0 0 3.5 13v1h-1a.5.5 0 0 0-.5.5m2.5-.5v-1a3.5 3.5 0 0 1 1.989-3.158c.533-.256 1.011-.79 1.011-1.491v-.702s.18.101.5.101.5-.1.5-.1v.7c0 .701.478 1.236 1.011 1.492A3.5 3.5 0 0 1 11.5 13v1z"/>
        </svg> {{formatDate(election.start).substring(0, 2)}}
      </span>
      <br/>
      <span>{{capitalizeFirstLetter(formatDate(election.start, 'numeric').substring(3, 11))}}</span>
      <br/>
      <span>{{formatTime(election.start)}}</span>
    </div>
    <div class="col date my-auto" aria-label="end date">
      <span class="first date">
        <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" class="bi bi-hourglass-bottom" viewBox="0 0 16 16">
          <path d="M2 1.5a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-1v1a4.5 4.5 0 0 1-2.557 4.06c-.29.139-.443.377-.443.59v.7c0 .213.154.451.443.59A4.5 4.5 0 0 1 12.5 13v1h1a.5.5 0 0 1 0 1h-11a.5.5 0 1 1 0-1h1v-1a4.5 4.5 0 0 1 2.557-4.06c.29-.139.443-.377.443-.59v-.7c0-.213-.154-.451-.443-.59A4.5 4.5 0 0 1 3.5 3V2h-1a.5.5 0 0 1-.5-.5m2.5.5v1a3.5 3.5 0 0 0 1.989 3.158c.533.256 1.011.791 1.011 1.491v.702s.18.149.5.149.5-.15.5-.15v-.7c0-.701.478-1.236 1.011-1.492A3.5 3.5 0 0 0 11.5 3V2z"/>
        </svg> {{formatDate(election.end).substring(0, 2)}}
      </span>
      <br/>
      <span>{{capitalizeFirstLetter(formatDate(election.end, 'numeric').substring(3, 11))}}</span>
      <br/>
      <span>{{formatTime(election.end)}}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type {Voting} from "@/stores/voting";
import {capitalizeFirstLetter, formatDate, formatTime, getStatus, Role} from "@/commons/utils";
import {defineEmits, ref} from "vue";
import {useAuthStore} from "@/stores/auth";

const authStore = useAuthStore();

function isOpen(election: Voting): boolean {
  return getStatus(election, now.value) === 'open';
}

const props = defineProps<{
  election: Voting,
  time: number,
}>()

defineEmits<{
  openModal: [electionId: string, electionName: string]
}>();

const now = ref(props.time);
</script>

<style scoped>
div.date {
  color: #0d6efd;
  border-left: 2px solid #e6308a;
  span.first {
    font-weight: bold;
    font-size: 2em;
  }
  span {
    font-weight: bold;
    font-size: 1.1em;
  }
}

p.name {
  font-weight: bold;
  font-size: 1.2em;
  margin: 4% 0;
  text-transform: uppercase;
}

a {
  color: #e6308a;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}

a.useful-link {
  padding: 6px 10px;
  margin: 6px;
  border-radius: 15px;
  border: 1px solid #0d6efd;
  text-decoration: none;
  color: #0d6efd;
}

a.useful-link:hover {
  font-weight: bold;
  box-shadow: 1px 2px 5px rgba(200, 200, 200, 0.82);
}
</style>
