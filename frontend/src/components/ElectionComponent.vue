<template>
  <div class="row election">
    <div class="date col-3 d-flex flex-column justify-content-center" aria-label="start and end dates">
      <span class="first"><svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" class="bi bi-hourglass-top" viewBox="0 0 16 16">
        <path d="M2 14.5a.5.5 0 0 0 .5.5h11a.5.5 0 1 0 0-1h-1v-1a4.5 4.5 0 0 0-2.557-4.06c-.29-.139-.443-.377-.443-.59v-.7c0-.213.154-.451.443-.59A4.5 4.5 0 0 0 12.5 3V2h1a.5.5 0 0 0 0-1h-11a.5.5 0 0 0 0 1h1v1a4.5 4.5 0 0 0 2.557 4.06c.29.139.443.377.443.59v.7c0 .213-.154.451-.443.59A4.5 4.5 0 0 0 3.5 13v1h-1a.5.5 0 0 0-.5.5m2.5-.5v-1a3.5 3.5 0 0 1 1.989-3.158c.533-.256 1.011-.79 1.011-1.491v-.702s.18.101.5.101.5-.1.5-.1v.7c0 .701.478 1.236 1.011 1.492A3.5 3.5 0 0 1 11.5 13v1z"/>
        </svg> {{ ("0" + election.start.getUTCDate()).slice(-2) }}
      </span>
      <span>{{ election.start.toLocaleString('default', { month: 'short' }) }} {{election.start.getFullYear() }}</span>
      <span>{{ ("0" + election.start.getHours()).slice(-2) }}:{{ ("0" + election.start.getMinutes()).slice(-2) }}</span>
      <hr/>
      <span class="first"><svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" class="bi bi-hourglass-bottom" viewBox="0 0 16 16">
        <path d="M2 1.5a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-1v1a4.5 4.5 0 0 1-2.557 4.06c-.29.139-.443.377-.443.59v.7c0 .213.154.451.443.59A4.5 4.5 0 0 1 12.5 13v1h1a.5.5 0 0 1 0 1h-11a.5.5 0 1 1 0-1h1v-1a4.5 4.5 0 0 1 2.557-4.06c.29-.139.443-.377.443-.59v-.7c0-.213-.154-.451-.443-.59A4.5 4.5 0 0 1 3.5 3V2h-1a.5.5 0 0 1-.5-.5m2.5.5v1a3.5 3.5 0 0 0 1.989 3.158c.533.256 1.011.791 1.011 1.491v.702s.18.149.5.149.5-.15.5-.15v-.7c0-.701.478-1.236 1.011-1.492A3.5 3.5 0 0 0 11.5 3V2z"/>
        </svg> {{ ("0" + election.end.getUTCDate()).slice(-2) }}
      </span>
      <span>{{ election.end.toLocaleString('default', { month: 'short' }) }} {{election.end.getFullYear() }}</span>
      <span>{{ ("0" + election.end.getHours()).slice(-2) }}:{{ ("0" + election.end.getMinutes()).slice(-2) }}</span>
    </div>
    <div class="vl col-1 d-flex flex-column"></div>
    <div class="msg col d-flex flex-column text-center">
      <p><a :href="`/election/${election.id}`" class="name">{{ election.goal }}</a></p>
      <p>{{ election.goal }} has actually <span class="attribute">{{ election.turnout }}%</span> affluence.
        <br/>Admissible choices are: <span class="attribute">{{election.choices.map((choice: Choice) => choice.name).join(', ')}}</span>.
      </p>
      <p>
        <a :href="`/election/${election.id}`" class="useful-link">Details</a>
        <a :href="`/vote/${election.id}`" class="useful-link" v-if="isOpen(election)">Vote</a>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import type {Choice, Voting} from "@/stores/voting";

function isOpen(election: Voting): boolean {
  const now = new Date();
  return now >= election.start && now < election.end;
}
defineProps<{
  election: Voting
}>()
</script>

<style scoped>

div.election {
  margin: 10px 0;
  padding: 10px;
  border-radius: 15px;
  box-shadow: 2px 5px 15px rgba(200, 200, 200, 0.82);
}

div.election:nth-child(even) {
  background-color: rgba(0, 115, 230, 0.2);
}

div.election:nth-child(odd) {
  background-color: rgba(217, 217, 217, 0.4);
}

div.date {
  color: #0d6efd;
  span.first {
    font-weight: bold;
    font-size: 2em;
  }
  span {
    font-weight: bold;
    font-size: 1.1em;
  }
}

div.msg {
  p:first-of-type {
    font-weight: bold;
    font-size: 1.1em;
    color: #e6308a;
    margin: 10px 0;
  }
  p:nth-of-type(2) {
    margin: 0 0 10px 0;
    text-align: left !important;
  }
}

span.attribute {
  font-weight: bold;
  font-size: 1.1em;
  margin: 10px 0;

}

a {
  color: #e6308a;
  text-decoration: none;
}

a.useful-link {
  margin: 0 10px;

}

a:hover {
  text-decoration: underline;
}

.vl {
  border-left: 2px solid #e6308a;
}

</style>