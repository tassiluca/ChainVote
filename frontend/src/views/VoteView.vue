<script setup lang="ts">
    import {onBeforeMount, ref } from "vue";
    import { useVotingStore } from '@/stores/voting';
    import { useRoute } from 'vue-router';
    import router from '@/router'
    import VoteOption from "@/components/vote/VoteOption.vue";
    import PageTitle from '@/components/PageTitleComponent.vue'


    const voteOptions: any = ref([]);
    const response = ref({});
    const submitting = ref(false);

    const choosedOption = ref("");
    const route = useRoute();
    const electionId = route.params.id as string;

    async function submitForm() {
        submitting.value = true;
        try {
          response.value = await useVotingStore().castVote(choosedOption.value);
          setTimeout(async () => await router.replace(`/elections/${electionId}`), 2000);
        } catch (e: any) {
          response.value = {
            success: false,
            msg: e.response.data.error.message,
          };
        }
        submitting.value = false;
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
    <div class="container-sm col-md-10">
        <header class="mb-2">
          <PageTitle :title="`Vote for election ${goal}`" />
        </header>
        <form @submit.prevent="submitForm" method="POST" >
            <div class="row mb-3">
                <h2>Vote choices</h2>
                <VoteOption v-for="option in voteOptions" 
                    :key="option.id" 
                    :optId="option.id.toString()" 
                    :name="option.name" 
                    v-model="choosedOption"/>
            </div>
            <button type="submit" class="btn btn-primary" :disabled="!choosedOption">
              Vote
              <span v-if="submitting" class="spinner-border spinner-border-sm text-light"></span>
            </button>
            <slot name="footer"/>
            <div v-if="response.hasOwnProperty('success')"
                 class="alert mt-3 mb-3 text-center col-10 mx-auto"
                 :class="{ 'alert-danger': !response.success, 'alert-success': response.success }"
                 role="alert"
            ><strong>{{ response.msg }}</strong></div>
        </form>
    </div>
</template>

<style scoped>
</style>