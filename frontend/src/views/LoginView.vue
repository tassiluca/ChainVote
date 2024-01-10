<script setup lang="ts">
import Breadcrumb from '@/components/BreadcrumbComponent.vue'
import PageTitle from '@/components/PageTitleComponent.vue'
import FormSwitcher from '@/components/forms/FormSwitcherComponent.vue'
import Form from '@/components/forms/FormComponent.vue'
import FormInput from '@/components/forms/FormInputComponent.vue'
import { useAuthStore } from '@/stores/auth'

async function onUserFormSubmit() {
  const authStore = useAuthStore();
  await authStore.login("email.prova@test.it", "Password1!1");
}

</script>

<template>
  <Breadcrumb :paths="[{name: 'Login', link: '/login'}]" />
  <div class="container-sm col-md-6 text-center">
    <PageTitle title="Login" />
    <FormSwitcher left="User" right="Admin">
      <!-- USER LOGIN FORM -->
      <template v-slot:left>
        <Form @submit.prevent="onUserFormSubmit" submit-btn-name="Login">
          <template v-slot:body>
            <FormInput helper="Enter your username" input-id="username" label="Username">
              <div class="col-auto">
                <div class="input-group mb-2">
                  <div class="input-group-prepend">
                    <div class="input-group-text">@</div>
                  </div>
                  <input type="email" class="form-control" placeholder="mario.rossi@gmail.com ༝༚༝༚" required />
                </div>
              </div>
            </FormInput>
            <FormInput helper="Enter your password" input-id="password" label="Password">
              <div class="input-group mb-2">
                <div class="input-group-prepend">
                  <div class="input-group-text">ꗃ</div>
                </div>
                <input type="password" class="form-control" placeholder="Your super secure password •ᴗ•" required />
              </div>
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
        <Form submit-btn-name="Login">
          <template v-slot:body>
            <FormInput helper="Enter your username" input-id="username" label="Username">
              <div class="col-auto">
                <div class="input-group mb-2">
                  <div class="input-group-prepend">
                    <div class="input-group-text">@</div>
                  </div>
                  <input type="email" class="form-control" placeholder="mario.rossi@gmail.com ༝༚༝༚" required />
                </div>
              </div>
            </FormInput>
            <FormInput helper="Enter your password" input-id="password" label="Password">
              <div class="input-group mb-2">
                <div class="input-group-prepend">
                  <div class="input-group-text">ꗃ</div>
                </div>
                <input type="password" class="form-control" placeholder="Your super secure password •ᴗ•" required />
              </div>
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