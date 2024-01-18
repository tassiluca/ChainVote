<script setup lang="ts">
import { makeRequest } from '@/assets/utils';
import { useForm } from 'vee-validate'
import * as yup from 'yup'

const {meta, errors, handleSubmit, defineField } = useForm({
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

const [email, emailAttrs] = defineField('email');
const [password, passwordAttrs] = defineField('password');
const [confirmPassword, confirmPasswordAttrs] = defineField('confirmPassword');
const [firstName, firstNameAttrs] = defineField('firstName');
const [secondName, secondNameAttrs] = defineField('secondName');

const onSubmit = handleSubmit((values) => {
    delete values.confirmPassword;

    makeRequest("http://localhost:8080/users/", "POST", values, '').then((response) => {
        alert(response.data.data);
    }).catch((error) => {
        alert(error);
    });
})

</script>

<template>
    <div class="container">
        <header class="mb-2">
            <h1> Register a new user </h1>
        </header>
        <form @submit.prevent="onSubmit">

            <div class="row mb-3">
                <label for="voteCode" class="form-label">Email</label>
                <input v-model="email" type="email" class="form-control" id="registration-email" placeholder="user.email@email.it">
                <span>{{ errors.email }}</span>
            </div>
            
            <div class="row mb-3">
                <label for="voteCode" class="form-label">Password</label>
                <input v-model="password" type="password" class="form-control" id="registration-password">
                <span>{{ errors.password }}</span>

                <label for="voteCode" class="form-label">Repeat password</label>
                <input  v-model="confirmPassword" type="password" class="form-control" id="registration-confirm-password">
                <span>{{ errors.confirmPassword }}</span>
            </div>

            <div class="row mb-3">
                <label for="voteCode" class="form-label">Name</label>
                <input v-model="firstName" type="text" class="form-control" id="registration-name">
                <span>{{ errors.name }}</span>
            </div>

            <div class="row mb-3">
                <label for="voteCode" class="form-label">Surname</label>
                <input v-model="secondName" type="text" class="form-control" id="registration-surname">
                <span>{{ errors.surname }}</span>
            </div>

            <button type="submit" class="btn btn-primary" :disabled="!meta.valid">Register a new user</button>
        </form>
    </div>
</template>


