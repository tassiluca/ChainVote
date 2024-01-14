<script setup lang="ts">
    import { ref } from "vue";
    import VoteOption from "@/components/vote/VoteOption.vue";

    const voteOptions = [{ id: 1, name: "Option 1" }, { id: 2, name: "Option 2" }];
    const voteCode = ref("");
    const choosedOption = ref("");

    function submitForm(): void {
        alert(choosedOption.value);
    }
    
</script>

<template>
    <div class="container">
        <header class="mb-2">
            <h1> Prova </h1>
        </header>
        <form @submit.prevent="submitForm" method="POST">
            <div class="row mb-3">
                <label for="voteCode" class="form-label">Vote Code</label>
                <input type="text" class="form-control" id="voteCode" aria-describedby="voteCodeDescription" v-model.lazy="voteCode">
                <div id="voteCodeDescription" class="form-text">Paste here the code generated for this election.</div>
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