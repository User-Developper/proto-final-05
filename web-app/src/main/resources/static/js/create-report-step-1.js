document.addEventListener("DOMContentLoaded", function () {

    const modal = document.getElementById("confirmModal");
    const openBtn = document.getElementById("openModal");
    const confirmBtn = document.getElementById("confirmSend");
    const cancelBtn = document.getElementById("cancelSend");

    const form = document.getElementById("submitForm");

    let isSubmitting = false;


    // =====================================================
    // OUVRIR MODAL
    // =====================================================

    openBtn.addEventListener("click", () => {

        modal.classList.remove("hidden");

    });


    // =====================================================
    // ANNULER
    // =====================================================

    cancelBtn.addEventListener("click", () => {

        modal.classList.add("hidden");

    });


    // =====================================================
    // CONFIRMER ENVOI
    // =====================================================

    confirmBtn.addEventListener("click", () => {

        if (isSubmitting) {
            return;
        }

        isSubmitting = true;

        // Fermer la fenêtre de confirmation
        modal.classList.add("hidden");

        // Bloquer le bouton pour éviter un double clic
        openBtn.disabled = true;

        openBtn.textContent = "Envoi...";


        // =================================================
        // ENVOI DU FORMULAIRE
        // =================================================

        form.submit();

    });

});