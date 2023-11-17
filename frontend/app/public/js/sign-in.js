"use strict";
$(document).ready(() => {
    const errordiv = document.getElementById('error-case')
    const form = document.getElementById('login');
    const email = document.getElementById('inputEmail');
    const password = document.getElementById('inputPassword');

    var goPreviousWindow = function() {
        window.history.back();
    }

    form.addEventListener('submit', async function(event) {
        event.preventDefault();
        try {
            const response = await fetch('http://localhost:3000/sign-in', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json' // Set the content type to JSON
                },
                body: {'email': email.value, 'password': password.value} // Add the JSON string to the body
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            // If the response is JSON, you can parse it
            const result = await response.json();
            console.log('Success:', result);

            // If the response contains a redirect, navigate to the redirected URL
            window.location.href = result.url;

        } catch (error) {
            console.error('Error:', error.message);
            // Handle the error
        }

        /*
        $.ajax({
            url: 'http://localhost:3000/sign-in', // + process.env.SIGN_UP_URL,
            type: "POST",
            data: {
                'email': email.value,
                'password': password.value
            },
            success: function(msg) {
                console.log(msg)
                hide(document.querySelector("#div-error-sign-in"))
                // If the response contains a redirect, navigate to the redirected URL
                window.location.href = msg.url;
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                console.log(errorThrown);
                show(document.querySelector("#div-error-sign-in"));
            }
        });

         */
    })
    function hide(element) {
        element.classList.add('hidden');
    }
    function show(element) {
        element.classList.remove('hidden');
    }
});