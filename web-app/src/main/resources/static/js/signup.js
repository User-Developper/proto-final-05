

document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");

    const username = document.getElementById("username");
    const prenom = document.getElementById("prenom");
    const email = document.getElementById("email");
    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword");
    const terms = document.querySelector('input[type="checkbox"]');

    function showError(input, message) {
        input.classList.add("error");

        let error = input.parentElement.querySelector(".error-message");

        if (!error) {
            error = document.createElement("small");
            error.className = "error-message";
            input.parentElement.appendChild(error);
        }

        error.textContent = message;
    }

    function clearError(input) {
        input.classList.remove("error");

        const error = input.parentElement.querySelector(".error-message");

        if (error) {
            error.remove();
        }
    }

    function validateEmail(emailValue) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailValue);
    }

    form.addEventListener("submit", (e) => {

        let isValid = true;

        if (username.value.trim() === "") {
            showError(username, "Le nom est obligatoire");
            isValid = false;
        } else {
            clearError(username);
        }

        if (prenom.value.trim() === "") {
            showError(prenom, "Le prénom est obligatoire");
            isValid = false;
        } else {
            clearError(prenom);
        }

//        if (!validateEmail(email.value)) {
//            showError(email, "Adresse e-mail invalide");
//            isValid = false;
//        } else {
//            clearError(email);
//        }

//        if (password.value.length < 8) {
//            showError(password, "Minimum 8 caractères");
//            isValid = false;
//        } else {
//            clearError(password);
//        }

        if (password.value !== confirmPassword.value) {
            showError(confirmPassword, "Les mots de passe ne correspondent pas");
            isValid = false;
        } else {
            clearError(confirmPassword);
        }

        if (!terms.checked) {
            alert("Veuillez accepter les conditions d'utilisation.");
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
        }
    });

});


document.addEventListener("DOMContentLoaded", function () {

    const password = document.getElementById("password");
    const passwordToggle = document.getElementById("passwordToggle");

    const confirmPassword =
        document.getElementById("confirmPassword");

    const confirmPasswordToggle =
        document.getElementById("confirmPasswordToggle");


    function togglePassword(input, button) {

        if (!input || !button) {
            return;
        }

        const icon = button.querySelector("i");

        if (input.type === "password") {

            input.type = "text";

            icon.classList.remove("fa-eye");
            icon.classList.add("fa-eye-slash");

            button.setAttribute(
                "aria-label",
                "Masquer le mot de passe"
            );

        } else {

            input.type = "password";

            icon.classList.remove("fa-eye-slash");
            icon.classList.add("fa-eye");

            button.setAttribute(
                "aria-label",
                "Afficher le mot de passe"
            );
        }
    }


    if (passwordToggle) {

        passwordToggle.addEventListener("click", function () {

            togglePassword(
                password,
                passwordToggle
            );

        });
    }


    if (confirmPasswordToggle) {

        confirmPasswordToggle.addEventListener("click", function () {

            togglePassword(
                confirmPassword,
                confirmPasswordToggle
            );

        });
    }

});

