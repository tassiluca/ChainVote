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

  const data: Ref<User | null> = ref(null);
  const userStore = useUserStore();

  onMounted(async () => {
    await getUser();
  });

  async function getUser() {
    try {
      data.value = await userStore.getUserInfo();
    } catch (e: any) {
      await router.push({name: "not-found"})
    }
  }

  interface Rule {
    validation: any,
    mutable: boolean,
  }

  const newValueRules: {
    [up: string]: Rule
  } = {
    'firstName': {
      'validation': yup.string().required('Name must be at least 1 character long'),
      'mutable': true,
    },
    'secondName': {
      'validation': yup.string().required('Surname must be at least 1 character long'),
      'mutable': true,
    },
    'email': {
      'validation': undefined,
      'mutable': false,
    },
    'role': {
      'validation': undefined,
      'mutable': false,
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
