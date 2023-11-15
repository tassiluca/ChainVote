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
        if (isFormValid()) {
            unsetWrong(errordiv)
            form.submit();
        } else {
            setWrong(errordiv);
        }
    })

    function isFormValid() {
        return false;
    }

    function setWrong(element) {
        element.classList.remove('hidden');
    }

    function unsetWrong(element) {
        element.classList.add('hidden');
    }
});