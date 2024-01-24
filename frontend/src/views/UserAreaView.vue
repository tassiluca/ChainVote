<template>
  <Breadcrumb :paths="[{name: 'User-area', link: '/user'}]" />
  <PageTitle title="User area"/>
  <div class="row gy-5 row-cols-md-2 row-cols-1 mx-auto my-2">
    <div class="col" v-for="property in Object.keys(newValueRules)" :key="property">
      <div class="p-2 border bg-light">
        <div class="card col-10 mx-auto">
          <UserProperty
              :property="property"
              :value="userRef[property as keyof typeof user]"
              :hide="newValueRules[property].hide"
              :mutable="newValueRules[property].mutable"
              :isValidValue="newValueRules[property].isValid"
              :help="newValueRules[property].help"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import UserProperty from "@/components/UserPropertyComponent.vue";
  import {useRoute} from "vue-router";
  import {ref} from "vue";
  import PageTitle from "@/components/PageTitleComponent.vue";
  import Breadcrumb from "@/components/BreadcrumbComponent.vue";

  interface User {
    name: string,
    surname: string,
    email: string,
    password: string,
    role: string,
  }

  const route = useRoute();
  const data: any = route.meta.data;

  let user: User = {
    name: "",
    surname: "",
    email: "",
    password: "",
    role: "",
  };

  for (let prop in user) {
    user[prop as keyof User] = data[prop as keyof User];
  }

  const userRef = ref(user);

  interface Rule {
    isValid: (val: string) => boolean,
    help: string,
    mutable: boolean,
    hide: boolean,
  }

  const newValueRules: {
    [up: string]: Rule
  } = {
    'name': {
      'isValid': (value: string) => {
      return value.trim().length > 0
      },
      'help': 'Name must be at least 1 character long',
      'mutable': true,
      'hide': false,
    },
    'surname': {
      'isValid': (value: string) => {
        return value.trim().length > 0
      },
      'help': 'Surname must be at least 1 character long',
      'mutable': true,
      'hide': false,
    },
    'email': {
      'isValid': (value: string) => {
        const reg = /^(([^<>()[\].,;:\s@"]+(\.[^<>()[\].,;:\s@"]+)*)|(".+"))@(([^<>()[\].,;:\s@"]+\.)+[^<>()[\].,;:\s@"]{2,})$/i;
        return reg.test(value)
      },
      'help': 'Email must contain @ and .',
      'mutable': true,
      'hide': false,
    },
    'password': {
      'isValid': (value: string) => {
        const reg = /^(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[~`!@#$%^&*()--+={}[\]|\\:;"'<>,.?/_])[a-zA-Z0-9~`!@#$%^&*()--+={}[\]|\\:;"'<>,.?/_]{8,16}$/
        return reg.test(value);
      },
      'help': 'Password must be at least 8 characters long and contain at least one number, one uppercase letter, one lowercase letter and one special character',
      'mutable': true,
      'hide': true,
    },
    'role': {
      'isValid': (_: string) => {
        return false;
      },
      'help': 'Immutable value',
      'mutable': false,
      'hide': false,
    }
  };
</script>

<style>
  .card {
    border-radius: 15px;
    padding: 2%;
    margin: 3% 0;
  }

  div .border {
    border-radius: 15px;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  tr div {
    margin: 1% 0;
  }
</style>
