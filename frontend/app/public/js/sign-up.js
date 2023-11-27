"use strict";

$(document).ready(() => {

    const urlToSignUp = window.location.origin + '/sign-up';

    const form = document.querySelector('#login');
    const name = document.querySelector('#inputName');
    const surname = document.querySelector('#inputSurname');
    const email = document.querySelector('#inputEmail');
    const password = document.querySelector('#inputPassword');
    const repeatPassword = document.querySelector('#inputRepeatPassword');
    const role = document.querySelector('#inputRole');

    function isPasswordRepeatValid() {
        return password.value === repeatPassword.value
    }

    function clearPassword() {
        setSuccess(password)
    }

    function checkRepeatedPassword() {
        if (repeatPassword.value === '') {
            setError(repeatPassword);
            repeatPassword.style.borderColor = 'initial';
        } else if (isPasswordRepeatValid()) {
            setSuccess(repeatPassword);
            repeatPassword.style.borderColor = 'green';
        } else {
            setError(repeatPassword);
            repeatPassword.style.borderColor = 'red';
        }
    }

    form.addEventListener('submit', function(event) {
        event.preventDefault();
        validateForm();
        if (isFormValid()) {

            const data = {
                'name': name.value,
                'surname': surname.value,
                'email': email.value,
                'password': password.value,
                'role': role.value
            };

            $.ajax({
                type: "POST",
                url: urlToSignUp,
                data: JSON.stringify(data),
                contentType: "application/json; charset=utf-8"
            }).done(function(response) {
                alert(response.message)
                if(response.success) {
                    hide(document.querySelector("#div-error-sign-up"))
                    window.location.href = response.url;
                } else {
                    document.getElementById('error-sign-up').innerHTML = response.message;
                    show(document.querySelector("#div-error-sign-up"));
                }
            }).fail(function(error) {
                document.getElementById('error-sign-up').innerHTML = 'Error ' + error.status + ': ' + error.responseJSON.message;
                show(document.querySelector("#div-error-sign-up"));
            });
        }
    })

    function validateForm() {
        if (name.value.trim() === '') {
            setError(name, 'Name can\'t be empty.');
        } else {
            setSuccess(name);
        }
        if (surname.value.trim() === '') {
            setError(surname, 'Surname can\'t be empty.');
        } else {
            setSuccess(surname);
        }
        if (email.value.trim() === '') {
            setError(email, 'Email can\'t be empty.');
        } else if (isEmailValid(email.value)) {
            setSuccess(email);
        } else {
            setError(email, 'Provide valid email');
        }
        if (password.value.trim() === '') {
            setError(password, 'Password can\'t be empty.');
        } else if (!isPasswordValid(password.value)) {
            setError(password, 'Password don\'t match regex');
        } else {
            checkRepeatedPassword()
            setSuccess(password)
        }
    }
    function setError(element, errorMessage) {
        element.classList.add('error');
    }
    function setSuccess(element) {
        element.classList.remove('error');
    }
    function hide(element) {
        element.classList.add('hidden');
    }
    function show(element) {
        element.classList.remove('hidden');
    }
    function isEmailValid(value) {
        const reg = /^(([^<>()[\].,;:\s@"]+(\.[^<>()[\].,;:\s@"]+)*)|(".+"))@(([^<>()[\].,;:\s@"]+\.)+[^<>()[\].,;:\s@"]{2,})$/i;
        return reg.test(value);
    }
    function isPasswordValid(value) {
        /*
            It must not contain any whitespace.
            It must contain at least one uppercase, one lowercase and one numeric character.
            It must contain at least one special character. [~`!@#$%^&*()--+={}[]|\:;"'<>,.?/_]
            Length must be between 8 to 16 characters
        */
        const reg = /^(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[~`!@#$%^&*()--+={}\[\]|\\:;"'<>,.?/_])[a-zA-Z0-9~`!@#$%^&*()--+={}\[\]|\\:;"'<>,.?/_]{8,16}$/
        return reg.test(value);
    }
    function isFormValid() {
        let result = true;
        const inputContainers = document.querySelectorAll("#login input");
        inputContainers.forEach((container) => {
            if (container.classList.contains('error')) {
                result = false;
            }
        })
        return result
    }
    window.checkRepeatedPassword = checkRepeatedPassword
    window.clearPassword = clearPassword
});