"use strict";

$(document).ready(() => {

    const urlToSignIn = window.location.origin + '/sign-in';

    const form = document.querySelector('#signInForm');
    const email = document.querySelector('#inputEmail');
    const password = document.querySelector('#inputPassword');

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
                hide($("#error"))
                if (window.location.href === urlToSignIn) {
                    window.location.href = response.url;
                } else {
                    window.location.reload();
                }
            } else {
                $("#error").text(response.message);
                show($("#error"));
            }
        }).fail(error => {
            $("#error").text('Error ' + error.status + ': ' + error.responseJSON.message);
            show($("#error"));
        });
    })

    function hide(element) {
        $(element).addClass('hidden');
    }
    function show(element) {
        $(element).removeClass('hidden');
    }
});