document.addEventListener("DOMContentLoaded", function () {

    const modal = document.getElementById("confirmModal");
    const openBtn = document.getElementById("openModal");
    const confirmBtn = document.getElementById("confirmSend");
    const cancelBtn = document.getElementById("cancelSend");

    const form = document.getElementById("submitForm");

    let isSubmitting = false;

    // OUVRIR MODAL
    openBtn.addEventListener("click", () => {
        modal.classList.remove("hidden");
    });

    // ANNULER
    cancelBtn.addEventListener("click", () => {
        modal.classList.add("hidden");
    });

    // CONFIRMER ENVOI
    confirmBtn.addEventListener("click", async () => {

        if (isSubmitting) return;
        isSubmitting = true;

        modal.classList.add("hidden");

        // lock bouton pour éviter double click
        openBtn.disabled = true;
        openBtn.textContent = "Envoi...";

        try {
            const res = await fetch(form.action, {
                method: "POST",
                body: new FormData(form)
            });

            if (!res.ok) throw new Error("Erreur serveur");

            // redirect direct
            window.location.href = "/report-success";

        } catch (e) {

            console.error(e);

            alert("Erreur lors de l'envoi");

            isSubmitting = false;

            openBtn.disabled = false;
            openBtn.textContent = "Envoyer 🚀";
        }
    });

});