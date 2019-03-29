var mymap = L.map('map').setView([48.864716, 2.349014], 13);
mymap.locate({setView: true, watch: true}) /* This will return map so you can do chaining */
        .on('locationfound', function(e){
            if(myMarker != null){
                mymap.removeLayer(myMarker)
            }
            myMarker = new L.Marker(e.latlng).bindPopup("vous");
            mymap.addLayer(myMarker).openPopup();
        })
       .on('locationerror', function(e){
            console.log("locationError : "+e);
            alert(e.message);
            Error = e;
        });