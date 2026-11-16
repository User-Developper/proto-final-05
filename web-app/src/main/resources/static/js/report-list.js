document.addEventListener("DOMContentLoaded", () => {

    const grid = document.getElementById("reportsGrid");
    const pagination = document.getElementById("pagination");

    const searchInput = document.getElementById("searchInput");
    const searchBtn = document.getElementById("searchBtn");
    const categorySelect = document.getElementById("categoryFilter");
    const gravitySelect = document.getElementById("gravityFilter");

    const openBtn = document.getElementById("openFilters");
    const closeBtn = document.getElementById("closeFilters");
    const overlay = document.getElementById("overlay");
    const sheet = document.getElementById("filterSheet");

    let currentPage = 0;
    let requestVersion = 0;
    const pageSize = 6;

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
        MEDIUM: "Moyenne",
        HIGH: "Élevée"
    };

    openBtn?.addEventListener("click", () => {
        sheet.classList.add("active");
        overlay.classList.add("active");
    });

    closeBtn?.addEventListener("click", closeSheet);
    overlay?.addEventListener("click", closeSheet);

    function closeSheet() {
        sheet.classList.remove("active");
        overlay.classList.remove("active");
    }

    async function loadReports(page = 0) {

        currentPage = page;
        const version = ++requestVersion;

        const q = searchInput?.value.trim() || "";
        const category = categorySelect?.value || "";
        const gravity = gravitySelect?.value || "";

        const params = new URLSearchParams({
            page: page,
            size: pageSize
        });

        if (q) {
            params.append("q", q);
        }

        if (category) {
            params.append("category", category);
        }

        if (gravity) {
            params.append("gravity", gravity);
        }

        grid.replaceChildren();
        pagination.replaceChildren();

        const loading = document.createElement("p");
        loading.className = "loading-message";
        loading.textContent = "Chargement des signalements...";
        grid.appendChild(loading);

        try {
            const response = await fetch(`/api/reports?${params.toString()}`);

            if (!response.ok) {
                throw new Error("Erreur lors du chargement des signalements");
            }

            const data = await response.json();

            if (version !== requestVersion) {
                return;
            }

            renderReports(data.content || []);
            renderPagination(data.number, data.totalPages);

        } catch (error) {
            if (version !== requestVersion) {
                return;
            }

            console.error(error);

            grid.replaceChildren();

            const message = document.createElement("p");
            message.className = "error-message";
            message.textContent = "Impossible de charger les signalements.";

            grid.appendChild(message);
        }
    }

    function renderReports(reports) {

        grid.replaceChildren();

        if (!reports || reports.length === 0) {
            const message = document.createElement("p");
            message.className = "empty-message";
            message.textContent = "Aucun signalement trouvé.";

            grid.appendChild(message);
            return;
        }

        reports.forEach(report => {
            const card = createReportCard(report);
            grid.appendChild(card);
            loadReportImages(report.id, card);
        });
    }

    function createReportCard(report) {

        const article = document.createElement("article");
        article.className = "report-card";

        const imageContainer = document.createElement("div");
        imageContainer.className = "report-images";

        const content = document.createElement("div");
        content.className = "report-content";

        const title = document.createElement("h3");
        title.textContent = report.title || "Signalement sans titre";

        const badges = document.createElement("div");
        badges.className = "badges";

        const categoryBadge = document.createElement("span");
        categoryBadge.className = "badge type";
        categoryBadge.textContent =
            categoryLabels[report.category] || report.category || "Non précisée";

        const gravityBadge = document.createElement("span");
        gravityBadge.className = "badge gravity";
        gravityBadge.textContent =
            gravityLabels[report.gravity] || report.gravity || "Non précisée";

        badges.append(categoryBadge, gravityBadge);

        const description = document.createElement("p");
        description.className = "description";
        description.textContent =
            report.description || "Aucune description disponible.";

        content.append(title, badges, description);

        if (report.location) {
            const location = document.createElement("p");
            location.className = "report-location";

            const locationIcon = document.createElement("i");
            locationIcon.className = "fa-solid fa-location-dot";

            const locationText = document.createElement("span");
            locationText.textContent = report.location;

            location.append(locationIcon, locationText);
            content.appendChild(location);
        }

        if (report.date) {
            const date = document.createElement("p");
            date.className = "date";

            const dateIcon = document.createElement("i");
            dateIcon.className = "fa-regular fa-calendar";

            const dateText = document.createElement("span");

            const parsedDate = new Date(report.date + "T00:00:00");

            dateText.textContent = Number.isNaN(parsedDate.getTime())
                ? report.date
                : new Intl.DateTimeFormat("fr-FR").format(parsedDate);

            date.append(dateIcon, dateText);
            content.appendChild(date);
        }

        const detailLink = document.createElement("a");
        detailLink.className = "report-detail-link";
        detailLink.href = `/report?id=${encodeURIComponent(report.id)}`;
        detailLink.textContent = "Voir le signalement";

        content.appendChild(detailLink);

        article.append(imageContainer, content);

        return article;
    }

    function updateImageLayout(imageContainer) {

        const imageCount = imageContainer.querySelectorAll(".report-image-link").length;

        imageContainer.classList.toggle("single-image", imageCount === 1);
        imageContainer.classList.toggle("two-images", imageCount === 2);
    }

    async function loadReportImages(reportId, card) {

        if (reportId == null) {
            return;
        }

        const imageContainer = card.querySelector(".report-images");

        try {
            const response = await fetch(
                `/api/reports/${encodeURIComponent(reportId)}/images`
            );

            if (!response.ok) {
                return;
            }

            const images = await response.json();

            if (!Array.isArray(images) || images.length === 0) {
                return;
            }

            const visibleImages = images.filter(image => image && image.id);

            visibleImages.forEach((image, index) => {

                const imageLink = document.createElement("a");
                imageLink.className = "report-image-link";
                imageLink.href =
                    `/report?id=${encodeURIComponent(reportId)}`;

                const img = document.createElement("img");
                img.className = "report-image";
                img.src =
                    `/report-images/${encodeURIComponent(reportId)}/${encodeURIComponent(image.id)}`;
                img.alt = image.fileName
                    ? `Photo du signalement : ${image.fileName}`
                    : `Photo ${index + 1} du signalement`;
                img.loading = "lazy";

                img.addEventListener("error", () => {
                    imageLink.remove();
                    updateImageLayout(imageContainer);
                });

                imageLink.appendChild(img);
                imageContainer.appendChild(imageLink);
            });

            updateImageLayout(imageContainer);

        } catch (error) {
            console.error(
                `Erreur lors du chargement des photos du signalement ${reportId}`,
                error
            );
        }
    }

    function renderPagination(current, total) {

        pagination.replaceChildren();

        if (!total || total <= 1) {
            return;
        }

        if (current > 0) {
            const previousButton = document.createElement("button");
            previousButton.type = "button";
            previousButton.textContent = "Précédent";
            previousButton.addEventListener("click", () => {
                loadReports(current - 1);
            });

            pagination.appendChild(previousButton);
        }

        const pageInfo = document.createElement("span");
        pageInfo.textContent = `${current + 1} / ${total}`;

        pagination.appendChild(pageInfo);

        if (current < total - 1) {
            const nextButton = document.createElement("button");
            nextButton.type = "button";
            nextButton.textContent = "Suivant";
            nextButton.addEventListener("click", () => {
                loadReports(current + 1);
            });

            pagination.appendChild(nextButton);
        }
    }

    let searchTimeout;

    searchInput?.addEventListener("input", () => {
        clearTimeout(searchTimeout);

        searchTimeout = setTimeout(() => {
            loadReports(0);
        }, 400);
    });

    searchBtn?.addEventListener("click", () => {
        clearTimeout(searchTimeout);
        loadReports(0);
    });

    categorySelect?.addEventListener("change", () => {
        loadReports(0);
    });

    gravitySelect?.addEventListener("change", () => {
        loadReports(0);
    });

    window.loadReports = loadReports;

    loadReports();
});