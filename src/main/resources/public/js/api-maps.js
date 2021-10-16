$( document ).ready(function() {
	
	jQuery(function($)
	{
		$('.slider').sss({showNav : true});
	});
});

function geocodeAddress(geocoder, resultsMap)
{
  var address = document.getElementById('address').innerHTML;
  geocoder.geocode({'address': address}, function(results, status) {
    if (status === google.maps.GeocoderStatus.OK) {
      resultsMap.setCenter(results[0].geometry.location);
      var marker = new google.maps.Marker({
        map: resultsMap,
        position: results[0].geometry.location
      });
    }
  });
}

var initialized = false;
		
function displayMap(){	
	if(!initialized)
		initialize();
	initialized = true;
}

function initialize() {
	var myOptions = {
	    zoom: 14,
	    center: new google.maps.LatLng(0.0, 0.0),
	    mapTypeId: google.maps.MapTypeId.ROADMAP
	  }
    map = new google.maps.Map(document.getElementById("map"),
                                myOptions);
	var geocoder = new google.maps.Geocoder();
	geocodeAddress(geocoder, map);
 }
  
