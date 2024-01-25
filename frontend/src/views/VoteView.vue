<script setup lang="ts">
    import { watch, onBeforeMount, ref } from "vue";
    import { useRoute } from 'vue-router';
    import { makeRequest } from '@/assets/utils'
    import VoteOption from "@/components/vote/VoteOption.vue";

    const voteOptions: object[] = [];
    const voteCode = ref("");
    const isValidVoteCode = ref(false);

    const choosedOption = ref("");
    const jwtToken = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjVhNWE5ZGM2YTNmN2ZhMTUzYzU0ZjJiIiwiZW1haWwiOiJhZG1pbi50ZXN0QHRlc3QuaXQiLCJwYXNzd29yZCI6IiQyYiQxMCRjMjB4NS9Xdjhqa0ZSVHlaV3VZU0J1dVJiQ2RwNTBIUGozMWFMeHhHbjRjN0VTd1JKUzg1UyIsImZpcnN0TmFtZSI6Ikdpb3BhaW4iLCJzZWNvbmROYW1lIjoiTm9HYWluIiwicm9sZSI6ImFkbWluIiwiX192IjowfSwiaWF0IjoxNzA1ODYwNDI5LCJleHAiOjE3MDU4NjEzMjksImF1ZCI6Imh0dHBzOi8vd3d3LmNoYWludm90ZS5jb20iLCJpc3MiOiJDaGFpblZvdGUifQ.22u6xLXp4feyFoOzMMceaWoMAkrSqAnge9bkginLpSvmFeI9kbEscjJWD8LU78aiEzrp2i5NtkV5YyIG_DcklKDcSxRQqZ-yG85qsjGXDWzxVmKJbQDKf1CbxXxH2uSq5aELpzr2-7ppQJBCexqwZdX9ehZydqmZYx-OesB1n8r-ld4dSroqVr6VCFwCVcX7RqSnSqTs50CrzpZlAV2yFXCsRGl0RmC-K0CaDt_pK3gRS_g8uF0BIFk5JyYKfAhJ7jK9JdhQo383Ut09nj53PfouVFBGNcF897FAejPJSlyC1jcqHIWUud8TAmsERuVt4oLKz8Vy5HlDV4bBi2bCuZRnl2OKHnDCbbCVQwtzV8SwknbKpGQVcQaa9rYxTgrWFW7b6fSP4L--3Pq_JZ8rCDgUEW91CNGlBsAjLmT56oL1ioEmjg78tkSfRawlaLYt8-spm45fgMTna2e-mCNPsjoV4cKdW9z0b-Y3I6DM3gRDEM45zUEv9Ya4tWjON0Io629K1JWUi4uTt5inYtYGIzMoeCaJtIKsjPdnoLhDoiacWQbB0njEtFiYa4y4THfA5kSI5LomSE3U6MzkyzklDsQgucgDqc-KOPfxU2sNOIO1DJwl5T-mkCS7WMOsR-yccjdWCrPodAwPqMQFcd6kg4cWbU4dmJ92-KgTwo2b8fs";
        
    const route = useRoute();
    const data: any = route.meta.data;
    const electionId = route.params.id;
    const goal = data.goal as string;

    function submitForm(): void {
        makeRequest(`http://localhost:8080/election/vote/${electionId}`, "PUT", {
            code: voteCode.value,
            choice: choosedOption.value
        }, jwtToken).then((response) => {
            alert(response.data.data);
        }).catch((error) => {
            alert(error);
        });
    }

    function checkVoteCode(code: string) {
        makeRequest("http://localhost:8080/code/check", "POST", {
            electionId: electionId,
            code: code
        }, jwtToken).then((response) => {
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