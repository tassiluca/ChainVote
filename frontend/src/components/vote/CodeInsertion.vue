<script setup lang="ts">
import { ref, type Ref, onMounted, nextTick, watch } from 'vue'
import {useVotingStore } from '@/stores/voting'
import { apiEndpoints } from '@/commons/globals'
import { useRoute } from 'vue-router'
import router from '@/router'
import axios from 'axios'

const {digitNumber} = defineProps<{ digitNumber: number }>();

const route = useRoute();

const inputRefs: Ref<string>[] = []
for(let i = 0; i < digitNumber; i++) {
  inputRefs.push(ref(''));
}

const submit = ref(false);
const error = ref(false);
const otp = ref('');


watch(inputRefs, (newValue) => {
  const allFieldsFilled = newValue.every(value => value.trim() !== '');
  if (allFieldsFilled && submit.value === false) {
    otp.value = newValue.join('');
    if (otp.value.length === digitNumber) {
      submitCode(otp.value);
    }
  } else {
    submit.value = false;
  }
});

function clearInputs() {
  inputRefs.forEach((input, _) => {
    input.value = '';
  });
  const inputs: NodeListOf<HTMLElement> = document.querySelectorAll("input");
  inputs.forEach((input, index) => {
    console.log(index);
    input.disabled = index !== 0;
  });
}


function submitCode(code: string) {
  submit.value = true;
  const urlCheck = `${apiEndpoints.API_SERVER}/code/check`;
  const data = {
    electionId: route.params.id,
    code: code
  };

  axios.post(urlCheck, data).then((response) => {
    if (response.data.data) {
      useVotingStore().setOtpInUse(code);
      useVotingStore().setOtpInUseElectionId(route.params.id as string)
      window.location.href = `/vote/${route.params.id}`;
    } else {
      error.value = true;
      clearInputs();
    }
  }).catch((err) => {
    error.value = true;
    clearInputs();
  }).finally(() => {
    submit.value = false;
  });
}



onMounted(async () => {
  await nextTick();
  const inputs: NodeListOf<HTMLElement> = document.querySelectorAll("input");

  inputs.forEach((input, _) => {
    input.addEventListener("keyup", (e) => {
      if (e.key === "Meta") {
        e.preventDefault();
        return;
      }

      const currentInput = input;
      const nextInput = input.nextElementSibling;
      const prevInput = input.previousElementSibling;

      // Check if the pressed key is a letter, number, symbol, or backspace
      const isAllowedCharacter = /^[a-zA-Z0-9!@#$%^&*()-_+=\[\]{}|\\;:'",.<>/?`~\s\b]$/.test(e.key)
        || e.key === "Backspace";

      // Any fields should contain only one alphanumeric character
      if (!isAllowedCharacter || currentInput.value.length > 1) {
        e.preventDefault();
        currentInput.value = "";
        return;
      }

      if (e.key === "Backspace") {
        if (prevInput) {
          prevInput.disabled = false;
          prevInput.value = "";
          currentInput.disabled = true;
          prevInput.focus();
        }
      } else {
        if (nextInput) {
          nextInput.disabled = false;
          currentInput.disabled = true;
          nextInput.focus();
        }
      }
    });
  });
});

</script>

<template>
  <div class="bg-white p-3 rounded shadow-sm text-center">
    <h4 class="text-center mb-3">Insert the election's OTC</h4>
    <div class="overflow-auto">
      <div class="d-flex flex-wrap justify-content-center">
        <input
          type="text"
          v-for="(_, index) in inputRefs"
          v-model="inputRefs[index].value"
          :disabled="index !== 0"
          class="form-control mx-2 my-2"
          style="max-width: 60px;"
          autocomplete="off"
        />
      </div>
    </div>
    <p v-if="error" class="text-danger mt-3">Code is not valid!</p>
  </div>
</template>

<style scoped>
  input {
    height: 45px;
    width: 42px;
    border-radius: 6px;
    outline: none;
    font-size: 1.125rem;
    text-align: center;
    border: 1px solid #ddd;
  }

  input:focus {
    border-color: #0073E6;
    box-shadow: 0 1px 0 rgba(0, 0, 0, 0.1);
  }
</style>