<script setup lang="ts">
import {ref, type Ref} from "vue";
import PageTitle from "@/components/PageTitleComponent.vue";
import Form from '@/components/forms/FormComponent.vue'
import FormInput from '@/components/forms/FormInputComponent.vue'
import { useForm } from 'vee-validate'
import * as yup from 'yup'
import {type UserCreation, useUserStore} from "@/stores/user";
import BreadcrumbComponent from "@/components/BreadcrumbComponent.vue";
import Breadcrumb from "@/components/BreadcrumbComponent.vue";

const response = ref({})

const {errors, defineField} = useForm({
  validationSchema: yup.object({
    firstName: yup.string().required(),
    secondName: yup.string().required(),
    email: yup.string().email().required(),
    password: yup.string().min(8).required().matches(
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\^&\*])/,
        'Password must contain at least one uppercase letter, one lowercase letter, one number and one special character'
    ),
    confirmPassword: yup.string().oneOf([yup.ref('password'), null], 'Passwords must match')
  }),
});

const email = defineField('email')[0];
const password = defineField('password')[0];
const confirmPassword = defineField('confirmPassword')[0];
const firstName = defineField('firstName')[0];
const secondName = defineField('secondName')[0];

function onSubmit() {
  const data: UserCreation = {
    email: email.value,
    password: password.value,
    firstName: firstName.value,
    secondName: secondName.value,
  }

  useUserStore().registration(data)
    .then(() => { response.value = {success: true, msg: "User created successfully"}})
    .catch((e) => { response.value = {success: false, msg: "Can't create user"}; });
}
interface Rule {
  help: string,
  label: string,
  type: string,
  placeholder: string,
  autocomplete: string,
  ref: Ref<any>
}

const properties: {
  [prop: string]: Rule
} = {
  'email': {
    'help': 'Insert a valid email address',
    'label': 'Email',
    'type': 'email',
    'placeholder': 'example@email.it',
    'autocomplete': '',
    'ref': email
  },
  'firstName': {
    'help': 'First name must be at least 1 character long',
    'label': 'First name',
    'type': 'string',
    'placeholder': 'Enter first name',
    'autocomplete': '',
    'ref': firstName
  },
 'secondName': {
    'help': 'Second name must be at least 1 character long',
    'label': 'Second name',
    'type': 'string',
    'placeholder': 'Enter second name',
    'autocomplete': '',
    'ref': secondName
  },
  'password': {
    'help': 'Password must contain at least one uppercase letter, one lowercase letter, one number and one special character',
    'label': 'Password',
    'type': 'password',
    'placeholder': '',
    'autocomplete': '',
    'ref': password
  },
  'confirmPassword': {
    'help': 'Confirm password must be equal to password',
    'label': 'Confirm password',
    'type': 'password',
    'placeholder': '',
    'autocomplete': '',
    'ref': confirmPassword
  },
}
</script>

<template>
    <Breadcrumb :paths="[{name: 'User registration', link: '/register'}]" />
    <div class="container-sm">
        <PageTitle title="User registration" />
        <Form @submit="onSubmit" :response="response" submit-btn-name="Create an user">
            <template v-slot:body>
                <div class="col" v-for="prop in Object.keys(properties)" :key="prop">
                    <div class="row gy-5 mx-auto mt-1">
                        <div class="p-3 border bg-light">
                            <FormInput :helper="properties[prop]['help']"
                                    :input-id="`input-${prop}`"
                                    :label="properties[prop]['label']">
                                <input v-model="properties[prop].ref.value" 
                                    class="form-control"
                                    :type="properties[prop]['type']" 
                                    :autocomplete="properties[prop]['autocomplete']"
                                    :placeholder="properties[prop]['placeholder']">
                            </FormInput>
                            <span class="validation">{{ errors[prop] }}</span>
                        </div>
                    </div>
                </div>
            </template>
        </Form>
    </div>
</template>

<style scoped>
span.validation {
  color: #c70224;
}
</style>
