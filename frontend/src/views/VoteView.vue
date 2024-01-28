<script setup lang="ts">
    import {onBeforeMount, ref } from "vue";
    import { useVotingStore } from '@/stores/voting';
    import { useRoute } from 'vue-router';
    import VoteOption from "@/components/vote/VoteOption.vue";
    import axios from "axios";

    const voteOptions: object[] = [];

    const choosedOption = ref("");
    const route = useRoute();
    const data: any = route.meta.data;
    const electionId = route.params.id;
    const goal = data.goal as string;

    function submitForm(): void {
        axios.put(`http://localhost:8080/election/vote/${electionId}`, {
            code: useVotingStore().getOtpInUse(),
            choice: choosedOption.value
        }).then((response) => {
            alert(response.data.data);
          useVotingStore().setOtpInUse("");
        }).catch((error) => {
            alert(error);
        });
    }

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
                <h2>Vote Options</h2>
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