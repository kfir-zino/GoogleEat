package com.example.googleeatkot

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import android.location.Location
import android.os.Looper
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.annotation.IntegerRes
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import com.example.googleeatkot.Common.Common
import com.example.googleeatkot.Model.myPlaces
import com.example.googleeatkot.remote.IGoogleAPIservice
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
// import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.compat.ui.PlacePicker
import com.google.android.libraries.places.compat.Place
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.Exception



class MapFragment : Fragment(), OnMapReadyCallback {

    var mMap: GoogleMap? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var mMarker: Marker? = null
    private var lastLocation: Location? = null
    private var locationUpdateState = false
    var url: String = ""
    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var DBNewPlace : DatabaseReference

    private var latitude : Double = 0.toDouble()
    private var longitude : Double = 0.toDouble()




    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS =  2
//        private const val PLACE_PICKER_REQUEST = 3
        @SuppressLint("StaticFieldLeak")
        var rootView: View? = null
        var mapFragment: SupportMapFragment? = null
        fun newInstance() = MapFragment()
        var addToMyPlaces : Button? = null
    }

    lateinit var mService: IGoogleAPIservice
    internal lateinit var currentPlaces: myPlaces
  //  lateinit var currPlaceViewPlace: ViewPlace


    private fun placeMarkerOnMap(location: LatLng) {
        // 1
        val markerOptions = MarkerOptions().position(location)
        // 2
        mMap!!.addMarker(markerOptions)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.map_fragment, container, false)


//        currPlaceViewPlace = childFragmentManager.findFragmentById(R.id.view_place) as ViewPlace
//        currPlaceViewPlace.setMenuVisibility(false)

        mapFragment = childFragmentManager.findFragmentById(R.id.frg) as? SupportMapFragment?  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment


        mapFragment?.getMapAsync(this)

        mService = Common.googleApiService
//        mMap!!.setInfoWindowAdapter(ViewPlace(this.requireContext()))


        locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)

            if (mMarker != null) {
                mMarker!!.remove()
            }
            lastLocation = p0!!.locations[p0!!.locations.size -1]
            latitude = lastLocation!!.latitude
            longitude = lastLocation!!.longitude
            val latLang = LatLng(latitude,longitude)
            var mMarkerOptions = MarkerOptions()
                .position(latLang)
                .title("your position")
            //placeMarkerOnMap(LatLng(lastLocation!!.latitude, lastLocation!!.longitude))
            mMarker = mMap!!.addMarker(mMarkerOptions)
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLang))
            mMap!!.animateCamera(CameraUpdateFactory.zoomTo(14f))
            try {
                this@MapFragment.nearByPlace()
            } catch (e: Exception) {
                Toast.makeText(this@MapFragment.requireContext(), "check2", Toast.LENGTH_SHORT).show()
            }
            if (mMap != null) {
                mMap!!.setOnMarkerClickListener { marker ->
                    addToMyPlaces = rootView!!.findViewById(R.id.addPlace)
                    if (Common.placesResults != null && Common.placesResults!!.isNotEmpty()) {
                        Common.currentResult = Common.placesResults!![marker.snippet]
                    }
//                    Common.placesResults = currentPlaces!!.results!!
                    mMap!!.setInfoWindowAdapter(ViewPlace(this@MapFragment.requireContext()))
                    addToMyPlaces!!.visibility = View.VISIBLE
                    marker.showInfoWindow()
                    true
                }

                mMap!!.setOnInfoWindowCloseListener {
                    addToMyPlaces!!.visibility = View.INVISIBLE
                }
            }
            if (addToMyPlaces != null) {

                addToMyPlaces!!.setOnClickListener {
                    val currUserID = mAuth.currentUser!!.uid
                    DBNewPlace = FirebaseDatabase.getInstance().getReference("Users").child(currUserID).child("MyPlaces")
                    val placeId = Common.currentResult!!.place_id
                    val key = DBNewPlace.push().key
                    val name: String = Common.currentResult!!.name!!
                    //adding to DB
                    DBNewPlace.child(key!!).setValue(FoodPlace(placeId, key, name))
                    addToMyPlaces!!.visibility = View.INVISIBLE
                }
            }

        }
    }

