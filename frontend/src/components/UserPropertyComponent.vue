<template>
  <form :id="`form-${property}`" @submit.prevent="onSubmit">
    <label :for="`input-${property}`">{{ capitalizeFirstLetter(property)}}</label>
    <hr :id="`old-value-${property}-separator`" class="hidden solid"/>
    <p :id="`old-value-${property}`"
       class="hidden">Old value: <strong>{{oldValue }}</strong></p>
    <input :id="`input-${property}`"
           class="form-control my-3"
           :readonly="isReadOnly"
           v-model="refValue"
           :name="property">
    <div v-if="mutable">
      <button class="btn btn-sm btn-primary btn-block"
              :id="`change-${property}`"
              @click.prevent="onChangeButton">
        Change
      </button>
      <div class="my-2 hidden" :id="`div-error-${property}`">
        <span>{{ errors.refValue }}</span>
      </div>
      <button class="btn btn-sm btn-primary btn-block hidden"
              :id="`restore-change-${property}`"
              @click.prevent="onRestoreButton">
        Restore
      </button>
      <button class="btn btn-sm btn-primary btn-block hidden"
              :id="`submit-change-${property}`"
              type="submit"
              :disabled="!meta.valid">
        Submit change
        <span :id="`spinner-${property}`"
              class="spinner-border spinner-border-sm hidden"
              role="status"
              aria-hidden="true"></span>
      </button>
      <p :id="`error-${property}`" class="alert alert-danger hidden" role="alert"></p>
      <p :id="`success-${property}`" class="alert alert-success hidden" role="alert"></p>
    </div>
  </form>
</template>

<script setup lang="ts">
  import {ref} from "vue";
  import {useForm} from "vee-validate";
  import * as yup from "yup";
  import {type User, useUserStore} from "@/stores/user";
  import {capitalizeFirstLetter} from "../commons/utils";

  const props = defineProps<{
    property: string,
    value: string,
    mutable: boolean,
    validation: any,
  }>()

  const oldValue = ref('');
  oldValue.value = props.value;

  const userStore = useUserStore();

  const isReadOnly = ref(true);

  const {meta, errors, handleSubmit, defineField } = useForm({
    validationSchema: yup.object({
      refValue: props.validation,
    }),
  });

  const [refValue, _] = defineField('refValue');
  refValue.value = oldValue.value;

  const onSubmit = handleSubmit(async (values) => {
    const spinner = document.getElementById('spinner-' + props.property)!;
    const err = document.getElementById('error-' + props.property)!;
    const success = document.getElementById('success-' + props.property)!;
    const oldValueElem = document.getElementById(`old-value-${props.property}`)!;

    showElem(spinner);
    hideElem(err);
    hideElem(success);
    hideElem(document.getElementById(`div-error-${props.property}`)!);

    try {
      const newUser = await userStore.updateUserInfo(props.property, values.refValue);
      success.innerHTML = 'Successfully modified ' + props.property + ' in ' + newUser[props.property as keyof User];
      isReadOnly.value = true;
      showElem(success);
      hideElem(err);
      setTimeout(() => {
        hideElem(success);
      }, 2000);
      hideElem(spinner);
      oldValue.value = values.refValue;
      hideElem(oldValueElem);
      hideElem(document.getElementById(`old-value-${props.property}-separator`)!);
      showElem(document.getElementById(`change-${props.property}`)!);
      hideElem(document.getElementById(`submit-change-${props.property}`)!);
      hideElem(document.getElementById(`restore-change-${props.property}`)!);
    } catch (e: any) {
      console.log(e)
      hideElem(spinner);
      refValue.value = oldValue.value;
      err.innerHTML = 'Error ' + e.code + ': ' + e.message;
      showElem(err);
      hideElem(success);
      setTimeout(() => {
        hideElem(err);
      }, 2000);
    }
  })

  function onChangeButton() {
    isReadOnly.value = false;
    refValue.value = "";
    hideElem(document.getElementById(`change-${props.property}`)!);
    hideElem(document.getElementById(`error-${props.property}`)!);
    hideElem(document.getElementById(`success-${props.property}`)!);
    showElem(document.getElementById(`old-value-${props.property}`)!);
    showElem(document.getElementById(`div-error-${props.property}`)!);
    showElem(document.getElementById(`old-value-${props.property}-separator`)!);
    showElem(document.getElementById(`submit-change-${props.property}`)!);
    showElem(document.getElementById(`restore-change-${props.property}`)!);
  }

  function onRestoreButton() {
    isReadOnly.value = true;
    refValue.value = oldValue.value;
    showElem(document.getElementById(`change-${props.property}`)!);
    hideElem(document.getElementById(`error-${props.property}`)!);
    hideElem(document.getElementById(`success-${props.property}`)!);
    hideElem(document.getElementById(`old-value-${props.property}`)!);
    hideElem(document.getElementById(`div-error-${props.property}`)!);
    hideElem(document.getElementById(`old-value-${props.property}-separator`)!);
    hideElem(document.getElementById(`submit-change-${props.property}`)!);
    hideElem(document.getElementById(`restore-change-${props.property}`)!);
  }

  function showElem(element: HTMLElement) {
    element.classList.remove("hidden");
  }

  function hideElem(element: HTMLElement) {
    element.classList.add("hidden");
  }
</script>

<style scoped>
  form label {
    display: block;
    text-align: center;
    font-weight: bold;
  }
  .hidden {
    display: none;
  }
  p.alert {
    margin: 2% 0;
    text-align: center;
  }
  button {
    margin-right: 2%;
  }
</style>