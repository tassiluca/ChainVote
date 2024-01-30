<script setup lang="ts">
    import {onBeforeMount, ref } from "vue";
    import { useVotingStore } from '@/stores/voting';
    import { useRoute } from 'vue-router';
    import router from '@/router'
    import VoteOption from "@/components/vote/VoteOption.vue";
    import axios from "axios";


    const voteOptions: any = ref([]);

    const choosedOption = ref("");
    const route = useRoute();
    const electionId = route.params.id as string;

    function submitForm(): void {
        axios.put(`http://localhost:8080/election/vote/${electionId}`, {
            code: useVotingStore().getOtpInUse(),
            choice: choosedOption.value
        }).then((response) => {
            alert(response.data.data);
            useVotingStore().setOtpInUse("");
            useVotingStore().setOtpInUseElectionId("");
        }).catch((error) => {
            alert(error);
        });
    }

    const goal = ref("");
    onBeforeMount(async () => {
        console.log(useVotingStore().getOtpInUse());
        if (!useVotingStore().getOtpInUse() || useVotingStore().getOtpInUseElectionId() !== electionId) {
          await router.push({ name: 'not-found' });
        }

        const data = await useVotingStore().getElectionInfo(electionId);
        goal.value = data.goal;
        data.choices.forEach((elem: any, idx: number) => {
            console.log(elem.choice);
            voteOptions.value.push({id: idx, name: elem.choice.toString()});
        });
    });
</script>

<template>
    <div class="container">
        <header class="mb-2">
            <h1> {{ goal }} </h1>
        </header>
        <form @submit.prevent="submitForm" method="POST" >
            <div class="row mb-3">
                <h2>Vote options</h2>
                <VoteOption v-for="option in voteOptions" 
                    :key="option.id" 
                    :optId="option.id.toString()" 
                    :name="option.name" 
                    v-model="choosedOption"/>
            </div>
            <button type="submit" class="btn btn-primary" :disabled="!choosedOption">Submit</button>
        </form>
    </div>
</template>

<style scoped>
</style>