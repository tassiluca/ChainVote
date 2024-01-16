<script setup lang="ts">
    import { watch, onBeforeMount, ref } from "vue";
    import { useRoute } from 'vue-router';
    import VoteOption from "@/components/vote/VoteOption.vue";
    import { makeRequest } from '@/assets/utils'

    const voteOptions: object[] = [];
    const voteCode = ref("");

    const isValidVoteCode = ref(false);

    const choosedOption = ref("");

    const route = useRoute();
    const data: any = route.meta.data;
    const goal = data.goal as string;

    const output = ref("");
    function submitForm(): void {
        alert(choosedOption.value);
    }

    function checkVoteCode(code: string) {
        const electionId = route.params.id;
        const jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjVhNWM0NDE2YTNmN2ZhMTUzYzU0ZjY0IiwiZW1haWwiOiJnaW9yaS5wcm92YUB0ZXN0Lml0IiwicGFzc3dvcmQiOiIkMmIkMTAkYTBOQVRNRjdiaDBIbXlZL0Vrd1lTT0ZkZU12dFZ1RmZYV2VTcGtOdW1JaWxXN2pNL0I5TzIiLCJmaXJzdE5hbWUiOiJHaW9wYWluIiwic2Vjb25kTmFtZSI6Ik5vR2FpbiIsInJvbGUiOiJ1c2VyIiwiX192IjowfSwiaWF0IjoxNzA1MzY1Mzg5LCJleHAiOjE3MDUzNjYyODksImF1ZCI6Imh0dHBzOi8vd3d3LmNoYWludm90ZS5jb20iLCJpc3MiOiJDaGFpblZvdGUifQ.nJBJN0th7SBUmzNc5ET6f-DJ6EoRwpyUEoQXQyeqoWlzePhL20NWW8tdN4fBkrLCljIm__8IzBb6-AcxkMd2Gu0LVjkDP-elcATY2oPYJEp51lWL8Qo9Bi2JKzwUvZU-z3zknptAxP4VlDdO4Lu7Po8hRynn-r5i17yH3Ubw0xzrs29dfQ3ws6qrW4Gha76ZoLI0W4XXGCsUmVXuyQ1kdBwS-fH1FE-lfjPomOz8rUSB8m13n47Com7X20ZUU62nxIuK-ksf6PjoqHwtBDtO-6bsAlDBBVxgqNSup_490wrLuMxx6_QfTOw0G2N36OQfU4aEG8XjnC_WtpUnYh1S8Ozwr4XBPbaas7gkpo_ERDdhnfl2c9oKlPThrIkShWFwbSa9Ncs34sZawu-ztYA3QGLsG1KKnX3DTpK1_iKnq_dwiKmqqv_pJuVGKsyy6xomjGPXS6K1On5vA-7MdlYFT5NI6Cg3g8LakrPYk31__vPpX2ga8R14EFyhHuCaw6kSlYyWG3e-6uzgnBNRaMGs6OLV48SJeid6UC5HFyjaP5BQyYGzzeSUnRqkXCATfgNAQAXYOhCLWcWCrm_2xjdQ3b7zGEFZOLgA9OmNp2MRzyOxB8TsjHM52YJVbKEFc0J9Jj2YRzgbRzL4PEycdPaLJLkn_VbCCEN9VYPGYZc3mFw";
        // make a axios post request to check the code
        
        makeRequest("http://localhost:8080/code/check", "POST", jwtToken, {
            electionId: electionId,
            code: code
        }).then((response) => {
            console.log(response);
            if(response.data.data) {
                isValidVoteCode.value = true;
            } else {
                isValidVoteCode.value = false;
            }
        });
    }

    watch(voteCode, (newValue, oldValue) => {
        checkVoteCode(newValue);
        output.value = `Computed: ${newValue}`;
    });

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
        <form @submit.prevent="submitForm" method="POST">
            <div class="row mb-3">
                <label for="voteCode" class="form-label">Vote Code</label>
                <input type="text" class="form-control" id="voteCode" aria-describedby="voteCodeDescription" v-model.lazy="voteCode">
                <div id="voteCodeDescription" class="form-text">{{ isValidVoteCode ? "The code is valid" : "Please insert a valid code"}}</div>
                <div> {{ output }}</div>
            </div>
            <div class="row mb-3">
                <h2>Vote Options</h2>
                <VoteOption v-for="option in voteOptions" 
                    :key="option.id" 
                    :optId="option.id.toString()" 
                    :name="option.name" 
                    v-model="choosedOption"/>
            </div>
            <button type="submit" class="btn btn-primary">Submit</button>
        </form>
    </div>
</template>

<style scoped>
</style>