//        val fab = rootView?.findViewById<FloatingActionButton>(R.id.fab)
//        fab?.setOnClickListener {
//            loadPlacePicker()
//        }
        createLocationRequest()
        if (mMap != null) {
            this.nearByPlace()
        }
        return rootView
    }

    private fun nearByPlace() {
        // mMap!!.clear()
        val radius = 10000
        val typePlace = "restaurant|bar|cafe" //" | bar | cafe"
        url = getUrl(latitude, longitude, typePlace, radius) // TODO - switch parameters as pleased
        mService.getNearByPlaces(url)
            .enqueue(object: Callback<myPlaces>{
                override fun onResponse(call: Call<myPlaces>, response: Response<myPlaces>?) {
                    currentPlaces = response!!.body()!!
                    if (response!!.isSuccessful) {
                        (activity as MainActivity).scrollPlaces()
                        //Toast.makeText(this@MapFragment.requireContext(), "check1", Toast.LENGTH_SHORT).show()
                        for ( i in 0 until response.body()!!.results!!.size) {
                            val googlePlace = response.body()!!.results!![i]
                            val lat = googlePlace.geometry!!.location!!.lat
                            val lng = googlePlace.geometry!!.location!!.lng
                            val placeName = googlePlace.name
                            val place_id = googlePlace.id
                            Common.placesResults!![place_id.toString()] = googlePlace
                            val latLng = LatLng(lat, lng)
                            val markerOptions: MarkerOptions = MarkerOptions()
                                .position(latLng)
                                .title(placeName)
                                .snippet(place_id)
                            when {
                                "restaurant" in googlePlace.types!! -> markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)) // TODO - cry to victor bitmap is not recognized
                                "bar" in googlePlace.types!! -> markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)) // TODO - cry to victor bitmap is not recognized
                                "cafe" in googlePlace.types!! -> markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)) // TODO - cry to victor bitmap is not recognized

                            }
                            try {
//                                markerOptions.snippet(i.toString())
                                mMap!!.addMarker(markerOptions)
                            } catch (t : Exception) {
                                Toast.makeText(this@MapFragment.requireContext(), ""+t.message, Toast.LENGTH_SHORT).show()
                            }

                        }
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
                        mMap!!.animateCamera((CameraUpdateFactory.zoomTo(14f)))

                    }
                }

                override fun onFailure(call: Call<myPlaces>, t: Throwable) {
                    Toast.makeText(this@MapFragment.requireContext(), ""+t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }





    private fun getUrl( lati: Double, longi: Double, typePlace: String, radius: Int): String
    {
        val googlePlaceUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
        googlePlaceUrl.append("?location=$lati,$longi&radius=$radius&type=$typePlace&key=AIzaSyASmgAWrMWkLhB26W9iZYjX-Vvtq0xJ0X4")
        return googlePlaceUrl.toString()
    }





    // 1
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }


//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                val place = PlacePicker.getPlace(this.requireContext(), data)
//                var addressText = place.name.toString()
//                addressText += "\n" + place.address.toString()
//
//                placeMarkerOnMap(place.latLng)
//            }
//        }

    }

    // 2
    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        locationUpdateState = false
    }

    // 3
    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
