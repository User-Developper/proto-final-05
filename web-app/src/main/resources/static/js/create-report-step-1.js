document.addEventListener("DOMContentLoaded", function () {

    const modal = document.getElementById("confirmModal");
    const openBtn = document.getElementById("openModal");
    const confirmBtn = document.getElementById("confirmSend");
    const cancelBtn = document.getElementById("cancelSend");

    const form = document.getElementById("submitForm");

    let isSubmitting = false;

    openBtn.addEventListener("click", () => {

        modal.classList.remove("hidden");

    });

    cancelBtn.addEventListener("click", () => {

        modal.classList.add("hidden");

    });

    confirmBtn.addEventListener("click", () => {

        if (isSubmitting) {
            return;
        }

        isSubmitting = true;

        modal.classList.add("hidden");

        openBtn.disabled = true;

        openBtn.textContent = "Envoi...";

        form.submit();

    });

});