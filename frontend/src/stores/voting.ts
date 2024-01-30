import {defineStore} from "pinia";
import {useAuthStore} from "@/stores/auth";
import {apiEndpoints} from "@/commons/globals";
import axios from "axios";
import { ref } from 'vue'

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

export interface VotingWithStatus extends Voting {
  status?: string
}

export interface VotingCreation {
  goal: string
  voters: string
  startDate: string
  endDate: string
  choices: string[]
}

export const useVotingStore = defineStore('voting', () => {

  const authStore = useAuthStore();

  const otpInUse = ref(sessionStorage.getItem("otpInUse"));
  const otpInUseElectionId = ref(sessionStorage.getItem("otpInUseElectionId"));

  async function getVotingBy(id: string): Promise<Voting> {
    const urlInfos = `${apiEndpoints.API_SERVER}/election/info/detail/${id}`;
    const urlDetails = `${apiEndpoints.API_SERVER}/election/detail/${id}`;
    const electionDetailsResponse = await axios.get(urlDetails);
    const electionInfosResponse = await axios.get(urlInfos);
    return toVoting(electionInfosResponse.data.data, electionDetailsResponse.data.data);
  }

  async function getVotings(): Promise<Voting[]> {
    const urlInfos = `${apiEndpoints.API_SERVER}/election/info/all`;
    const urlDetails = `${apiEndpoints.API_SERVER}/election/all`;
    const electionDetailsResponse = await axios.get(urlDetails);
    const electionInfosResponse = await axios.get(urlInfos);
    const votings: Voting[] = [];
    for (const election of electionInfosResponse.data.data) {
      election.details = electionDetailsResponse.data.data.find((i: any) => i.id === election.electionId);
      votings.push(toVoting(election, election.details));
    }
    return votings;
  }

  function toVoting(electionInfos: any, electionDetails: any): Voting {
    return {
      id: electionInfos.electionId,
      goal: electionInfos.goal,
      voters: electionInfos.voters,
      start: new Date(electionInfos.startDate),
      end: new Date(electionInfos.endDate),
      choices: electionInfos.choices.map((i: any) => ({ name: i.choice })),
      turnout: electionDetails.affluence,
      results: electionDetails.results
    }
  }

  async function createVoting(voting: VotingCreation): Promise<{ success: boolean, msg: string }> {
    const urlInfo = `${apiEndpoints.API_SERVER}/election/info`;
    const urlVoting = `${apiEndpoints.API_SERVER}/election`;
    const responseInfo = await axios.post(
        urlInfo,
        voting,
        {headers: {'Authorization': `Bearer ${authStore.accessToken}`}}
    );
    if (responseInfo.status !== 201) {
      return {success: false, msg: responseInfo.data.message};
    } else {
      // Election info created, now create the election
      const responseVoting = await axios.post(
          urlVoting,
          responseInfo.data.data,
          {headers: {'Authorization': `Bearer ${authStore.accessToken}`}}
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
      } else {
        return {success: true, msg: `Election ${responseInfo.data.data.electionId} created successfully!`};
      }
    }
  }

  async function getElectionInfo(electionId: string) {
    const url = `${apiEndpoints.API_SERVER}/election/info/detail/${electionId}`;
    const response = await axios.get(url);

    if (response.status !== 200) {
      throw new Error('Error during election info retrieval');
    }

    return response.data.data;
  }

  function setOtpInUse(otp: string) {
    sessionStorage.setItem("otpInUse", otp);
  }

  function setOtpInUseElectionId(electionId: string) {
    sessionStorage.setItem("otpInUseElectionId", electionId);
  }

  function getOtpInUse(): string | null {
    return otpInUse.value;
  }

  function getOtpInUseElectionId(): string | null {
    return otpInUseElectionId.value;
  }

  return { getVotingBy, getVotings, createVoting, setOtpInUse, getOtpInUse, getElectionInfo, setOtpInUseElectionId, getOtpInUseElectionId }
});