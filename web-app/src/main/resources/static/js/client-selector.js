// =====================
// MAP INIT
// =====================

const map = L.map("map").setView([46.6, 2.5], 6);

L.tileLayer(
    "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
    {
        attribution: "&copy; OpenStreetMap"
    }
).addTo(map);


// =====================
// ICONS
// =====================

const clientIcon = L.icon({
    iconUrl:
        "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-blue.png",

    shadowUrl:
        "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",

    iconSize: [25, 41],
    iconAnchor: [12, 41]
});


const selectedIcon = L.icon({
    iconUrl:
        "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-green.png",

    shadowUrl:
        "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",

    iconSize: [25, 41],
    iconAnchor: [12, 41]
});


const searchIcon = L.icon({
    iconUrl:
        "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png",

    shadowUrl:
        "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",

    iconSize: [25, 41],
    iconAnchor: [12, 41]
});


// =====================
// STATE
// =====================

let searchMarker = null;
let selectedMarker = null;
let activeCircle = null;
let selectedClient = null;

const clients = window.clients || [];


// =====================
// DOM
// =====================

const searchInput =
    document.getElementById("searchClient");

const suggestions =
    document.getElementById("suggestions");

const selectedText =
    document.getElementById("selectedClient");

const selectedZone =
    document.getElementById("selectedZone");

const clientIdInput =
    document.getElementById("clientId");

const selectedCard =
    document.getElementById("selectedClientCard");

const deselectButton =
    document.getElementById("deselectClient");

const activeBadge =
    document.getElementById("activeBadge");

const submitButton =
    document.querySelector(".btn-primary");


// =====================
// INITIAL STATE
// =====================

submitButton.disabled = true;

activeBadge.style.display = "none";

deselectButton.style.display = "none";


// =====================
// DISTANCE
// =====================

function distance(lat1, lon1, lat2, lon2) {

    const R = 6371;

    const dLat =
        (lat2 - lat1) * Math.PI / 180;

    const dLon =
        (lon2 - lon1) * Math.PI / 180;

    const a =
        Math.sin(dLat / 2) ** 2 +
        Math.cos(lat1 * Math.PI / 180) *
        Math.cos(lat2 * Math.PI / 180) *
        Math.sin(dLon / 2) ** 2;

    return (
        2 *
        R *
        Math.atan2(
            Math.sqrt(a),
            Math.sqrt(1 - a)
        )
    );
}


// =====================
// DESELECT CLIENT
// =====================

function deselectClient() {

    selectedClient = null;


    // Supprimer le marker sélectionné

    if (selectedMarker) {

        map.removeLayer(selectedMarker);

        selectedMarker = null;
    }


    // Supprimer le périmètre

    if (activeCircle) {

        map.removeLayer(activeCircle);

        activeCircle = null;
    }


    // Vider l'ID client

    clientIdInput.value = "";


    // Réinitialiser les informations

    selectedText.innerText =
        "Aucun client sélectionné";

    selectedZone.innerHTML =
        "Sélectionnez un client sur la carte.";


    // Masquer le badge

    activeBadge.style.display =
        "none";


    // Masquer le bouton X extérieur

    deselectButton.style.display =
        "none";


    // Désactiver le bouton continuer

    submitButton.disabled = true;
}


// =====================
// X EXTERIEUR
// =====================

deselectButton.addEventListener(
    "click",
    function (event) {

        event.preventDefault();

        event.stopPropagation();

        deselectClient();
    }
);


// =====================
// DISPLAY CLIENTS
// =====================

clients.forEach(function (client) {

    if (
        client.latitude == null ||
        client.longitude == null
    ) {
        return;
    }


    const lat =
        parseFloat(client.latitude);

    const lng =
        parseFloat(client.longitude);


    if (
        Number.isNaN(lat) ||
        Number.isNaN(lng)
    ) {
        return;
    }


    const marker =
        L.circleMarker(
            [lat, lng],
            {
                radius: 6,

                color: "#2563eb",

                fillColor: "#2563eb",

                fillOpacity: 0.8
            }
        ).addTo(map);


    marker.bindPopup(`
        <div>
            <strong>${client.name}</strong>
            <br>
            Zone : ${client.perimeter ?? 0} km
        </div>
    `);


    marker.on(
        "click",
        function () {

            selectClient(client);
        }
    );
});


// =====================
// SELECT CLIENT
// =====================

