document.querySelector('.location-btn').addEventListener('click', function() {

    if (navigator.geolocation) {

        navigator.geolocation.getCurrentPosition(

            function(position) {

                const lat = position.coords.latitude;
                const lng = position.coords.longitude;

                document.querySelector('input[name="latitude"]').value = lat;
                document.querySelector('input[name="longitude"]').value = lng;

                document.querySelector('input[name="location"]').value =
                    "Position GPS récupérée";

                console.log("Latitude :", lat);
                console.log("Longitude :", lng);
            },

            function(error) {
                alert("Impossible de récupérer votre position : " + error.message);
            }

        );

    } else {
        alert("La géolocalisation n'est pas supportée par votre navigateur.");
    }

});