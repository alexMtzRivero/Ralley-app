var mymap = L.map('map').setView([45.188096,5.718452], 13);

var quizzesGroup = L.layerGroup().addTo(mymap);
var opponentsGroup = L.layerGroup().addTo(mymap);

var myMarker;
var Error;

var todoIcon = L.icon({
        iconUrl: 'images/quiz_todo.png',
        iconSize: [45, 45],
    });

var doneIcon = L.icon({
        iconUrl: 'images/quiz_done.png',
        iconSize: [45, 45],
    });

var teamIcon = L.icon({
        iconUrl: 'images/team.png',
        iconSize: [45, 45],
    });

var opponentIcon = L.icon({
        iconUrl: 'images/opponent.png',
        iconSize: [30, 30],
    });

L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw',
{
    maxZoom: 20,
    minZoom: 13,
    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, ' +
    '<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
    'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
    id: 'mapbox.streets'
}).addTo(mymap);


mymap.locate({setView: false, watch: true, timeout: 100000})
        .on('locationfound', function(e){
            if(myMarker != null){
                mymap.removeLayer(myMarker)
            }
            myMarker = new L.Marker(e.latlng, {icon: teamIcon}).bindPopup("vous");
            mymap.addLayer(myMarker);
        })
       .on('locationerror', function(e){
            console.log("locationError : "+e.message);
        });

function locateOnce(){
    mymap.flyTo(myMarker.getLatLng(), 20);
}
