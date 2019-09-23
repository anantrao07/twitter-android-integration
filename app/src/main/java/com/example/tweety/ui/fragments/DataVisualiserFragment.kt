package com.example.tweety.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest
import com.example.tweety.*
import com.example.tweety.networking.VolleyInstance
import com.example.tweety.networking.generateSignature
import com.example.tweety.networking.generateSignatureBaseStr
import com.example.tweety.networking.getBase64
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime


class DataVisualiserFragment : Fragment(), OnMapReadyCallback {

    private lateinit var userName: String
    private lateinit var authToken: String
    private lateinit var consumerSecretKey: String
    private lateinit var consumerApiKey: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        bundle.let {
            userName = it?.getString("userName").toString()
            authToken = it?.getString("authToken").toString()
        }
        consumerApiKey = resources.getString(R.string.com_twitter_sdk_android_CONSUMER_KEY)
        consumerSecretKey = resources.getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        location = Location(LocationManager.GPS_PROVIDER)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestPrimaryLocationPermission()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data_visualiser, container, false)
    }

    override fun onResume() {
        super.onResume()
       // getTweets()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {

            MY_PERMISSIONS_REQUEST_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                }
            }
        }

    }

    private fun requestPrimaryLocationPermission() {
        if (!isLocationRequestGranted()) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_FINE_LOCATION
            )
        } else {
            getTweets()
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        // Add a marker in Sydney, Australia, and move the camera.
        val northAmerica = LatLng(41.850033, -87.6500523)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(northAmerica))
        googleMap?.animateCamera(CameraUpdateFactory.zoomTo(2f))
    }

    private fun getTweets() {
        val queue = VolleyInstance.getInstance(this.requireContext())
        val lastLocation = requestLastKnownLocation()
        location.let {
            lastLocation.latitude = it.latitude
            lastLocation.longitude = it.longitude
        }
        val parameterString = "q=geocode=-22.912214,-43.230182,1km&lang=pt&result_type=recent"//&geocode=${location.latitude} ${location.longitude} 5 km&count=2"
        val url = SEARCH_TWEETS_WITH_GEO_TAGS + parameterString


        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            Listener { response ->
                Log.d("response received****", "Response: %s".format(response.toString()))
            },
            Response.ErrorListener { error ->
                Log.d("ERROR received****", "Response: %s".format(error.toString()))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return getHeaderHashMap()
            }
        }
        queue.addToRequestQueue(jsonObjectRequest)
    }

    private fun getHeaderHashMap(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        headers["oauth_consumer_key"] = consumerApiKey
        headers["oauth_nonce"] = getBase64(LocalDateTime.now().toString())
        headers["oauth_signature_method"] = "HMAC-SHA1"
        headers["oauth_timestamp"] = System.currentTimeMillis().toString()
        headers["oauth_token"] = "token"
        headers["oauth_version"] = "1.0"
        headers["oauth_signature"] = generateSignature(
            generateSignatureBaseStr(
                REQUEST_METHOD_GET,
                SEARCH_TWEETS_WITH_GEO_TAGS,
                headers
            ),
            consumerSecretKey, "tokenSecret"
        )

        return headers
    }

    private fun isLocationRequestGranted(): Boolean {
        return this.context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLastKnownLocation(): Location {
        if (isLocationRequestGranted()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    if (it != null) {
                        location = it
                    }
                }
        } else{
            location.latitude = 0.0
            location.longitude = 0.0
        }
        return location
    }

    companion object {
        @JvmStatic
        fun newInstance(): DataVisualiserFragment {
            return DataVisualiserFragment()
        }
    }
}
