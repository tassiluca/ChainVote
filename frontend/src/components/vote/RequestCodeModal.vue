<script setup lang="ts">
import { ref, onBeforeMount } from 'vue'
import axios from 'axios'
import { apiEndpoints } from "@/commons/globals";

const { electionName, electionId } = defineProps<{
  electionName: string,
  electionId: string,
}>()

const validationMessage = ref("")
const requestError = ref(false)
const codeRequest = ref(false)
const submit = ref(false)
const code = ref("")

function sendCodeRequest(id: string) {
  submit.value = true
  axios.post(`${apiEndpoints.API_SERVER}/code/generate`, { electionId: id })
  .then((response) => {
    code.value = response.data.data
    console.log(code.value)
    codeRequest.value = true
    requestError.value = false
  }).catch((error) => {
    console.log(error);
    codeRequest.value = true
    requestError.value = true
    validationMessage.value = error.response.data.error.message;
  }).finally(() => {
    submit.value = false
  });
}

</script>

<template>
  <div class="modal fade" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1">
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
              <p>Already have a code? <a :href="`/insert-code/${electionId}`">Vote now</a></p>
            </div>
            <div class="row mb-2">
              <form @submit.prevent="sendCodeRequest(electionId)">
                <div class="input-group mb-3 has-validation">
                  <input type="text" class="form-control" aria-label="Code output" v-model="code" readonly>
                  <div class="input-group-append">
                    <button class="btn btn-outline-primary" type="submit">
                      Request code
                      <span v-if="submit" class="spinner-border spinner-border-sm text-light"></span>
                    </button>
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

<style scoped>
    .success {
        color: #31B90F;
    }

    .fail {
        color: #B9310F;
    }
</style>