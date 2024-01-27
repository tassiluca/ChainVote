<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth';
import axios from "axios"


const { electionName, electionId } = defineProps<{ 
    electionName: string, 
    electionId: number,
}>()

const codeRequest = ref(false)
const requestError = ref(false)
const validationMessage = ref("")

function sendCodeRequest() {
    axios.post("http://localhost:8080/code/generate", {
        userId: uid,
        electionId: electionId
    }).then((response) => {
        code.value = response.data.data
        codeRequest.value = true
        requestError.value = false
    }).catch((error) => {
        codeRequest.value = true
        requestError.value = true
        validationMessage.value = error.response.data.error.message
    });
}



</script>

<template>
    <div class="modal fade" id="modal_vote" tabindex="-1">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Vote for {{ electionName }}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="container-fluid">
                        <div class="row mb-2">
                            <p>
                                To access "{{ electionName }}" voting, it is necessary to apply for a One Time Code (OTC).  Once the
                                generation procedure is completed the following account will no longer be able to request additional codes for the following ballot.
                                The code is strictly bounded to the following election and it can't be used elsewhere.
                            </p>
                        </div>
                        <div class="row mb-2">
                            <p>Already have a code? <a href="#toElectionPage">Vote now</a></p>
                        </div>
                        <div class="row mb-2">
                            <form @submit.prevent="sendCodeRequest">
                                <div class="input-group mb-3 has-validation">
                                    <input type="text" class="form-control" aria-label="Code output" v-model="code" readonly>
                                    <div class="input-group-append">
                                        <button class="btn btn-outline-primary" type="submit">Request code</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                        <div class="row mb-2" v-if="codeRequest">
                            <p v-if="requestError" class="fail"> Error while generating code: "{{ validationMessage }}"</p>
                            <p v-else class="success">
                                The code was successfully generated for the following election.
                                The first part is showed above and the second part will be sent to the email address associated with the following account.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>