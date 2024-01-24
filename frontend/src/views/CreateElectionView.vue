<script setup lang="ts">
import Breadcrumb from '@/components/BreadcrumbComponent.vue'
import Form from '@/components/forms/FormComponent.vue'
import FormInput from '@/components/forms/FormInputComponent.vue'
import {ref} from "vue";
import PageTitle from "@/components/PageTitleComponent.vue";

interface Election {
  goal: string
  voters: number
  startDate: string | Date
  endDate: string | Date
  choices: string[]
}

const response = ref({})

const startDateValue = new Date();
const endDateValue = new Date(startDateValue.getTime() + 24 * 60 * 60 * 1000);
startDateValue.setHours(10, 0);
endDateValue.setHours(10, 0);

const references: {
   [prop: string]: any
} = {
  goal: ref(""),
  voters: ref("0"),
  startDate: ref(startDateValue.toISOString().slice(0,16)),
  endDate: ref(endDateValue.toISOString().slice(0,16)),
  choices: ref([ref(""), ref("")]),
}

interface Rule {
  help: string,
  label: string,
  type: string,
  placeholder: string,
  pre: string,
  autocomplete: string,
}

const properties: {
  [prop: string]: Rule
} = {
  'goal': {
    'help': 'Goal must be at least 1 character long',
    'label': 'Goal',
    'type': 'string',
    'placeholder': 'Enter goal',
    'pre': '⌖',
    'autocomplete': 'goal',
  },
  'voters': {
    'help': 'Voters must be at least 2',
    'label': 'Voters',
    'type': 'number',
    'placeholder': 'Enter voters',
    'pre': '✐',
    'autocomplete': 'voters',
  },
  'startDate': {
    'help': 'Start date must be a valid date',
    'label': 'Start date',
    'type': 'datetime-local',
    'placeholder': 'Enter start date',
    'pre': '🗓️',
    'autocomplete': 'start date',
  },
  'endDate': {
    'help': 'End date must be a valid and non-elapsed date, which follows the start date',
    'label': 'End date',
    'type': 'datetime-local',
    'placeholder': 'Enter end date',
    'pre': '🗓️',
    'autocomplete': 'end date',
  },
  'choice': {
    'help': 'Choice must be at least 1 character long and different from each others',
    'label': 'Choice',
    'type': 'string',
    'placeholder': 'Enter choice',
    'pre': '☑',
    'autocomplete': 'choice',
  },
}

const copyWithoutElement = (original: { [key: string]: any }, elementToRemove: string) => {
  // Using object spread syntax to create a shallow copy
  const copy = { ...original };

  // Removing the specified element from the copy
  delete copy[elementToRemove];

  return copy;
};

const addElection = () => {
  references['choices'].value.push(ref(""));
}

const removeElection = () => {
  if (Object.keys(references['choices'].value).length > 2) {
    references['choices'].value.pop();
  }
}

async function onFormSubmit() {
  // TODO link to backend
  for (const prop in references) {
    console.log(prop);
    console.log(references[prop].value);
    if (Array.isArray(references[prop].value)) {
      for (const choice in references[prop].value) {
        console.log(choice);
        console.log(references[prop].value[choice].value);
      }
    }
  }
  response.value = {success: true, msg: "Successfully created election"};
  // try {
  //   await authStore.login(role.value, username.value, password.value);
  // } catch (e: any) {
  //   const genericErrorMsg = "Type the correct username and password, and try again.";
  //   if (e instanceof AxiosError) {
  //     response.value = {
  //       success: false,
  //       msg: `${e.response!.data.error.message}: ${e.response!.data.error.name}. ${genericErrorMsg}`
  //     };
  //   } else {
  //     response.value = {success: false, msg: `${e.message}. ${genericErrorMsg}`};
  //   }
  // }
}
</script>

<template>
  <Breadcrumb :paths="[{name: 'Elections', link: '/elections'}, {name: 'Create', link: '/elections/create'}]" />
  <PageTitle title="Create Election" />
  <Form @submit="onFormSubmit" :response="response" submit-btn-name="Create election">
    <template v-slot:body>
      <div class="row gy-5 row-cols-lg-2 row-cols-1 mx-auto mt-3">
        <div class="col" v-for="prop in Object.keys(copyWithoutElement(properties, 'choice'))" :key="prop">
          <div class="p-3 border bg-light">
            <FormInput :helper="properties[prop]['help']"
                       :input-id="`input-${prop}`"
                       :label="properties[prop]['label']"
                       :pre="properties[prop]['pre']">
              <input v-model="references[prop].value"
                     :type="properties[prop]['type']" class="form-control"
                     :placeholder="properties[prop]['placeholder']" required
                     :autocomplete="properties[prop]['autocomplete']"/>
            </FormInput>
          </div>
        </div>
      </div>
      <div class="col-10 mx-auto mt-3 text-center">
        <hr/>
        <h2 class="text-primary">Election choices:</h2>
      </div>
      <div class="row gy-5 row-cols-lg-3 row-cols-2 mx-auto my-2">
        <div class="col" v-for="idx of Array(references['choices'].value.length).keys()" :key="`choice-${idx}`">
          <div class="p-3 border bg-light">
            <FormInput :helper="properties['choice']['help']"
                       :input-id="`choice-input-${idx}`"
                       :label="`${properties['choice']['label']} ${idx}`"
                       :pre="properties['choice']['pre']">
              <input v-model="references['choices'].value[idx].value"
                     :type="properties['choice']['type']" class="form-control"
                     :placeholder="`${properties['choice']['placeholder']} ${idx}`" required
                     :autocomplete="`${properties['choice']['autocomplete']} ${idx}`"/>
            </FormInput>
          </div>
        </div>
      </div>
      <div class="my-5">
        <ul>
          <li>
            <button id="addElection" class="btn btn-xs btn-primary" @click.prevent="addElection" type="button">+</button>
            <button id="removeElection" v-if="Object.keys(references['choices'].value).length > 2" class="btn btn-xs btn-primary" @click.prevent="removeElection" type="button">-</button>
          </li>
        </ul>
      </div>
    </template>
  </Form>
</template>


<style scoped>
ul {
  list-style-type: none;
  li {
    padding-right: 20px;
    a {
      color: #E6308A;
      font-weight: bold;
      text-decoration: none;
    }
    a:hover {
      color: #E6308A;
      opacity: 0.7;
      transition: 0.5s;
      text-decoration: underline;
    }
  }
}
div {
  border-radius: 15px;
}

.btn-xs {
  margin-right: 2%;
}

input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  /* display: none; <- Crashes Chrome on hover */
  -webkit-appearance: none;
  margin: 0; /* <-- Apparently some margin are still there even though it's hidden */
}

input[type=number] {
  -moz-appearance:textfield; /* Firefox */
}
</style>