<script setup lang="ts">
    import { watch, onBeforeMount, ref } from "vue";
    import { useRoute } from 'vue-router';
    import VoteOption from "@/components/vote/VoteOption.vue";
    import axios from "axios";

    const voteOptions: object[] = [];
    const voteCode = ref("");
    const isValidVoteCode = ref(false);

    const choosedOption = ref("");
    const route = useRoute();
    const data: any = route.meta.data;
    const electionId = route.params.id;
    const goal = data.goal as string;

    function submitForm(): void {
        axios.put(`http://localhost:8080/election/vote/${electionId}`, {
            code: voteCode.value,
            choice: choosedOption.value
        }).then((response) => {
            alert(response.data.data);
        }).catch((error) => {
            alert(error);
        });
    }

    function checkVoteCode(code: string) {
        axios.post("http://localhost:8080/code/check", {
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