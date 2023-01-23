package com.oyelekeokiki.dentreality_task

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.oyelekeokiki.dentreality_task.data.Country
import com.oyelekeokiki.dentreality_task.databinding.ActivityMapsBinding
import com.oyelekeokiki.dentreality_task.utils.CountrySetup.Companion.fetchCountries


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var countries: List<Country>

    private var homeCountry: Country? = null
    private var selectedMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        countries = fetchCountries()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        with(mMap) {
            setOnMarkerClickListener(this@MapsActivity);
            uiSettings.isZoomControlsEnabled = true
            countries.forEachIndexed { index, country ->
                val marker = addMarker(MarkerOptions().position(country.latLng).title(country.name))
                marker?.tag = index
                marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            }
            setOnInfoWindowClickListener { clickedMarker ->
                homeCountry = countries[clickedMarker.tag as Int]
                selectedMarker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                clickedMarker.showInfoWindow()
                selectedMarker = clickedMarker
            };

            val middleCountry = countries[3].latLng // Brute-forced for time-constraints and good UX
            mMap.animateCamera(CameraUpdateFactory.newLatLng(middleCountry))
            // Setting an info window adapter allows us to change the both the contents and
            // look of the info window.
            setInfoWindowAdapter(DRInfoWindowAdapter())
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.showInfoWindow()
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
        return true
    }

    /** Demonstrates customizing the info window and/or its contents.  */
    internal inner class DRInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        private val contents: View = layoutInflater.inflate(R.layout.marker_info, null)

        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

        override fun getInfoContents(marker: Marker): View? {
            render(marker, contents)
            return contents
        }

        private fun configureHomeCountryUI(
            nameText: TextView,
            distanceText: TextView,
            homeImageButton: AppCompatImageButton,
            marker: Marker,
            country: Country
        ) {
            var homeText = ""
            homeCountry?.let { unwrappedCountry ->
                if (homeCountry == country) {
                    homeText = " (Home)"
                    distanceText.visibility = View.GONE
                    homeImageButton.setColorFilter(Color.GREEN)
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    return@let
                }

                homeText = ""
                homeImageButton.setColorFilter(Color.GRAY)
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                val results = floatArrayOf(0.0f, 0.0f, 0.0f)
                // https://developer.android.com/reference/android/location/Location.html#distanceBetween(double,%20double,%20double,%20double,%20float[])
                Location.distanceBetween(
                    country.latLng.latitude, country.latLng.longitude,
                    unwrappedCountry.latLng.latitude, unwrappedCountry.latLng.longitude,
                    results
                )
                if (results.isNotEmpty()) {
                    val distanceInKiloMetersValue = String.format("%.2f", results[0] / 1000)
                    val distanceTextValue = "Distance: ${distanceInKiloMetersValue}km"
                    distanceText.text = distanceTextValue
                    distanceText.visibility = View.VISIBLE
                } else {
                    distanceText.visibility = View.GONE
                }
            }
            val nameTextValue = "${country.name}, ${country.countryCode}${homeText}"
            nameText.text = nameTextValue
        }

        private fun render(marker: Marker, view: View) {
            val country = countries[marker.tag as Int]
            val nameText = view.findViewById<TextView>(R.id.name_text)
            val homeImageButton = view.findViewById<AppCompatImageButton>(R.id.home_button)
            val capitalText = view.findViewById<TextView>(R.id.capital_text)
            val distanceText = view.findViewById<TextView>(R.id.distance_text)

            val capitalTextValue = "Capital: ${country.capital}"
            capitalText.text = capitalTextValue
            configureHomeCountryUI(nameText, distanceText, homeImageButton, marker, country)

        }
    }

}