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
    
    <h1> Prova </h1>
    <form @submit.prevent="submitForm" method="POST">
        <input type="text" v-model.lazy="voteCode" />
        <VoteOption v-for="option in voteOptions" 
                    :key="option.id" 
                    :optId="option.id.toString()" 
                    :name="option.name" 
                    v-model="choosedOption"/>
            
        <button type="submit">Submit</button>
    </form>
</template>