function selectClient(client) {

    const lat =
        parseFloat(client.latitude);

    const lng =
        parseFloat(client.longitude);


    if (
        Number.isNaN(lat) ||
        Number.isNaN(lng)
    ) {
        return;
    }


    selectedClient = client;


    // =====================
    // INFORMATIONS
    // =====================

    selectedText.innerText =
        client.name;


    selectedZone.innerHTML = `

        <div class="zone-row">

            <i class="fa-solid fa-city"></i>

            <span>
                ${client.city ?? "Ville inconnue"}
            </span>

        </div>


        <div class="zone-row">

            <i class="fa-solid fa-location-dot"></i>

            <span>
                ${client.address ?? "Adresse inconnue"}
            </span>

        </div>


        <div class="zone-row">

            <i class="fa-solid fa-ruler"></i>

            <span>
                ${client.perimeter ?? 0} km
            </span>

        </div>
    `;


    // =====================
    // FORM
    // =====================

    clientIdInput.value =
        client.id;

    submitButton.disabled =
        false;


    // =====================
    // UI
    // =====================

    activeBadge.style.display =
        "inline-flex";

    deselectButton.style.display =
        "flex";


    // =====================
    // REMOVE OLD SELECTION
    // =====================

    if (selectedMarker) {

        map.removeLayer(selectedMarker);

        selectedMarker = null;
    }


    if (activeCircle) {

        map.removeLayer(activeCircle);

        activeCircle = null;
    }


    // =====================
    // SELECTED MARKER
    // =====================

    selectedMarker =
        L.marker(
            [lat, lng],
            {
                icon: selectedIcon
            }
        ).addTo(map);


    // =====================
    // CLIENT PERIMETER
    // =====================

    const perimeter =
        parseFloat(client.perimeter) || 0;


    activeCircle =
        L.circle(
            [lat, lng],
            {
                radius:
                    perimeter * 1000,

                color:
                    "#10b981",

                weight: 2,

                fillColor:
                    "#34d399",

                fillOpacity: 0.18
            }
        ).addTo(map);


    // =====================
    // POPUP
    // =====================

    selectedMarker.bindPopup(`

        <div class="client-map-popup">

            <button
                type="button"
                class="map-deselect-button"
                id="mapDeselectClient"
                aria-label="Désélectionner"
                title="Désélectionner">

                <i class="fa-solid fa-xmark"></i>

            </button>


            <div class="client-map-popup-label">
                CLIENT SÉLECTIONNÉ
            </div>


            <div class="client-map-popup-name">
                ${client.name}
            </div>


            <div class="client-map-popup-info">

                <i class="fa-solid fa-city"></i>

                ${client.city ?? "Ville inconnue"}

            </div>


            <div class="client-map-popup-info">

                <i class="fa-solid fa-location-dot"></i>

                ${client.address ?? "Adresse inconnue"}

            </div>


            <div class="client-map-popup-info">

                <i class="fa-solid fa-ruler"></i>

                ${perimeter} km

            </div>

        </div>

    `);


    // =====================
    // POPUP OPEN
    // =====================

    selectedMarker.on(
        "popupopen",
        function (event) {

            const popupElement =
                event.popup.getElement();


            if (!popupElement) {
                return;
            }


            const mapDeselectButton =
                popupElement.querySelector(
                    "#mapDeselectClient"
                );


            if (!mapDeselectButton) {
                return;
            }


            mapDeselectButton.onclick =
                function (event) {

                    event.preventDefault();

                    event.stopPropagation();


                    // Désélection complète

                    deselectClient();


                    // Fermer la popup

                    map.closePopup();
                };
        }
    );


    // =====================
    // OPEN POPUP
    // =====================

    selectedMarker.openPopup();


    // =====================
    // CENTER MAP
    // =====================

    map.flyTo(
        [lat, lng],
        12,
        {
            duration: 0.8
        }
    );
}


// =====================
// SEARCH OSM
// =====================

let timeout = null;

let controller = null;


