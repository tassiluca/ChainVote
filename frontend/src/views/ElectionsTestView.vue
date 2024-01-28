<script setup lang="ts">
    import * as bootstrap from 'bootstrap'
    import { ref, onMounted, nextTick } from 'vue'
    import RequestCodeModal from '@/components/vote/RequestCodeModal.vue';

    const electionName = ref("Test election")  // Name of the election to pass to the modal
    const electionId = ref("2027020788")       // Id of the election to request code for
    /*
     * The id of the modal.
     * It is used to open the modal from the parent component.
     */
    const modalId = ref("modal_vote")
    const modal = ref() // The element that will control the modal

    onMounted(async () => {
      // Await the next tick to be sure that the modal is rendered
      await nextTick();
      modal.value = new bootstrap.Modal(`#${modalId.value}`, {})
    });

    function openModal() {
        // This functions should be called when user clicks on the links to request the code
        // Here should set the values of the props of the modal
        // electionName.value = ...
        // electionId.value = ...
        if (modal.value) {
          modal.value.show()
        }
    }
</script>

<template>
    <div class="container">
        <header>
            <h1> Test page for code request </h1>
        </header>
        <button type="button" class="btn btn-primary" @click="openModal"> Launch demo modal </button>
        <RequestCodeModal :electionName="electionName" :electionId="electionId" :id="modalId" />
    </div>
</template>


