<template>
  <Breadcrumb :paths="[{name: 'User area', link: '/user'}]" />
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
              :validation="newValueRules[property].validation"
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
  import * as yup from 'yup'

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
    validation: any,
    mutable: boolean,
    hide: boolean,
  }

  const newValueRules: {
    [up: string]: Rule
  } = {
    'name': {
      'validation': yup.string().required('Name must be at least 1 character long'),
      'mutable': true,
      'hide': false,
    },
    'surname': {
      'validation': yup.string().required('Surname must be at least 1 character long'),
      'mutable': true,
      'hide': false,
    },
    'email': {
      'validation': yup.string().email('Email must contain @ and following chars').required('Email is required'),
      'mutable': true,
      'hide': false,
    },
    'password': {
      'validation': yup.string().min(8, 'Password must be at least 8 characters long').required('Password is required').matches(
          /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])/,
          'Password must contain at least one uppercase letter, one lowercase letter, one number and one special character'
      ),
      'mutable': true,
      'hide': true,
    },
    'role': {
      'validation': undefined,
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
