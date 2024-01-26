<script setup lang="ts">
import Breadcrumb from '@/components/BreadcrumbComponent.vue'
import Form from '@/components/forms/FormComponent.vue'
import FormInput from '@/components/forms/FormInputComponent.vue'
import {type Ref, ref} from "vue";
import PageTitle from "@/components/PageTitleComponent.vue";
import {useVotingStore, type VotingCreation} from "@/stores/voting";

const votingStore = useVotingStore();

const response = ref({})

const startDateValue = new Date();
const endDateValue = new Date(startDateValue.getTime() + 24 * 60 * 60 * 1000);
startDateValue.setHours(10, 0);
endDateValue.setHours(10, 0);

const startValues = {
  goal: "",
  voters: 2,
  startDate: startDateValue.toISOString().slice(0,16),
  endDate: endDateValue.toISOString().slice(0,16),
  choices: ["", ""],
}

const references: {
   [prop: string]: any
} = {};

const copyWithoutElement = (original: { [key: string]: any }, elementToRemove: string) => {
  // Using object spread syntax to create a shallow copy
  const copy = { ...original };

  // Removing the specified element from the copy
  delete copy[elementToRemove];

  return copy;
};


function resetValues() {
  for (const prop in copyWithoutElement(startValues, 'choices')) {
    references[prop] = ref(startValues[prop as keyof typeof startValues]);
  }
  references['choices'] = ref(startValues['choices'].map((item: string) => ref(item)));
}

resetValues();

interface Rule {
  help: string,
  validValue: any,
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
    'validValue': (value: string) => value.trim().length > 0,
    'label': 'Goal',
    'type': 'string',
    'placeholder': 'Enter goal',
    'pre': '⌖',
    'autocomplete': 'goal',
  },
  'voters': {
    'help': 'Voters must be at least 2',
    'validValue': (value: number) => value > 1,
    'label': 'Voters',
    'type': 'number',
    'placeholder': 'Enter voters',
    'pre': '✐',
    'autocomplete': 'voters',
  },
  'startDate': {
    'help': 'Start date must be a valid date',
    'validValue': (_: any) => true,
    'label': 'Start date',
    'type': 'datetime-local',
    'placeholder': 'Enter start date',
    'pre': '🗓️',
    'autocomplete': 'start date',
  },
  'endDate': {
    'help': 'End date must be a valid and non-elapsed date, which comes after the start date',
    'validValue': (value: any, sD: any) => new Date(value).getTime() > new Date(sD).getTime() && new Date(value).getTime() > new Date().getTime(),
    'label': 'End date',
    'type': 'datetime-local',
    'placeholder': 'Enter end date',
    'pre': '🗓️',
    'autocomplete': 'end date',
  },
  'choices': {
    'help': 'Choice must be at least 1 character long and different from each others',
    'validValue': (value: [Ref<string>]) => isValidList(value.map((item: Ref<string>) => item.value as string)),
    'label': 'Choice',
    'type': 'string',
    'placeholder': 'Enter choice',
    'pre': '☑',
    'autocomplete': 'choice',
  },
}

function isValidList(strings: string[]) {
  // Check for empty elements
  const hasEmpty = strings.some(str => str.trim() === '');
  if (hasEmpty) {
    return false;
  }

  // Check for duplicates
  const set = new Set(strings.map((item: string) => item.trim()));
  return set.size === strings.length;
}

const addElection = () => {
  references['choices'].value.push(ref(""));
}

const removeElection = () => {
  if (Object.keys(references['choices'].value).length > 2) {
    references['choices'].value.pop();
  }
}

async function onFormSubmit() {

  const errors = [];

  const values: VotingCreation = {
    goal: references['goal'].value,
    voters: references['voters'].value,
    startDate: references['startDate'].value,
    endDate: references['endDate'].value,
    choices: references['choices'].value.map((item: any) => item.value),
  }

  for (const prop in copyWithoutElement(references, 'endDate')) {
    if (!properties[prop].validValue(references[prop].value)) {
      errors.push(properties[prop].help);
    }
  }
  if (!properties['endDate'].validValue(references['endDate'].value, references['startDate'].value)) {
    errors.push(properties['endDate'].help);
  }

  if (errors.length > 0) {
    response.value = {success: false, msg: errors.join('\n')};
    return;
  }

  votingStore.createVoting(values).then((res) => {
    response.value = {success: res.success, msg: res.msg};
    if (res.success) {
      resetValues();
    }
  })
}
</script>

<template>
  <Breadcrumb :paths="[{name: 'Elections', link: '/elections'}, {name: 'Create', link: '/elections/create'}]" />
  <PageTitle title="Create Election" />
  <Form @submit="onFormSubmit" :response="response" submit-btn-name="Create election">
    <template v-slot:body>
      <div class="row gy-5 row-cols-lg-2 row-cols-1 mx-auto mt-1">
        <div class="col" v-for="prop in Object.keys(copyWithoutElement(properties, 'choices'))" :key="prop">
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
      <div class="col-10 mx-auto mt-5 text-center">
        <h2 class="text-primary">Election choices</h2>
      </div>
      <div class="row gy-5 row-cols-lg-3 row-cols-2 mx-auto my-2">
        <div class="col" v-for="idx of Array(references['choices'].value.length).keys()" :key="`choice-${idx}`">
          <div class="p-3 border bg-light">
            <FormInput :helper="properties['choices']['help']"
                       :input-id="`choices-input-${idx}`"
                       :label="`${properties['choices']['label']} ${idx}`"
                       :pre="properties['choices']['pre']">
              <input v-model="references['choices'].value[idx].value"
                     :type="properties['choices']['type']" class="form-control"
                     :placeholder="`${properties['choices']['placeholder']} ${idx}`" required
                     :autocomplete="`${properties['choices']['autocomplete']} ${idx}`"/>
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