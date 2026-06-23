document.addEventListener("DOMContentLoaded", () => {

    const grid = document.getElementById("reportsGrid");
    const pagination = document.getElementById("pagination");

    const searchInput = document.getElementById("searchInput");
    const categorySelect = document.getElementById("categoryFilter");
    const gravitySelect = document.getElementById("gravityFilter");

    const openBtn = document.getElementById("openFilters");
    const closeBtn = document.getElementById("closeFilters");
    const overlay = document.getElementById("overlay");
    const sheet = document.getElementById("filterSheet");
    const applyBtn = document.getElementById("applyFilters");

    let currentPage = 0;
    const pageSize = 6;

    // =========================
    // OPEN / CLOSE FILTERS
    // =========================
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

    // =========================
    // MAIN LOAD
    // =========================
    async function loadReports(page = 0) {

        currentPage = page;

        const q = searchInput?.value || "";
        const category = categorySelect?.value || "";
        const gravity = gravitySelect?.value || "";

        let url = `/api/reports?page=${page}&size=${pageSize}`;
//        let url = "http://localhost:8083/api/reports?page=0&size=6";

        if (q) url += `&q=${encodeURIComponent(q)}`;
        if (category) url += `&category=${category}`;
        if (gravity) url += `&gravity=${gravity}`;

        console.log("CALL:", url);

        try {
            const res = await fetch(url);

            if (!res.ok) {
                const text = await res.text();
                console.error("API ERROR:", text);
                grid.innerHTML = `<p>Erreur chargement données</p>`;
                return;
            }

            const data = await res.json();

            renderReports(data.content);
            renderPagination(data.number, data.totalPages);

        } catch (err) {
            console.error("NETWORK ERROR:", err);
            grid.innerHTML = `<p>Erreur réseau</p>`;
        }
    }

    // =========================
    // RENDER REPORTS
    // =========================
    function renderReports(reports) {

        grid.innerHTML = "";

        if (!reports || reports.length === 0) {
            grid.innerHTML = `<p>Aucun résultat</p>`;
            return;
        }

        reports.forEach(r => {
            grid.innerHTML += `
                <article class="report-card">
                    <div class="report-content">

                        <h3>${r.title}</h3>

                        <div class="badges">
                            <span class="badge type">${r.category}</span>
                            <span class="badge gravity">${r.gravity}</span>
                        </div>

                        <p class="description">
                            ${r.description}
                        </p>

                    </div>
                </article>
            `;
        });
    }

    // =========================
    // PAGINATION
    // =========================
    function renderPagination(current, total) {

        pagination.innerHTML = "";

        if (current > 0) {
            pagination.innerHTML += `
                <button onclick="window.loadReports(${current - 1})">
                    Précédent
                </button>
            `;
        }

        pagination.innerHTML += `<span>${current + 1} / ${total}</span>`;

        if (current < total - 1) {
            pagination.innerHTML += `
                <button onclick="window.loadReports(${current + 1})">
                    Suivant
                </button>
            `;
        }
    }

    window.loadReports = loadReports;

    // =========================
    // FILTER EVENTS
    // =========================
    applyBtn?.addEventListener("click", () => {
        closeSheet();
        loadReports(0);
    });

    categorySelect?.addEventListener("change", () => loadReports(0));
    gravitySelect?.addEventListener("change", () => loadReports(0));

    // =========================
    // SEARCH (debounce)
    // =========================
    searchInput?.addEventListener("input", () => {

        clearTimeout(window._t);

        window._t = setTimeout(() => {
            loadReports(0);
        }, 400);
    });

    // INIT
    loadReports();
});