//This uses a new endpoint called restrictedAreas, made with an existing method, to load the
//restricted areas onto the map.
async function loadRestrictedAreas(map) {
try{
        const res = await fetch("/api/v1/getRestrictedAreas");
        const areas = await res.json();

        areas.forEach(area => {
        if (!area.vertices) return;
//This converts the input lat lng objects into [lat, lng] that Leaflet can read
            const latLngs = area.vertices.map(v => [v.lat, v.lng]);

            const polygon = L.polygon(latLngs, {
                color: "red",
                weight: 2,
                fillOpacity: 0.15
            }).addTo(map);
            if (area.name){
            polygon.bindPopup(area.name);
            }
        });
        } catch (e){
        console.error("Failed to load restricted areas:", e);
        }
}

document.addEventListener("DOMContentLoaded", () => {
    const button = document.getElementById("calculate-button");
    const input = document.getElementById("dispatch-input");
    const result = document.getElementById("result");
    // This is roughly lng lat of where Edinburgh is, and 14 is the zoom level
    const map = L.map("map").setView([55.944, -3.200], 14);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom : 19,
        attribution: "&copy; OpenStreetMap contributors",
    }).addTo(map);

    loadRestrictedAreas(map);

// This line removes the old route when a new input is received to avoid confusion
    let currentLayer = null;

//Listens out for when the button is clicked
    button.addEventListener("click", async () => {

//This clears the previous text result, and attempts to parse the input as JSON.
    result.textContent = "";

    let body;
    try {
        body = JSON.parse(input.value);
    } catch (e) {
//If it is invalid JSON, it will show an error and exit.
        result.textContent = "Invalid JSON: Check if your brackets and quotes are correct. Full details:" + e.message;
        return;
    }

    try {
//This executes the POST request with new body and puts the raw output into 'text'
        const response = await fetch("/api/v1/calcDeliveryPathAsGeoJson", {
            method: "POST",
            headers: {"Content-Type" : "application/json"},
            body: JSON.stringify(body)
        });

        const text = await response.text();

        result.textContent = text;

//This part takes the raw output ('text') and interprets it as geoJSON, and returns an error if it cannot
        let geo;
        try {
            geo = JSON.parse(text);
        } catch (e) {
            result.textContent +=
            "\n\n(Could not parse response as JSON: " + e.message + ")";
            return;
        }

//Then puts the geoJSON into a format Leaflet can accept
        let geojsonFeature;
        if (geo.type === "LineString") {
            geojsonFeature = {
                type: "Feature",
                geometry: geo,
                properties: {},
            };
        } else {
            geojsonFeature = geo;
        }

//Removes any old routes
        if (currentLayer) {
            map.removeLayer(currentLayer);
        }
//Puts the geoJSON on the map and returns an error message if something goes wrong.
        currentLayer = L.geoJSON(geojsonFeature).addTo(map);
        const bounds = currentLayer.getBounds();
        if (bounds.isValid()){
            map.fitBounds(bounds, {padding: [20,20]});
        }
    } catch (e) {
        result.textContent = "Error: " + e.message;
    }
    });
});