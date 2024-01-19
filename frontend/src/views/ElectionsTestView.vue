<script setup>
    import * as bootstrap from 'bootstrap'
    import { ref, onMounted } from 'vue'
    import { makeRequest } from '@/assets/utils'

    const modal = ref(null)
    const codeRequest = ref(false)
    const requestError = ref(false)
    const validationMessage = ref("")

    const electionName = "Test election"
    //const uid = "65ab091c11f72779fb454ff7"
    //const electionId = "2027020788"
    //const jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjVhYjA5MWMxMWY3Mjc3OWZiNDU0ZmY3IiwiZW1haWwiOiJnaW9yaTIucHJvdmFAdGVzdC5pdCIsInBhc3N3b3JkIjoiJDJiJDEwJDQ5bFUvMzc1Y0RwWVpaNi8ueGdRdi5HL3BuaW1PUUQvN2pRTjlvbjk4RG1WVXlXUkx1bjdhIiwiZmlyc3ROYW1lIjoiR2lvcGFpbiIsInNlY29uZE5hbWUiOiJOb0dhaW4iLCJyb2xlIjoidXNlciIsIl9fdiI6MH0sImlhdCI6MTcwNTcwNzgxNiwiZXhwIjoxNzA1NzA4NzE2LCJhdWQiOiJodHRwczovL3d3dy5jaGFpbnZvdGUuY29tIiwiaXNzIjoiQ2hhaW5Wb3RlIn0.i19AzPc-jMaJ_B6BjHXrfNUREN13pu6vkgmmKnuCW_tzLgMx5SeUOFivBtw5ok8D2u5cj1toBfPC8tBwL4tLNam1yWb-QQUo4oQm51TiTD5vJsSURFo7Bvk85ko3uxVuATQNRnYbVfqb57Ws2cuGtKt_WVb_S_NCf9o91mCIrzDuV6xRTfK12on6CbnpLWew4UY6Nq5HKvk4I88sHLI89kN0kWBhSuOGTfD6cJ45iF0htDrSDNV_JCsNLGLEkzwGFmxZKOIBwSIIrm8qSa5HOZ0qesxunJb_7CYMyXp9UpXCV4Hj7764BbFLyRghtrS5_4M3234kS2_vlVZdDTXqxQAUfzi-VKSOLeX8bxCn99IF2v_8oC4Uhw7riIB1TIvz839vbzzFDPRimxsLDDusWRUsNEFppeOLz3__bzj0ra29hczjcx3haq0qzwmVMl26Q5UfkXn2nPXHCD8EWP4HnMuGdH9c4idRNgy4qKrMaymSitWyip9EjUwFwHS89wn1hBZavNKWZh-jRiUZ1Kbt_V0JkIU5LAQLR8xsHGR3KO4U6nE7161ZeG5E-oprWGE05ywbtXocn5u3zKSlfjw2DzFZ34CJZqSXD-P4AzkE-is0nW7B472aWzUkOrdRwzi4zOrvRiugDqq_OIAc0aElBaNOS6dJwccOF9U6jU3aUyM";
     
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
                                <p v-else class="success"> Code successfully validated </p>
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