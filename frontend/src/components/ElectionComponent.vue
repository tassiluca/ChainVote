<template>
  <table>
    <tr>
      <td>
        <div class="msg text-center">
          <div class="row">
            <p class="name"><a :href="`/election/${election.id}`" class="name">{{ election.goal }}</a></p>
          </div>
          <div class="row">
            <p class="links-container">
              <a :href="`/elections/${election.id}`" class="useful-link">Details</a>
              <a :href="`/vote/${election.id}`" class="useful-link" v-if="isOpen(election)">Vote</a>
            </p>
          </div>
        </div>
      </td>
      <td>
        <div class="date" aria-label="start date">
          <span class="first"><svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" class="bi bi-hourglass-top" viewBox="0 0 16 16">
            <path d="M2 14.5a.5.5 0 0 0 .5.5h11a.5.5 0 1 0 0-1h-1v-1a4.5 4.5 0 0 0-2.557-4.06c-.29-.139-.443-.377-.443-.59v-.7c0-.213.154-.451.443-.59A4.5 4.5 0 0 0 12.5 3V2h1a.5.5 0 0 0 0-1h-11a.5.5 0 0 0 0 1h1v1a4.5 4.5 0 0 0 2.557 4.06c.29.139.443.377.443.59v.7c0 .213-.154.451-.443.59A4.5 4.5 0 0 0 3.5 13v1h-1a.5.5 0 0 0-.5.5m2.5-.5v-1a3.5 3.5 0 0 1 1.989-3.158c.533-.256 1.011-.79 1.011-1.491v-.702s.18.101.5.101.5-.1.5-.1v.7c0 .701.478 1.236 1.011 1.492A3.5 3.5 0 0 1 11.5 13v1z"/>
            </svg> {{ ("0" + election.start.getUTCDate()).slice(-2) }}
          </span><br/>
          <span>{{ election.start.toLocaleString('default', { month: 'short' }) }} {{election.start.getFullYear() }}</span><br/>
          <span>{{ ("0" + election.start.getHours()).slice(-2) }}:{{ ("0" + election.start.getMinutes()).slice(-2) }}</span>
        </div>
      </td>
      <td>
        <div class="date" aria-label="end date">
          <span class="first date"><svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" class="bi bi-hourglass-bottom" viewBox="0 0 16 16">
            <path d="M2 1.5a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-1v1a4.5 4.5 0 0 1-2.557 4.06c-.29.139-.443.377-.443.59v.7c0 .213.154.451.443.59A4.5 4.5 0 0 1 12.5 13v1h1a.5.5 0 0 1 0 1h-11a.5.5 0 1 1 0-1h1v-1a4.5 4.5 0 0 1 2.557-4.06c.29-.139.443-.377.443-.59v-.7c0-.213-.154-.451-.443-.59A4.5 4.5 0 0 1 3.5 3V2h-1a.5.5 0 0 1-.5-.5m2.5.5v1a3.5 3.5 0 0 0 1.989 3.158c.533.256 1.011.791 1.011 1.491v.702s.18.149.5.149.5-.15.5-.15v-.7c0-.701.478-1.236 1.011-1.492A3.5 3.5 0 0 0 11.5 3V2z"/>
            </svg> {{ ("0" + election.end.getUTCDate()).slice(-2) }}
          </span><br/>
          <span>{{ election.end.toLocaleString('default', { month: 'short' }) }} {{election.end.getFullYear() }}</span><br/>
          <span>{{ ("0" + election.end.getHours()).slice(-2) }}:{{ ("0" + election.end.getMinutes()).slice(-2) }}</span>
        </div>
      </td>
    </tr>
  </table>
</template>

<script setup lang="ts">
import type {VotingWithStatus} from "@/stores/voting";

function isOpen(election: VotingWithStatus): boolean {
  return election.status === 'open';
}
defineProps<{
  election: VotingWithStatus
}>()
</script>

<style scoped>

table td + td {
  border-left: 2px solid red;
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

p.name {
  font-weight: bold;
  font-size: 1.1em;
  color: #e6308a;
  margin: 4% 0;
}

p.links-container {
  display: flex;
  justify-content: space-between;
  padding: 0 15%;
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

</style>