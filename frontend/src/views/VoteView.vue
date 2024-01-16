<script setup lang="ts">
    import { watch, onBeforeMount, ref } from "vue";
    import { useRoute } from 'vue-router';
    import { makeRequest } from '@/assets/utils'
    import VoteOption from "@/components/vote/VoteOption.vue";

    const voteOptions: object[] = [];
    const voteCode = ref("");
    const isValidVoteCode = ref(false);

    const choosedOption = ref("");
    const jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjVhNWM0NDE2YTNmN2ZhMTUzYzU0ZjY0IiwiZW1haWwiOiJnaW9yaS5wcm92YUB0ZXN0Lml0IiwicGFzc3dvcmQiOiIkMmIkMTAkYTBOQVRNRjdiaDBIbXlZL0Vrd1lTT0ZkZU12dFZ1RmZYV2VTcGtOdW1JaWxXN2pNL0I5TzIiLCJmaXJzdE5hbWUiOiJHaW9wYWluIiwic2Vjb25kTmFtZSI6Ik5vR2FpbiIsInJvbGUiOiJ1c2VyIiwiX192IjowfSwiaWF0IjoxNzA1NDQ0NTIwLCJleHAiOjE3MDU0NDU0MjAsImF1ZCI6Imh0dHBzOi8vd3d3LmNoYWludm90ZS5jb20iLCJpc3MiOiJDaGFpblZvdGUifQ.vZDSlO6huVo-RLzRH5ma1_OxkR7T7Px2TJG2KCybDuMuN58s5F4SARSf7HC7ocspUCKlKCtva2Cs3NzjbkzFlm9DTa8IPnX-BXjmIjDOaj9gyvGp-n3nPBA4JtvscV2eMcJELjh5aJVaE3brUMMokYeXXuukCqUy5iTfAMtZ_bRpPyHOU70xMot_s0w_0Kn5TUa01wl0lzGaYBeS16n1_4I4W88Z0eE1inPJk6VhSEdPs1nEhI8QxpJEBdK92d7L9zuZUOd9wrvD4GjDkTNxuf175QXgfQ_L8H4WZFh-o5B8RaBb2MFdpUJsmGAL2O0dOOwHUnoacVGOw3T-Tk0q8l0Gb9ZhEoAtANloDEjxu2dn3XC4LHu0zN9jsBlrTiGxXPGcva973Ed6PjY2R8mJMnvNODLQp5uXFco5b_NkUfj4ENVePdaJfkFsk9Rgt29Mc2Hq2eMOEOYojj7gOjqJJSyuH40FKAAoi88QGhwfiRYH2NPbF5ywl7bJS5eArn3nsuNqvny8m5DRWx72GLV-7h5KVgNg9dscQUSeaQPrxexofeg8Bb4gqOglVqcvs5POWcd1gVCsBZls4lVi-QSh7dhnmYcS9Tii1LLKJFRflKVCRISZ20XNV1tRRhzjUJoH-QOQAnyKtqknMz5OM-VNqcHLfFPO-mTcZtkKI7SKjfM";
        
    const route = useRoute();
    const data: any = route.meta.data;
    const electionId = route.params.id;
    const goal = data.goal as string;

    function submitForm(): void {
        makeRequest(`http://localhost:8080/election/vote/${electionId}`, "PUT", jwtToken, {
            code: voteCode.value,
            choice: choosedOption.value
        }).then((response) => {
            alert(response.data.data);
        }).catch((error) => {
            alert(error);
        });
    }

    function checkVoteCode(code: string) {
        makeRequest("http://localhost:8080/code/check", "POST", jwtToken, {
            electionId: electionId,
            code: code
        }).then((response) => {
            console.log(response);
            isValidVoteCode.value = response.data.data;
        });
    }

    watch(voteCode, (newValue, _) => { checkVoteCode(newValue) });

    onBeforeMount(() => {
        data.choices.forEach((elem: any, idx: number) => {
            voteOptions.push({id: idx, name: elem.choice.toString()});
        })
    });
</script>

<template>
    <div class="container">
        <header class="mb-2">
            <h1> {{ goal }} </h1>
        </header>
        <form @submit.prevent="submitForm" method="POST" >
            <div class="row mb-3">
                <label for="voteCode" class="form-label">Vote Code</label>
                <input type="text" class="form-control" id="voteCode" aria-describedby="voteCodeDescription" v-model.lazy="voteCode">
                <div id="voteCodeDescription" class="form-text">{{ isValidVoteCode ? "The code is valid" : "Please insert a valid code"}}</div>
            </div>
            <div class="row mb-3">
                <h2>Vote Options</h2>
                <VoteOption v-for="option in voteOptions" 
                    :key="option.id" 
                    :optId="option.id.toString()" 
                    :name="option.name" 
                    v-model="choosedOption"/>
            </div>
            <button type="submit" class="btn btn-primary" :disabled="!isValidVoteCode || !choosedOption">Submit</button>
        </form>
    </div>
</template>

<style scoped>
</style>