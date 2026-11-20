
document.addEventListener("DOMContentLoaded", () => {

    const grid = document.getElementById("homeReportsGrid");

    if (!grid) return;

    const categoryLabels = {
        AIR_POLLUTION: "Pollution de l'air",
        WATER_POLLUTION: "Pollution de l'eau",
        SOIL_POLLUTION: "Pollution des sols",
        NOISE: "Bruit",
        WASTE: "Déchets",
        VEGETATION: "Végétation",
        TOXIC_PRODUCTS: "Produits toxiques"
    };

    const gravityLabels = {
        LOW: "Faible",
        BASIC: "Faible",
        MEDIUM: "Modérée",
        HIGH: "Élevée",
        CRITICAL: "Critique"
    };

    async function loadReports() {
        try {
            const response = await fetch("/api/reports?page=0&size=3");

            if (!response.ok) {
                throw new Error("Erreur lors du chargement des signalements");
            }

            const data = await response.json();
            const reports = data.content || [];

            grid.innerHTML = "";

            if (reports.length === 0) {
                grid.innerHTML = `
                    <div class="reports-empty">
                        <i class="fa-regular fa-clipboard"></i>
                        <h3>Aucun signalement récent</h3>
                        <p>Les nouveaux signalements apparaîtront ici.</p>
                    </div>
                `;
                return;
            }

            reports.forEach(report => {
                const card = createReportCard(report);
                grid.appendChild(card);
                loadReportImages(report.id, card);
            });

        } catch (error) {
            console.error(error);

            grid.innerHTML = `
                <div class="reports-empty">
                    <p>Impossible de charger les signalements.</p>
                </div>
            `;
        }
    }

    function createReportCard(report) {
        const card = document.createElement("article");
        card.className = "report-card";

        const category = categoryLabels[report.category] || report.category || "";
        const gravity = gravityLabels[report.gravity] || report.gravity || "";

        const date = report.date
            ? new Intl.DateTimeFormat("fr-FR").format(
                new Date(report.date + "T00:00:00")
            )
            : "";

        card.innerHTML = `
            <div class="report-images"></div>

            <div class="report-content">

                <div class="badges">
                    <span class="badge type">${escapeHtml(category)}</span>
                    <span class="badge gravity">${escapeHtml(gravity)}</span>
                </div>

                <h3>${escapeHtml(report.title || "")}</h3>

                <p class="description">
                    ${escapeHtml(report.description || "")}
                </p>

                <div class="report-location">
                    <i class="fa-solid fa-location-dot"></i>
                    <span>${escapeHtml(report.location || "")}</span>
                </div>

                <div class="date">
                    <i class="fa-regular fa-calendar"></i>
                    <span>${date}</span>
                </div>

                <a href="/report?id=${encodeURIComponent(report.id)}"
                   class="report-detail-link">
                    Voir le signalement
                    <i class="fa-solid fa-arrow-right"></i>
                </a>

            </div>
        `;

        return card;
    }

    async function loadReportImages(reportId, card) {
        const container = card.querySelector(".report-images");

        try {
            const response = await fetch(`/api/reports/${reportId}/images`);

            if (!response.ok) return;

            const images = await response.json();

            if (!images || images.length === 0) {
                container.remove();
                return;
            }

            images.forEach(image => {
                const img = document.createElement("img");

                img.src = `/report-images/${reportId}/${image.id}`;
                img.alt = image.fileName || "Photo du signalement";
                img.loading = "lazy";

                container.appendChild(img);
            });

            if (images.length === 1) {
                container.classList.add("single-image");
            } else if (images.length === 2) {
                container.classList.add("two-images");
            }

        } catch (error) {
            console.error("Erreur lors du chargement des photos :", error);
        }
    }

    function escapeHtml(value) {
        return String(value).replace(/[&<>"']/g, character => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        })[character]);
    }

    loadReports();

});
