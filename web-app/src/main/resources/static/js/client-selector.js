document.addEventListener("DOMContentLoaded", function () {

    // =====================
    // MAP INIT
    // =====================
    const map = L.map('map').setView([46.6, 2.5], 6);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap'
    }).addTo(map);

    // =====================
    // ICONS
    // =====================
    const clientIcon = L.icon({
        iconUrl: "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-blue.png",
        shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
        iconSize: [25, 41],
        iconAnchor: [12, 41]
    });

    const selectedIcon = L.icon({
        iconUrl: "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-green.png",
        shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
        iconSize: [25, 41],
        iconAnchor: [12, 41]
    });

    const searchIcon = L.icon({
        iconUrl: "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png",
        shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
        iconSize: [25, 41],
        iconAnchor: [12, 41]
    });

    // =====================
    // STATE
    // =====================
    let searchMarker = null;
    let selectedMarker = null;
    let activeCircle = null;

    const clients = window.clients || [];

    // =====================
    // DOM
    // =====================
    const searchInput = document.getElementById("searchClient");
    const suggestions = document.getElementById("suggestions");

    const selectedText = document.getElementById("selectedClient");
    const selectedZone = document.getElementById("selectedZone");
    const clientIdInput = document.getElementById("clientId");

    const submitButton = document.querySelector(".btn-primary");
    submitButton.disabled = true;

    // =====================
    // DISTANCE (Haversine)
    // =====================
    function distance(lat1, lon1, lat2, lon2) {
        const R = 6371;

        const dLat = (lat2 - lat1) * Math.PI / 180;
        const dLon = (lon2 - lon1) * Math.PI / 180;

        const a =
            Math.sin(dLat / 2) ** 2 +
            Math.cos(lat1 * Math.PI / 180) *
            Math.cos(lat2 * Math.PI / 180) *
            Math.sin(dLon / 2) ** 2;

        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // =====================
    // DISPLAY CLIENTS (BLUE)
    // =====================
    clients.forEach(c => {

        if (!c.latitude || !c.longitude) return;

        const marker = L.circleMarker([c.latitude, c.longitude], {
            radius: 6,
            color: "#2563eb",
            fillColor: "#2563eb",
            fillOpacity: 0.8
        }).addTo(map);

        marker.bindPopup(`
            <b>${c.name}</b><br>
            Zone: ${c.perimeter} km
        `);

        marker.on("click", () => selectClient(c));
    });

    // =====================
    // SELECT CLIENT (GREEN)
    // =====================
    function selectClient(client) {

        const lat = parseFloat(client.latitude);
        const lng = parseFloat(client.longitude);

        selectedText.innerText = client.name;

        selectedZone.innerHTML = `
            <div><i class="fa-solid fa-city"></i> ${client.city ?? "Ville inconnue"}</div>
            <div><i class="fa-solid fa-location-dot"></i> ${client.address ?? "Adresse inconnue"}</div>
            <div><i class="fa-solid fa-ruler"></i> ${client.perimeter} km</div>
        `;

        clientIdInput.value = client.id;
        submitButton.disabled = false;

        // reset old selection
        if (selectedMarker) map.removeLayer(selectedMarker);
        if (activeCircle) map.removeLayer(activeCircle);

        // marker selected (GREEN)
        selectedMarker = L.marker([lat, lng], {
            icon: selectedIcon
        }).addTo(map);

        // circle selected (GREEN)
        activeCircle = L.circle([lat, lng], {
            radius: client.perimeter * 1000,

            color: '#10b981',
            weight: 2,
            fillColor: '#34d399',
            fillOpacity: 0.18
        }).addTo(map);

        map.flyTo([lat, lng], 12, { duration: 0.8 });
    }

    // =====================
    // SEARCH OSM (FR ONLY)
    // =====================
    let timeout = null;
    let controller = null;

    searchInput.addEventListener("input", function () {

        const query = this.value.trim();
        suggestions.innerHTML = "";

        if (query.length < 3) return;

        clearTimeout(timeout);

        timeout = setTimeout(() => {

            if (controller) controller.abort();
            controller = new AbortController();

            fetch(
                `https://nominatim.openstreetmap.org/search?format=json&countrycodes=fr&limit=8&q=${encodeURIComponent(query)}`,
                { signal: controller.signal }
            )
                .then(res => res.json())
                .then(data => {

                    suggestions.innerHTML = "";

                    const unique = new Map();
                    data.forEach(i => unique.set(i.place_id, i));

                    const results = [...unique.values()].slice(0, 5);

                    if (results.length === 0) {
                        suggestions.innerHTML = `<div class="suggestion-item">Aucun résultat</div>`;
                        return;
                    }

                    results.forEach(item => {

                        const div = document.createElement("div");
                        div.className = "suggestion-item";

                        div.innerHTML = `
                            <div class="suggestion-title">
                                <i class="fa-solid fa-location-dot"></i>
                                ${item.display_name}
                            </div>
                        `;

                        div.onclick = () => {

                            const lat = parseFloat(item.lat);
                            const lon = parseFloat(item.lon);

                            // search marker (RED)
                            if (searchMarker) map.removeLayer(searchMarker);

                            searchMarker = L.marker([lat, lon], {
                                icon: searchIcon
                            }).addTo(map);

                            map.flyTo([lat, lon], 13, { duration: 0.8 });

                            searchInput.value = item.display_name;
                            suggestions.innerHTML = "";

                            // AUTO SELECT NEAREST CLIENT
                            if (clients.length) {

                                const nearest = clients.reduce((a, b) => {

                                    const da = distance(lat, lon, a.latitude, a.longitude);
                                    const db = distance(lat, lon, b.latitude, b.longitude);

                                    return da < db ? a : b;
                                });

                                selectClient(nearest);
                            }
                        };

                        suggestions.appendChild(div);
                    });

                });

        }, 300);
    });

    // =====================
    // CLOSE SUGGESTIONS
    // =====================
    document.addEventListener("click", function (e) {
        if (!searchInput.contains(e.target) && !suggestions.contains(e.target)) {
            suggestions.innerHTML = "";
        }
    });

});