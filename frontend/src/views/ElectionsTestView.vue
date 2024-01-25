<script setup>
    import * as bootstrap from 'bootstrap'
    import { ref, onMounted } from 'vue'
    import { makeRequest } from '@/assets/utils'

    const modal = ref(null)
    const codeRequest = ref(false)
    const requestError = ref(false)
    const validationMessage = ref("")

    const electionName = "Test election"
    const uid = "65ab091c11f72779fb454ff7"
    const electionId = "2027020788"
    const jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjVhYjA5MWMxMWY3Mjc3OWZiNDU0ZmY3IiwiZW1haWwiOiJnaW9yaTIucHJvdmFAdGVzdC5pdCIsInBhc3N3b3JkIjoiJDJiJDEwJDQ5bFUvMzc1Y0RwWVpaNi8ueGdRdi5HL3BuaW1PUUQvN2pRTjlvbjk4RG1WVXlXUkx1bjdhIiwiZmlyc3ROYW1lIjoiR2lvcGFpbiIsInNlY29uZE5hbWUiOiJOb0dhaW4iLCJyb2xlIjoidXNlciIsIl9fdiI6MH0sImlhdCI6MTcwNTk1MjAzNywiZXhwIjoxNzA1OTUyOTM3LCJhdWQiOiJodHRwczovL3d3dy5jaGFpbnZvdGUuY29tIiwiaXNzIjoiQ2hhaW5Wb3RlIn0.xKYQsgBGYlXUSYQguoXoKsnRMtZrYCXnjf6vx6E84NlmmofcLbzui2Bxi-NoE9PxxQr8VpF89KNXtaDWYtRF9k6FDlkfPuO6yiDpFaeyLo44aNVZcRMvQQSj_TPIf0xB0D8rqxyF8aH4sFAZUxrOlGjP_pwKYZXvmsEQSnGR5Oc3lg_-bv_kpeV5PWPzep8FkI0-YhirzSPERXY7PoFnay1Y6boZbZmcWQQQV6MXxutRpCBumIO31MbHwoc6VVryKPJ2egM80i5UqX57puJ0hPA_MbS89R3UkS3csAFS8PwxLTxmIXmL03vqi72ZNlbV3qn4K4MPu8cfB--4ABzmSt-urDP5AIB5AAno_0O8NDLX5kuvq4kKbnXBSPbeA6jLKxvjwlIFLAN_znhfSuWoGhqQAusXyLPyXWjtEkfVvGMNHoWVEPZYjILI9p6xB9timHwvDtDUxT9B80mVy0T4sbSV9AknB163gVy1YFYfOjr-EZyyc3Z9IM3lrvSJNR5xF-8vDQy50g6R0pM5vngwVnsXIQH2z0fasfsLSAcemFYjgvMNxbWrmPualcQ-BiMtmQDo8jPj4BZmR-aH0_hWH_WEWquE-1To93RUeQISlqm7WuhwUSiH80s3Fp_kE70lF8HZyn2hX4rFovAfglkXY43ZyPrm1dmgH-b4Ved17nM";
     
    const code = ref("")
    onMounted(() => {
        modal.value = new bootstrap.Modal('#modal_vote', {})
    })

    function openModal()
    {
        modal.value.show()
    }
    
    function sendCodeRequest()
    {
        makeRequest("http://localhost:8080/code/generate", "POST", {
            userId: uid,
            electionId: electionId
        }, jwtToken).then((response) => {
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
    <div class="container">
        <header>
            <h1> Test page for code request </h1>
        </header>
        
        <button type="button" class="btn btn-primary" @click="openModal"> Launch demo modal </button>

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
                                <p>Already have a code?  <a href="#toElectionPage">Vote now</a></p>
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
```