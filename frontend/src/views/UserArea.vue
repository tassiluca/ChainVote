<template>
  <div class="card col-8 mx-auto bg-primary">
    <ul>
      <li v-for="property in properties" :key="property">
        <div class="card col-10 mx-auto">
          <user-property
              :property="property"
              :value="user[property]"
              :hide="propertiesToHide.includes(property)"
              :mutable="propertiesMutable.includes(property)"
              :id="props.id"
              :isValidValue="propertiesMutable.includes(property) ? newValueRules[property.valueOf()].isValid : null"
              :help="propertiesMutable.includes(property) ? newValueRules[property.valueOf()].help : null"
          />
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import UserProperty from "@/components/UserProperty.vue";

const props = defineProps<{
  id: string
}>()

interface User {
  name: string,
  surname: string,
  email: string,
  password: string,
  role: string,
}

type UserProps = keyof User;

const properties: UserProps[] = [
  "name",
  "surname",
  "email",
  "password",
  "role",
]

interface Rule {
  isValid: (val: string) => boolean,
  help: string,
}

const newValueRules: {
  [up: string]: Rule
} = {
  'name': {
    'isValid': (value: string) => {
    return value.trim().length > 0
    },
    'help': 'Name must be at least 1 character long',
  },
  'surname': {
    'isValid': (value: string) => {
      return value.trim().length > 0
    },
    'help': 'Surname must be at least 1 character long',
  },
  'email': {
    'isValid': (value: string) => {
      const reg = /^(([^<>()[\].,;:\s@"]+(\.[^<>()[\].,;:\s@"]+)*)|(".+"))@(([^<>()[\].,;:\s@"]+\.)+[^<>()[\].,;:\s@"]{2,})$/i;
      return reg.test(value)
    },
    'help': 'Email must contain @ and .',
  },
  'password': {
    'isValid': (value: string) => {
      const reg = /^(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[~`!@#$%^&*()--+={}[\]|\\:;"'<>,.?/_])[a-zA-Z0-9~`!@#$%^&*()--+={}[\]|\\:;"'<>,.?/_]{8,16}$/
      return reg.test(value);
    },
    'help': 'Password must be at least 8 characters long and contain at least one number, one uppercase letter, one lowercase letter and one special character',
  }
};

const propertiesToHide: UserProps[] = [
  "password",
]

const propertiesMutable: UserProps[] = [
  "name",
  "surname",
  "email",
  "password",
]

const user: User = {
  name: "John",
  surname: "Doe",
  email: "prova@hotmail.it",
  password: "password",
  role: "user",
}

</script>


<style>
  .card {
    border-radius: 15px;
    padding: 2%;
    margin: 3% 0;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  tr div {
    margin: 1% 0;
  }
</style>