//            if(mMap != null) {
//                this.nearByPlace()
//            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (mMap != null) {
            mMap!!.setOnMarkerClickListener { marker ->
                addToMyPlaces = rootView!!.findViewById(R.id.addPlace)
                if (Common.placesResults != null && Common.placesResults!!.isNotEmpty()) {
                    Common.currentResult = Common.placesResults!![marker.snippet]
                }
                mMap!!.setInfoWindowAdapter(ViewPlace(this.requireContext()))
                addToMyPlaces!!.visibility = View.VISIBLE
                marker.showInfoWindow()
                true
            }

            mMap!!.setOnInfoWindowCloseListener {
                addToMyPlaces!!.visibility = View.INVISIBLE
            }
        }
        if (addToMyPlaces != null) {

            addToMyPlaces!!.setOnClickListener {
                val currUserID = mAuth.currentUser!!.uid
                DBNewPlace = FirebaseDatabase.getInstance().getReference("Users").child(currUserID).child("MyPlaces")
                val placeId = Common.currentResult!!.place_id
                val key = DBNewPlace.push().key
                val name: String = Common.currentResult!!.name!!
                //adding to DB
                DBNewPlace.child(key!!).setValue(FoodPlace(placeId, key, name))
                addToMyPlaces!!.visibility = View.INVISIBLE
            }
        }
//        if(mMap != null) {
//            this.nearByPlace()
//        }
    }




    private fun startLocationUpdates() {
        //1
        if (ActivityCompat.checkSelfPermission(this.requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        //2
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper() /* Looper */)
    }

    private fun createLocationRequest() {
        // 1
        locationRequest = LocationRequest()
        // 2
        locationRequest.interval = 10000
        // 3
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        // 4
        val client = LocationServices.getSettingsClient(this.requireActivity())
        val task = client.checkLocationSettings(builder.build())

        // 5
        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            // 6
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@MapFragment.requireActivity(),
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }





    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap =
            Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    override fun onMapReady(gmap: GoogleMap?) {
        mMap = gmap!!


        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.uiSettings.isZoomGesturesEnabled = true
        mMap!!.uiSettings.isScrollGesturesEnabled = true
        mMap!!.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true


        mMap!!.clear() //clear old markers

        var googlePlex: CameraPosition = if (lastLocation != null) {
            CameraPosition.builder()
                .target(LatLng(lastLocation!!.longitude,lastLocation!!.latitude))
                .zoom(10f)
                .bearing(0f)
                .tilt(45f)
                .build()
        } else {
            CameraPosition.builder()
                .target(LatLng(37.4219999, -122.0862462))
                .zoom(10f)
                .bearing(0f)
                .tilt(45f)
                .build()
        }
        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null)


        try {
            mMap!!.setOnMarkerClickListener { marker ->
                addToMyPlaces = rootView!!.findViewById(R.id.addPlace)
                if (Common.placesResults != null && Common.placesResults!!.isNotEmpty()) {
                    Common.currentResult = Common.placesResults!![marker.snippet]
                }
//                Common.placesResults = currentPlaces!!.results!!
                mMap!!.setInfoWindowAdapter(ViewPlace(this.requireContext()))
                addToMyPlaces!!.visibility = View.VISIBLE
                marker.showInfoWindow()
                true
            }

            mMap!!.setOnInfoWindowCloseListener {
                addToMyPlaces!!.visibility = View.INVISIBLE
            }

            if (addToMyPlaces != null) {

                addToMyPlaces!!.setOnClickListener {
                    val currUserID = mAuth.currentUser!!.uid
                    DBNewPlace = FirebaseDatabase.getInstance().getReference("Users").child(currUserID).child("MyPlaces")
                    val placeId = Common.currentResult!!.place_id
                    val key = DBNewPlace.push().key
                    val name: String = Common.currentResult!!.name!!
                    //adding to DB
                    DBNewPlace.child(key!!).setValue(FoodPlace(placeId, key, name))
                    addToMyPlaces!!.visibility = View.INVISIBLE
                }
            }



            (activity as MainActivity).scrollPlaces()
        } catch (t : Exception) {
            Toast.makeText(this@MapFragment.requireContext(), "fail to Click"+t.message, Toast.LENGTH_SHORT).show()
        }
        Toast.makeText(this@MapFragment.requireContext(), "WTF", Toast.LENGTH_SHORT).show()

    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}
