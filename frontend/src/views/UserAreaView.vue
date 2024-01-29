<template>
  <Breadcrumb :paths="[{name: 'User area', link: '/user'}]" />
  <PageTitle title="User area"/>
  <div v-if="data" class="row gy-5 row-cols-md-2 row-cols-1 mx-auto my-2">
    <div class="col" v-for="property in Object.keys(newValueRules)" :key="property">
      <div class="p-2 border bg-light">
        <div class="card col-10 mx-auto">
          <UserProperty
              :property="property"
              :value="data![property as keyof User]"
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
  import {onMounted, type Ref, ref} from "vue";
  import PageTitle from "@/components/PageTitleComponent.vue";
  import Breadcrumb from "@/components/BreadcrumbComponent.vue";
  import * as yup from 'yup'
  import router from "@/router";
  import {type User, useUserStore} from "@/stores/user";

  const userStore = useUserStore();
  const data: Ref<User | null> = ref(null);

  onMounted(async () => {
    await getUser();
  });

  async function getUser() {
    try {
      data.value = await userStore.getUserInfo();
      console.log(data.value);
    } catch (e: any) {
      console.error(e);
      await router.push({name: "not-found"})
    }
  }

  interface Rule {
    validation: any,
    mutable: boolean,
    hide: boolean,
  }

  const newValueRules: {
    [up: string]: Rule
  } = {
    'firstName': {
      'validation': yup.string().required('Name must be at least 1 character long'),
      'mutable': true,
      'hide': false,
    },
    'secondName': {
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
