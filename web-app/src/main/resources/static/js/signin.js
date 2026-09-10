document.addEventListener("DOMContentLoaded", function () {

    const form = document.querySelector("form");

    const username = document.getElementById("username");
    const password = document.getElementById("password");

    const usernameError = document.getElementById("usernameError");
    const passwordError = document.getElementById("passwordError");

    const forgotPassword = document.getElementById("forgotPassword");

    const passwordToggle = document.getElementById("passwordToggle");


    /* =========================
       VALIDATION DU FORMULAIRE
       ========================= */

    form.addEventListener("submit", function (e) {

        let valid = true;

        usernameError.textContent = "";
        passwordError.textContent = "";

        username.style.border = "";
        password.style.border = "";


        if (!username.value.trim()) {

            usernameError.textContent = "Nom d'utilisateur obligatoire";
            username.style.border = "1px solid red";

            valid = false;
        }


        if (!password.value.trim()) {

            passwordError.textContent = "Mot de passe obligatoire";
            password.style.border = "1px solid red";

            valid = false;
        }


        if (!valid) {
            e.preventDefault();
        }

    });


    /* =========================
       MOT DE PASSE VISIBLE
       ========================= */

    if (passwordToggle) {

        passwordToggle.addEventListener("click", function () {

            const icon = passwordToggle.querySelector("i");


            if (password.type === "password") {

                password.type = "text";

                icon.classList.remove("fa-eye");
                icon.classList.add("fa-eye-slash");

                passwordToggle.setAttribute(
                    "aria-label",
                    "Masquer le mot de passe"
                );

            } else {

                password.type = "password";

                icon.classList.remove("fa-eye-slash");
                icon.classList.add("fa-eye");

                passwordToggle.setAttribute(
                    "aria-label",
                    "Afficher le mot de passe"
                );

            }

        });

    }


    /* =========================
       MOT DE PASSE OUBLIÉ
       ========================= */

    if (forgotPassword) {

        forgotPassword.addEventListener("click", function (e) {

            e.preventDefault();

            alert("Fonction à implémenter");

        });

    }

});