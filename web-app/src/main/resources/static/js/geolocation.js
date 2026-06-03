
document.querySelector('.location-btn').addEventListener('click', function() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function(position) {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;

                // Exemple : remplir le champ location avec "lat, lng"
                document.querySelector('input[name="location"]').value = lat + ', ' + lng;

                console.log("Latitude:", lat, "Longitude:", lng);
            },
            function(error) {
                alert("Impossible de récupérer votre position : " + error.message);
            }
        );
    } else {
        alert("La géolocalisation n'est pas supportée par votre navigateur.");
    }
});
