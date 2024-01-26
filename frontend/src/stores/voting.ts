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

export interface VotingCreation {
  goal: string
  voters: number
  startDate: string
  endDate: string
  choices: string[]
}

export const useVotingStore = defineStore('voting', () => {

  const authStore = useAuthStore();

  async function getVotingBy(id: string): Promise<Voting> {
    const urlInfos = `${apiEndpoints.API_SERVER}/election/info/detail/${id}`;
    const urlDetails = `${apiEndpoints.API_SERVER}/election/detail/${id}`;
    const electionDetailsResponse = await axios.get(
      urlDetails,
      { headers : { 'Authorization': `Bearer ${authStore.accessToken()}` }}
    );
    const electionInfosResponse = await axios.get(
      urlInfos,
      { headers : { 'Authorization': `Bearer ${authStore.accessToken()}` }}
    );
    return toVoting(electionInfosResponse, electionDetailsResponse);
  }

  async function getVotings(): Promise<Voting[]> {
    const urlInfos = `${apiEndpoints.API_SERVER}/election/info/all`;
    const urlDetails = `${apiEndpoints.API_SERVER}/election/all`;
    const electionDetailsResponse = await axios.get(
        urlDetails,
        { headers : { 'Authorization': `Bearer ${authStore.accessToken()}` }}
    );
    const electionInfosResponse = await axios.get(
        urlInfos,
        { headers : { 'Authorization': `Bearer ${authStore.accessToken()}` }}
    );

    const votings: Voting[] = [];

    for (const election of electionInfosResponse.data.data) {
      election.details = electionDetailsResponse.data.data.find((i: any) => i.electionId === election.electionId);
      votings.push(toVoting(election, election.details));
    }

    return votings;
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

  async function createVoting(voting: VotingCreation): Promise<{ success: boolean, msg: string }> {
    const urlInfo = `${apiEndpoints.API_SERVER}/election/info`;
    const urlVoting = `${apiEndpoints.API_SERVER}/election`;
    const responseInfo = await axios.post(
        urlInfo,
        voting,
        { headers : { 'Authorization': `Bearer ${authStore.accessToken()}` }}
    );
    if (responseInfo.status !== 200) {
        return {success: false, msg: responseInfo.data.message};
    } else {
      // Election info created, now create the election
      const responseVoting = await axios.post(
          urlVoting,
          responseInfo.data.data,
          { headers : { 'Authorization': `Bearer ${authStore.accessToken()}` }}
      );
      if (responseVoting.status !== 200) {
        // Something went wrong with creating election, delete the info
        const responseDelete = await axios.delete(
            urlInfo,
            responseInfo.data.data,
        );
        if (responseDelete.status !== 200) {
          // Something went wrong with deleting the info
          return {success: false, msg: 'Something went wrong. Please contact assistance.'};
        } else {
          // Successfully deleted the info
          return {success: false, msg: 'Something went wrong. Please try again.'};
        }
      }
        return {success: true, msg: responseInfo.data.message};
      }
    }

  return { getVotingBy, getVotings, createVoting };
});