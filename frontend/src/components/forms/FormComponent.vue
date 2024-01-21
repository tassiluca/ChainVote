<script setup lang="ts">

import {type PropType, ref, watch} from 'vue';

const props = defineProps({
  /** The name of the submit button */
  submitBtnName: {
    type: String,
    required: false,
    default: 'Submit'
  },

  /**
   * The response object with the information of the successful or failed form submission.
   *
   * Usage:
   * ```
   * const response = ref({})
   * async function onUserFormSubmit() {
   *   ...
   *   response.value = {success: true, msg: "Successfull login"};
   * }
   *
   * <template>
   *    <Form @submit="onUserFormSubmit" submit-btn-name="Login" :response="response">
   *      ...
   *    </Form>
   * </template>
   * ```
   */
  response: {
    type: Object as PropType<{ success: boolean, msg: string }>,
    required: false,
    default: () => ({})
  },
});

const submitting = ref(false);

watch(() => props.response, (response) => {
  if (Object.prototype.hasOwnProperty.call(response, 'success')) {
    submitting.value = false;
  }
})

/* Used to emit the submit event in order to be able to handle the form submission in the parent component. */
const emit = defineEmits(['submit']);
function onFormSubmitted() {
  submitting.value = true;
  emit('submit')
}
</script>

<template>
  <form @submit.prevent="onFormSubmitted">
    <div class="col-sm">
      <slot name="body"/>
      <div class="text-center">
        <button type="submit" class="btn btn-primary mt-3 mb-3">
          {{ submitBtnName }}
          <span v-if="submitting" class="spinner-border spinner-border-sm text-light"></span>
        </button>
      </div>
    </div>
    <slot name="footer"/>
    <div v-if="response.hasOwnProperty('success')"
         class="alert mt-3 mb-3 text-center col-10 mx-auto"
         :class="{ 'alert-danger': !response.success, 'alert-success': response.success }"
         role="alert"
    ><strong>{{ response.msg }}</strong></div>
  </form>
</template>

<style scoped></style>
