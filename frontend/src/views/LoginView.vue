<script setup lang="ts">
import Breadcrumb from '@/components/BreadcrumbComponent.vue'
import PageTitle from '@/components/PageTitleComponent.vue'
import FormSwitcher from '@/components/forms/FormSwitcherComponent.vue'
import Form from '@/components/forms/FormComponent.vue'
import FormInput from '@/components/forms/FormInputComponent.vue'
import {Role, useAuthStore} from '@/stores/auth'
import {onMounted, ref} from "vue";
import router from "@/router";

const authStore = useAuthStore();

const response = ref({})
const username = ref("")
const password = ref("")
const role = ref(Role.User)

onMounted(() => {
  if (authStore.isLogged) {
    router.push("/dashboard");
  }
})

async function onFormSubmit() {
  try {
    await authStore.login(role.value, username.value, password.value);
  } catch (e: any) {
    const genericErrorMsg = "Type the correct username and password, and try again.";
    if ('response' in e) {
      response.value = {
        success: false,
        msg: `${e.response.data.error.message}: ${e.response.data.error.name}. ${genericErrorMsg}`
      };
    } else {
      response.value = {success: false, msg: `${e.message}. ${genericErrorMsg}`};
    }
  }
}
</script>

<template>
  <Breadcrumb :paths="[{name: 'Login', link: '/login'}]" />
  <div class="container-sm col-md-6 text-center">
    <PageTitle title="Login" />
    <FormSwitcher left="User" right="Admin" :on-right-click="() => { role = Role.Admin }" :on-left-click="() => { role = Role.User }">
      <!-- USER LOGIN FORM -->
      <template v-slot:left>
        <Form @submit="onFormSubmit" :response="response" submit-btn-name="Login">
          <template v-slot:body>
            <FormInput helper="Enter your username" input-id="username" label="Username" pre="@">
              <input v-model="username" type="email" class="form-control" placeholder="mario.rossi@gmail.com ༝༚༝༚" required autocomplete="username"/>
            </FormInput>
            <FormInput helper="Enter your password" input-id="password" label="Password" pre="ꗃ">
              <input v-model="password" type="password" class="form-control" placeholder="Your super secure password •ᴗ•" required autocomplete="current-password"/>
            </FormInput>
          </template>
          <template v-slot:footer>
            <div class="col-sm">
              <ul>
                <li><a href="#">I forgot the password</a></li>
                <li><a href="#">Register</a></li>
              </ul>
            </div>
          </template>
        </Form>
      </template>
      <!-- ADMIN LOGIN FORM -->
      <template v-slot:right>
        <Form @submit="onFormSubmit" :response="response" submit-btn-name="Login">
          <template v-slot:body>
            <FormInput helper="Enter your username" input-id="username" label="Username" pre="@">
              <input v-model="username" type="email" class="form-control" placeholder="mario.rossi@gmail.com ༝༚༝༚" required autocomplete="username"/>
            </FormInput>
            <FormInput helper="Enter your password" input-id="password" label="Password" pre="ꗃ">
              <input v-model="password" type="password" class="form-control" placeholder="Your super secure password •ᴗ•" required autocomplete="current-password"/>
            </FormInput>
          </template>
        </Form>
      </template>
    </FormSwitcher>
  </div>
</template>

<style scoped>
ul {
  list-style-type: none;
  padding: 0;
  display: flex;
  justify-content: center;
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
</style>