<template>
  <form :id="`form-${property}`" method="post">
    <label :for="`input-${property}`">{{ capitalizeFirstLetter(property) }}</label>
    <p :id="`old-value-${property}`" class="hidden">Old value: <strong>{{ value }}</strong></p>
    <input :type="hide ? 'password' : 'text'" :id="`input-${property}`" class="form-control" :readonly="isReadOnly" :value="value" :name="property">
    <div v-if="mutable">
      <button class="btn btn-sm btn-primary btn-block" :id="`change-${property}`" @click.prevent="onChangeButton(property)">
        Change
      </button>
      <button class="btn btn-sm btn-primary btn-block hidden" :id="`submit-change-${property}`" type="submit" @click.prevent="onSubmitChangeButton(property, value)">
        Submit change
        <span :id="`spinner-${property}`" class="spinner-border spinner-border-sm hidden" role="status" aria-hidden="true"></span>
      </button>
      <button class="btn btn-sm btn-primary btn-block hidden" :id="`restore-change-${property}`" type="submit" @click.prevent="onRestoreButton(property)">
        Restore
      </button>
      <p :id="`error-${property}`" class="alert alert-danger hidden" role="alert"></p>
      <p :id="`success-${property}`" class="alert alert-success hidden" role="alert"></p>
    </div>
  </form>
</template>

<script setup lang="ts">

  import {makeAjaxPostRequest} from "@/utils";

  const props = defineProps<{
    property: string,
    value: string,
    hide: boolean,
    mutable: boolean,
    id: string,
    isValidValue: (val: string) => boolean,
    help: string,
  }>()

  function capitalizeFirstLetter(str: string) {
    return str.charAt(0).toUpperCase() + str.slice(1);
  }

  function onSubmitChangeButton(property: string, oldValue: string) {

    const urlToSubmit = '/user/' + props.id + '/change';
    const spinner = document.getElementById('spinner-' + property)!
    const error = document.getElementById('error-' + property)!
    const success = document.getElementById('success-' + property)!
    const input = document.getElementById('input-' + property) as HTMLInputElement;
    const restore = document.getElementById('restore-change-' + property) as HTMLButtonElement;
    showElem(spinner);

    if (input.value !== oldValue) {
      if (!props.isValidValue(input.value)) {
        error.innerHTML = props.help;
        showElem(error);
        hideElem(success);
        hideElem(spinner);
        return;
      }
      hideElem(restore)
      const data = {
        'property': property,
        'value': input.value,
      };
      makeAjaxPostRequest(urlToSubmit, data,
          (res: {success: boolean, message: string}) => {
            if (res.success) {
              success.innerHTML = res.message;
              showElem(success);
              hideElem(error);
              setTimeout(() => {
                hideElem(success);
              }, 2000);
              refresh();
            } else {
              error.innerHTML = res.message;
              showElem(error);
              hideElem(success);
            }
          },
          (err: {status: string, responseJSON: { message: string }}) => {
            error.innerHTML = 'Error ' + err.status + ': ' + err.responseJSON.message;
            showElem(error);
            hideElem(success);
          }
      );
    } else {
      showElem(success);
      success.innerText = 'Nothing to change';
      setTimeout(() => {
        hideElem(success);
      }, 2000);
    }
    hideElem(spinner);
    isReadOnly.value = true;
    showElem(document.getElementById(`change-${property}`)!);
    hideElem(document.getElementById(`old-value-${property}`)!);
    hideElem(document.getElementById(`submit-change-${property}`)!);
    hideElem(document.getElementById(`restore-change-${property}`)!);
  }

  function refresh() {
    window.location.reload();
  }

</script>

<script lang="ts">
  import {ref} from "vue";

  const isReadOnly = ref(true);

  function onChangeButton(property: string) {
    isReadOnly.value = false;
    hideElem(document.getElementById(`change-${property}`)!);
    hideElem(document.getElementById(`error-${property}`)!);
    hideElem(document.getElementById(`success-${property}`)!);
    showElem(document.getElementById(`old-value-${property}`)!);
    showElem(document.getElementById(`submit-change-${property}`)!);
    showElem(document.getElementById(`restore-change-${property}`)!);
  }

  function onRestoreButton(property: string) {
    isReadOnly.value = true;
    showElem(document.getElementById(`change-${property}`)!);
    hideElem(document.getElementById(`error-${property}`)!);
    hideElem(document.getElementById(`success-${property}`)!);
    hideElem(document.getElementById(`old-value-${property}`)!);
    hideElem(document.getElementById(`submit-change-${property}`)!);
    hideElem(document.getElementById(`restore-change-${property}`)!);
  }

  function showElem(element: HTMLElement) {
    element.classList.remove("hidden");
  }

  function hideElem(element: HTMLElement) {
    element.classList.add("hidden");
  }
</script>

<style>
  form label {
    display: block;
    text-align: center;
    font-weight: bold;
  }
  .hidden {
    display: none;
  }
  input {
    margin: 2% 0;
  }
  p.alert {
    margin: 2% 0;
    text-align: center;
  }
  button {
    margin-right: 2%;
  }
</style>