searchInput.addEventListener(
    "input",
    function () {

        const query =
            this.value.trim();


        suggestions.innerHTML =
            "";


        if (query.length < 3) {
            return;
        }


        clearTimeout(timeout);


        timeout = setTimeout(
            function () {

                if (controller) {
                    controller.abort();
                }


                controller =
                    new AbortController();


                fetch(
                    `https://nominatim.openstreetmap.org/search?format=json&countrycodes=fr&limit=8&q=${encodeURIComponent(query)}`,
                    {
                        signal:
                            controller.signal
                    }
                )
                    .then(
                        function (response) {

                            return response.json();
                        }
                    )
                    .then(
                        function (data) {

                            suggestions.innerHTML =
                                "";


                            const unique =
                                new Map();


                            data.forEach(
                                function (item) {

                                    unique.set(
                                        item.place_id,
                                        item
                                    );
                                }
                            );


                            const results =
                                [
                                    ...unique.values()
                                ].slice(0, 5);


                            if (
                                results.length === 0
                            ) {

                                suggestions.innerHTML = `
                                    <div class="suggestion-item">
                                        Aucun résultat
                                    </div>
                                `;

                                return;
                            }


                            results.forEach(
                                function (item) {

                                    const div =
                                        document.createElement(
                                            "div"
                                        );


                                    div.className =
                                        "suggestion-item";


                                    div.innerHTML = `

                                        <div class="suggestion-title">

                                            <i class="fa-solid fa-location-dot"></i>

                                            ${item.display_name}

                                        </div>

                                    `;


                                    div.onclick =
                                        function () {

                                            const lat =
                                                parseFloat(
                                                    item.lat
                                                );

                                            const lon =
                                                parseFloat(
                                                    item.lon
                                                );


                                            // =====================
                                            // SEARCH MARKER
                                            // =====================

                                            if (
                                                searchMarker
                                            ) {

                                                map.removeLayer(
                                                    searchMarker
                                                );
                                            }


                                            searchMarker =
                                                L.marker(
                                                    [lat, lon],
                                                    {
                                                        icon:
                                                            searchIcon
                                                    }
                                                ).addTo(map);


                                            // =====================
                                            // PERIMETER DE RECHERCHE
                                            // =====================

                                            if (
                                                window.searchCircle
                                            ) {

                                                map.removeLayer(
                                                    window.searchCircle
                                                );
                                            }


                                            window.searchCircle =
                                                L.circle(
                                                    [lat, lon],
                                                    {
                                                        radius: 10000,

                                                        color:
                                                            "#ef4444",

                                                        weight: 2,

                                                        fillColor:
                                                            "#ef4444",

                                                        fillOpacity:
                                                            0.08
                                                    }
                                                ).addTo(map);


                                            // =====================
                                            // MAP
                                            // =====================

                                            map.flyTo(
                                                [lat, lon],
                                                13,
                                                {
                                                    duration:
                                                        0.8
                                                }
                                            );


                                            searchInput.value =
                                                item.display_name;


                                            suggestions.innerHTML =
                                                "";


                                            /*
                                             * IMPORTANT :
                                             *
                                             * On NE sélectionne plus
                                             * automatiquement le client
                                             * le plus proche.
                                             *
                                             * L'utilisateur doit
                                             * cliquer lui-même sur
                                             * un client.
                                             */
                                        };


                                    suggestions.appendChild(
                                        div
                                    );
                                }
                            );
                        }
                    )
                    .catch(
                        function (error) {

                            if (
                                error.name !==
                                "AbortError"
                            ) {

                                console.error(
                                    "Erreur recherche OSM :",
                                    error
                                );
                            }
                        }
                    );

            },
            300
        );
    }
);


// =====================
// CLOSE SUGGESTIONS
// =====================

document.addEventListener(
    "click",
    function (event) {

        if (
            !searchInput.contains(
                event.target
            ) &&
            !suggestions.contains(
                event.target
            )
        ) {

            suggestions.innerHTML =
                "";
        }
    }
);


/* =====================================================
   MODAL CLIENT SÉLECTIONNÉ
===================================================== */

document.addEventListener("DOMContentLoaded", function () {

    const modal = document.getElementById("clientSelectedModal");

    if (!modal) {
        return;
    }


    /*
     * La classe "show" est déjà ajoutée par Thymeleaf
     * lorsque le serveur confirme que le client a été sauvegardé.
     *
     * On ajoute simplement la gestion de fermeture avec ESC.
     */

    document.addEventListener("keydown", function (event) {

        if (event.key === "Escape" && modal.classList.contains("show")) {

            modal.classList.remove("show");

        }

    });


    /*
     * Permet de fermer la modal en cliquant
     * sur l'arrière-plan.
     */

    modal.addEventListener("click", function (event) {

        if (event.target === modal) {

            modal.classList.remove("show");

        }

    });

});