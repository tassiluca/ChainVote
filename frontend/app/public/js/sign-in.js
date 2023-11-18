"use strict";
$(document).ready(() => {
    const form = document.getElementById('login');
    const email = document.getElementById('inputEmail');
    const password = document.getElementById('inputPassword');

    function goPreviousWindow() {
        window.history.back();
    }

    form.addEventListener('submit', async function(event) {
        event.preventDefault();

        const data = {
            'email': email.value,
            'password': password.value,
        };

        $.ajax({
            type: "POST",
            url: 'http://localhost:3000/sign-in',
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8"
        }).done(function(response) {
            alert(response.message)
            // console.log('Response: ' + response)
            if(response.success) {
                hide(document.querySelector("#div-error-sign-in"))
                window.location.href = response.url;
            } else {
                document.getElementById('error-sign-in').innerHTML = response.message;
                show(document.querySelector("#div-error-sign-in"));
            }
        }).fail(function(error) {
            alert(error.message)
            // console.log('Error: ' + JSON.stringify(error))
            document.getElementById('error-sign-in').innerHTML = 'Error ' + error.status + ': ' + error.responseJSON.message;
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