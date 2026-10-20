document.addEventListener("DOMContentLoaded", function () {

    /* =========================================================
       ELEMENTS
    ========================================================= */

    const searchInput =
        document.getElementById("searchInput");

    const suggestionsBox =
        document.getElementById("suggestions");

    const latInput =
        document.getElementById("latitude");

    const lngInput =
        document.getElementById("longitude");

    const locationInput =
        document.getElementById("location");

    const addressBox =
        document.getElementById("address");

    const locateBtn =
        document.getElementById("locateBtn");

    const nextButton =
        document.getElementById("nextButton");


    /* =========================================================
       ACTIVE CLIENT
    ========================================================= */

    const activeClient =
        window.activeClient || null;


    /* =========================================================
       MAP INITIALIZATION
    ========================================================= */

    /*
     * Position par défaut si aucun client n'est disponible.
     */

    let defaultLatitude = 45.603354;
    let defaultLongitude = 1.688334;
    let defaultZoom = 8;


    /*
     * Si un client actif existe,
     * on utilise ses coordonnées.
     */

    if (
        activeClient &&
        activeClient.latitude != null &&
        activeClient.longitude != null
    ) {

        defaultLatitude =
            Number(activeClient.latitude);

        defaultLongitude =
            Number(activeClient.longitude);

        if (activeClient.zoom != null) {

            defaultZoom =
                Number(activeClient.zoom);

        } else {

            defaultZoom = 13;

        }
    }


    const map =
        L.map("map").setView(
            [
                defaultLatitude,
                defaultLongitude
            ],
            defaultZoom
        );


    /* =========================================================
       OPENSTREETMAP
    ========================================================= */

    L.tileLayer(
        "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        {
            attribution:
                "&copy; OpenStreetMap contributors"
        }
    ).addTo(map);


    /* =========================================================
       VARIABLES
    ========================================================= */

    let marker = null;

    let clientCircle = null;

    let searchTimeout = null;


    /* =========================================================
       CLIENT ZONE
    ========================================================= */

    if (
        activeClient &&
        activeClient.latitude != null &&
        activeClient.longitude != null
    ) {

        const clientLat =
            Number(activeClient.latitude);

        const clientLng =
            Number(activeClient.longitude);


        /*
         * Si un périmètre existe,
         * on l'affiche autour du client.
         */

        if (
            activeClient.perimeter != null &&
            Number(activeClient.perimeter) > 0
        ) {

            clientCircle =
                L.circle(
                    [
                        clientLat,
                        clientLng
                    ],
                    {
                        radius:
                            Number(activeClient.perimeter) * 1000,

                        color: "#1d4ed8",

                        weight: 2,

                        opacity: 0.7,

                        fillColor: "#2563eb",

                        fillOpacity: 0.08
                    }
                )
                .addTo(map);

        }


        /*
         * Marqueur du client.
         */

        L.marker(
            [
                clientLat,
                clientLng
            ]
        )
        .addTo(map)
        .bindPopup(
            `
                <strong>
                    ${escapeHtml(
                        activeClient.name || "Client"
                    )}
                </strong>
                <br>
                ${escapeHtml(
                    activeClient.city || ""
                )}
            `
        );


        /*
         * On affiche le popup uniquement
         * si le client existe.
         */

        setTimeout(function () {

            const clientMarker =
                findLastMarker();

            if (clientMarker) {
                clientMarker.openPopup();
            }

        }, 300);

    }


    /* =========================================================
       FIND LAST MARKER
    ========================================================= */

    function findLastMarker() {

        let lastMarker = null;

        map.eachLayer(function (layer) {

            if (layer instanceof L.Marker) {
                lastMarker = layer;
            }

        });

        return lastMarker;
    }


    /* =========================================================
       REVERSE GEOCODING
    ========================================================= */

    function reverseGeocode(lat, lng) {

        addressBox.textContent =
            "Recherche de l'adresse...";


        fetch(
            `https://nominatim.openstreetmap.org/reverse` +
            `?format=jsonv2` +
            `&lat=${encodeURIComponent(lat)}` +
            `&lon=${encodeURIComponent(lng)}`
        )
        .then(function (response) {

            if (!response.ok) {
                throw new Error(
                    "Erreur reverse geocoding"
                );
            }

            return response.json();

        })
        .then(function (data) {

            const address =
                data.display_name ||
                "Adresse inconnue";


            addressBox.textContent =
                address;

            locationInput.value =
                address;

        })
        .catch(function () {

            addressBox.textContent =
                "Impossible de récupérer l'adresse.";

            locationInput.value =
                "Position sélectionnée";

        });
    }


    /* =========================================================
       SET MARKER
    ========================================================= */

    function setMarker(lat, lng) {

        lat = Number(lat);
        lng = Number(lng);


        if (
            Number.isNaN(lat) ||
            Number.isNaN(lng)
        ) {
            return;
        }


        /*
         * Supprime l'ancien marqueur.
         */

        if (marker) {

            map.removeLayer(marker);

        }


        /*
         * Crée le nouveau marqueur.
         */

        marker =
            L.marker(
                [
                    lat,
                    lng
                ],
                {
                    draggable: true
                }
            )
            .addTo(map);


        /*
         * Centre la carte sur
         * la position sélectionnée.
         */

        map.setView(
            [
                lat,
                lng
            ],
            16
        );


        /*
         * Stocke les coordonnées
         * dans les champs cachés.
         */

        latInput.value =
            lat;

        lngInput.value =
            lng;


        /*
         * Active le bouton Continuer.
         */

        nextButton.disabled =
            false;


        /*
         * Récupère l'adresse.
         */

        reverseGeocode(
            lat,
            lng
        );


        /*
         * Lorsque l'utilisateur
         * déplace le marqueur.
         */

        marker.on(
            "dragend",
            function () {

                const position =
                    marker.getLatLng();

                setMarker(
                    position.lat,
                    position.lng
                );

            }
        );

    }


    /* =========================================================
       MAP CLICK
    ========================================================= */

    map.on(
        "click",
        function (event) {

            setMarker(
                event.latlng.lat,
                event.latlng.lng
            );

        }
    );


    /* =========================================================
       SEARCH AUTOCOMPLETE
    ========================================================= */

    searchInput.addEventListener(
        "input",
        function () {

            clearTimeout(
                searchTimeout
            );


            const query =
                this.value.trim();


            if (query.length < 3) {

                suggestionsBox.innerHTML =
                    "";

                return;
            }


            searchTimeout =
                setTimeout(
                    function () {

                        fetch(
                            `https://nominatim.openstreetmap.org/search` +
                            `?format=json` +
                            `&q=${encodeURIComponent(query)}` +
                            `&limit=5` +
                            `&addressdetails=1`
                        )
                        .then(function (response) {

                            if (!response.ok) {
                                throw new Error(
                                    "Erreur recherche"
                                );
                            }

                            return response.json();

                        })
                        .then(function (data) {

                            suggestionsBox.innerHTML =
                                "";


                            if (
                                !data ||
                                data.length === 0
                            ) {

                                const empty =
                                    document.createElement(
                                        "div"
                                    );

                                empty.className =
                                    "suggestion-item";

                                empty.textContent =
                                    "Aucun résultat";

                                suggestionsBox.appendChild(
                                    empty
                                );

                                return;
                            }


                            data
                                .slice(0, 5)
                                .forEach(
                                    function (item) {

                                        const div =
                                            document.createElement(
                                                "div"
                                            );

                                        div.className =
                                            "suggestion-item";

                                        div.textContent =
                                            item.display_name;


                                        div.addEventListener(
                                            "click",
                                            function () {

                                                setMarker(
                                                    item.lat,
                                                    item.lon
                                                );


                                                searchInput.value =
                                                    item.display_name;


                                                suggestionsBox.innerHTML =
                                                    "";

                                            }
                                        );


                                        suggestionsBox.appendChild(
                                            div
                                        );

                                    }
                                );

                        })
                        .catch(function () {

                            suggestionsBox.innerHTML =
                                "";

                        });

                    },
                    300
                );

        }
    );


    /* =========================================================
       GPS
    ========================================================= */

    locateBtn.addEventListener(
        "click",
        function () {

            if (
                !navigator.geolocation
            ) {

                alert(
                    "La géolocalisation n'est pas supportée par votre navigateur."
                );

                return;
            }


            locateBtn.disabled =
                true;


            locateBtn.innerHTML =
                `
                    <i class="fa-solid fa-spinner fa-spin"></i>
                    Localisation...
                `;


            navigator.geolocation.getCurrentPosition(

                function (position) {

                    setMarker(
                        position.coords.latitude,
                        position.coords.longitude
                    );


                    locateBtn.disabled =
                        false;


                    locateBtn.innerHTML =
                        `
                            <i class="fa-solid fa-location-crosshairs"></i>
                            Me localiser
                        `;

                },

                function () {

                    alert(
                        "Impossible d'obtenir votre position."
                    );


                    locateBtn.disabled =
                        false;


                    locateBtn.innerHTML =
                        `
                            <i class="fa-solid fa-location-crosshairs"></i>
                            Me localiser
                        `;

                },

                {
                    enableHighAccuracy: true,

                    timeout: 10000,

                    maximumAge: 0
                }
            );

        }
    );


    /* =========================================================
       CLOSE SUGGESTIONS WHEN CLICKING OUTSIDE
    ========================================================= */

    document.addEventListener(
        "click",
        function (event) {

            if (
                !searchInput.contains(event.target) &&
                !suggestionsBox.contains(event.target)
            ) {

                suggestionsBox.innerHTML =
                    "";

            }

        }
    );


    /* =========================================================
       ESCAPE HTML
    ========================================================= */

    function escapeHtml(value) {

        if (value == null) {
            return "";
        }

        return String(value)
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }


    /* =========================================================
       MAP RESIZE
    ========================================================= */

    setTimeout(
        function () {

            map.invalidateSize();

        },
        200
    );

});