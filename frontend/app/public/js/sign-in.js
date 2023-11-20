"use strict";

$(document).ready(() => {

    const urlToSignIn = window.location.origin + '/sign-in';

    const form = document.getElementById('login');
    const email = document.getElementById('inputEmail');
    const password = document.getElementById('inputPassword');

    form.addEventListener('submit', async function(event) {
        event.preventDefault();

        const data = {
            'email': email.value,
            'password': password.value,
        };

        $.ajax({
            type: "POST",
            url: urlToSignIn,
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8"
        }).done(function(response) {
            if (response.success) {
                hide(document.querySelector("#div-error-sign-in"))
                window.location.href = response.url;
            } else {
                document.getElementById('error-sign-in').innerHTML = response.message;
                show(document.querySelector("#div-error-sign-in"));
            }
        }).fail(function(error) {
            document.getElementById('error-sign-in').innerHTML = 'Error ' + error.status + ': ' + error.statusText;
            show(document.querySelector("#div-error-sign-in"));
        });
    })

    function hide(element) {
        element.classList.add('hidden');
    }
    function show(element) {
        element.classList.remove('hidden');
    }
});