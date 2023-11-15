"use strict";
$(document).ready(() => {
    const errordiv = document.getElementById('error-case')
    const form = document.getElementById('login');
    const email = document.getElementById('inputEmail');
    const password = document.getElementById('inputPassword');

    var goPreviousWindow = function() {
        window.history.back();
    }

    form.addEventListener('submit', function(event) {
        event.preventDefault();
        $.ajax({
            url: "http://localhost:3000/sign-in", // + process.env.SIGN_IN_URL,
            type: "POST",
            data: {
                'email': email.value,
                'password': password.value
            },
            success: function(msg) {
                hide(document.querySelector("#div-error-sign-in"))
                window.location = 'http://localhost:3000/'
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                show(document.querySelector("#div-error-sign-in"))
            }
        });
    })
    function hide(element) {
        element.classList.add('hidden');
    }
    function show(element) {
        element.classList.remove('hidden');
    }
});