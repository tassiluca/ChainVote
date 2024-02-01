<template>
  <Breadcrumb :paths="[{name: 'Login', link: '/login'}, {name: 'Password forgotten', link: '/password-retrieval'}]" />
  <div class="container-sm col-md-6 text-center">
    <PageTitle title="Password forgotten" />
        <Form @submit="onFormSubmit" :response="response" submit-btn-name="Request password">
          <template v-slot:body>
            <FormInput helper="Enter your e-mail" input-id="username" label="Mail" pre="@">
              <input v-model="username" type="email" class="form-control" placeholder="mario.rossi@gmail.com ༝༚༝༚" required autocomplete="e-mail"/>
            </FormInput>
          </template>
          <template v-slot:footer>
            <div class="col-sm">
              <ul>
                <li><a href="/login">Login</a></li>
                <li><a href="/register">Register</a></li>
              </ul>
            </div>
          </template>
        </Form>
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
<script setup lang="ts">
import Breadcrumb from "@/components/BreadcrumbComponent.vue";
import Form from "@/components/forms/FormComponent.vue";
import FormInput from "@/components/forms/FormInputComponent.vue";
import PageTitle from "@/components/PageTitleComponent.vue";
import {useAuthStore} from "@/stores/auth";
import {useUserStore} from "@/stores/user";
import {onMounted, ref} from "vue";
import router from "@/router";

const authStore = useAuthStore();
const userStore = useUserStore();

const response = ref({})
const username = ref("")

onMounted(() => {
  if (authStore.isLogged) {
    router.push("/dashboard");
  }
})

async function onFormSubmit() {
  try {
    const res = await userStore.passwordResetRequest(username.value);
    response.value = {
      success: true,
      msg: `An e-mail has been sent to ${username.value} with instructions to reset your password.`
    };
  } catch (e: any) {
    const genericErrorMsg = "Type a valid username and try again.";
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