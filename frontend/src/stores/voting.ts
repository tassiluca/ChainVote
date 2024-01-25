import {defineStore} from "pinia";
import {useAuthStore} from "@/stores/auth";
import {apiEndpoints} from "@/commons/globals";
import axios from "axios";

export interface Choice {
  name: string;
}

export interface Voting {
  id: number;
  goal: string;
  voters: number;
  start: Date;
  end: Date;
  choices: Choice[];
  turnout: string;
  results: Record<string, number>;
}

export const useVotingStore = defineStore('voting', () => {

  const authStore = useAuthStore();

  async function getVotingBy(id: string): Promise<Voting> {
    const urlInfos = `${apiEndpoints.API_SERVER}/election/info/detail/${id}`;
    const urlDetails = `${apiEndpoints.API_SERVER}/election/detail/${id}`;
    const electionDetailsResponse = await axios.get(
      urlDetails,
      { headers : { 'Authorization': `Bearer ${authStore.accessToken}` }}
    );
    const electionInfosResponse = await axios.get(
      urlInfos,
      { headers : { 'Authorization': `Bearer ${authStore.accessToken}` }}
    );
    return toVoting(electionInfosResponse, electionDetailsResponse);
  }

  function toVoting(electionInfos: any, electionDetails: any): Voting {
    return {
      id: electionInfos.data.data.electionId,
      goal: electionInfos.data.data.goal,
      voters: electionInfos.data.data.voters,
      start: new Date(electionInfos.data.data.startDate),
      end: new Date(electionInfos.data.data.endDate),
      choices: electionInfos.data.data.choices.map((i: any) => ({ name: i.choice })),
      turnout: electionDetails.data.data.affluence,
      results: electionDetails.data.data.results
    }
  }

  return { getVotingBy };
});