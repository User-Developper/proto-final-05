document.addEventListener("DOMContentLoaded", function () {

    const form = document.querySelector("form");

    const username = document.getElementById("username");
    const password = document.getElementById("password");

    const usernameError = document.getElementById("usernameError");
    const passwordError = document.getElementById("passwordError");

    form.addEventListener("submit", function (e) {

        let valid = true;

        usernameError.textContent = "";
        passwordError.textContent = "";

        username.style.border = "";
        password.style.border = "";

        if (!username.value.trim()) {
            usernameError.textContent = "Username obligatoire";
            username.style.border = "1px solid red";
            valid = false;
        }

        if (!password.value.trim()) {
            passwordError.textContent = "Password obligatoire";
            password.style.border = "1px solid red";
            valid = false;
        }

        if (!valid) {
            e.preventDefault();
        }
    });

    document.getElementById("forgotPassword").addEventListener("click", function (e) {
        e.preventDefault();
        alert("Fonction à implémenter");
    });
});