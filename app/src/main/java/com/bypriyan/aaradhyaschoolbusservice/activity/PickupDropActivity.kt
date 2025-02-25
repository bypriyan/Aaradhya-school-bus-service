package com.bypriyan.aaradhyaschoolbusservice.activity

import android.Manifest
import kotlinx.coroutines.launch
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityPickupDropBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class PickupDropActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityPickupDropBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var pickupMarker: Marker? = null
    private var pickupLocation: LatLng? = null
    private var dropLocation: LatLng? = null
    private var onlyDropLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickupDropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment =
            supportFragmentManager.findFragmentById(com.bypriyan.aaradhyaschoolbusservice.R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnCurrentLocation.setOnClickListener { getCurrentLocation() }
        binding.btnClearPickup.setOnClickListener { clearPickupLocation() }
        binding.btnClearDrop.setOnClickListener { clearDropLocation() }
        binding.btnClearOnlyDrop.setOnClickListener { clearOnlyDropLocation() }

        binding.etPickup.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val locationName = binding.etPickup.text.toString()
                if (locationName.isNotEmpty()) searchLocation(locationName, true)
                true
            } else false
        }

        binding.etDrop.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val locationName = binding.etDrop.text.toString()
                if (locationName.isNotEmpty()) searchLocation(locationName, false)
                true
            } else false
        }

        binding.etOnlyDrop.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val locationName = binding.etOnlyDrop.text.toString()
                if (locationName.isNotEmpty()) {
                    // Disable pickup and drop fields when only drop is selected
                    binding.etPickup.isEnabled = false
                    binding.etDrop.isEnabled = false
                    searchLocation(locationName, false, true)
                }
                true
            } else false
        }

        binding.etOnlyDrop.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Disable pickup and drop fields when only drop is focused
                binding.etPickup.isEnabled = false
                binding.etDrop.isEnabled = false
            } else {
                // Re-enable pickup and drop fields when only drop loses focus
                if (binding.etOnlyDrop.text.isNullOrEmpty()) {
                    binding.etPickup.isEnabled = true
                    binding.etDrop.isEnabled = true
                }
            }
        }

        binding.btnConfirm.setOnClickListener {
            val pickupAddress = binding.etPickup.text.toString()
            val dropAddress = binding.etDrop.text.toString()
            val onlyDropAddress = binding.etOnlyDrop.text.toString()

            if (pickupAddress.isEmpty() && dropAddress.isEmpty() && onlyDropAddress.isEmpty()) {
                Toast.makeText(this, "Please select at least one location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Handle confirmation logic
            if (onlyDropAddress.isNotEmpty()) {
                Toast.makeText(this, "Only Drop Location Selected", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Pickup and/or Drop Locations Selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Move camera to user's current location
        getCurrentLocation()

        // Detect map movement and update drop location dynamically
        googleMap.setOnCameraIdleListener {
            val centerLatLng = googleMap.cameraPosition.target
            if (binding.etOnlyDrop.hasFocus()) {
                onlyDropLocation = centerLatLng
                val address = getAddressFromLatLng(centerLatLng)
                binding.etOnlyDrop.setText(address)
                binding.btnClearOnlyDrop.visibility = View.VISIBLE
            } else {
                dropLocation = centerLatLng
                val address = getAddressFromLatLng(centerLatLng)
                binding.etDrop.setText(address)
                binding.btnClearDrop.visibility = View.VISIBLE
            }
            calculateDistance()
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                setPickupLocation(latLng)
            }
        }
    }

    private fun setPickupLocation(latLng: LatLng) {
        val address = getAddressFromLatLng(latLng)
        pickupLocation = latLng
        pickupMarker?.remove()
        pickupMarker = googleMap.addMarker(MarkerOptions().position(latLng).title("Pickup: $address"))
        binding.etPickup.setText(address)
        binding.btnClearPickup.visibility = View.VISIBLE

        // Disable only drop field when pickup is set
        binding.etOnlyDrop.isEnabled = false

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        calculateDistance()
    }

    private fun clearPickupLocation() {
        pickupLocation = null
        pickupMarker?.remove()
        pickupMarker = null
        binding.etPickup.text.clear()
        binding.btnClearPickup.visibility = View.GONE

        // Re-enable only drop field when pickup is cleared
        binding.etOnlyDrop.isEnabled = true
    }

    private fun clearDropLocation() {
        dropLocation = null
        binding.etDrop.text.clear()
        binding.btnClearDrop.visibility = View.GONE

        // Re-enable only drop field when drop is cleared
        binding.etOnlyDrop.isEnabled = true
    }

    private fun clearOnlyDropLocation() {
        onlyDropLocation = null
        binding.etOnlyDrop.text.clear()
        binding.btnClearOnlyDrop.visibility = View.GONE

        // Re-enable pickup and drop fields when only drop is cleared
        binding.etPickup.isEnabled = true
        binding.etDrop.isEnabled = true
    }

    private fun searchLocation(locationName: String, isPickup: Boolean, isOnlyDrop: Boolean = false) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (addresses!!.isNotEmpty()) {
                val location = LatLng(addresses[0].latitude, addresses[0].longitude)
                if (isPickup) {
                    setPickupLocation(location)
                } else if (isOnlyDrop) {
                    onlyDropLocation = location
                    binding.etOnlyDrop.setText(addresses[0].getAddressLine(0))
                    binding.btnClearOnlyDrop.visibility = View.VISIBLE
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                } else {
                    dropLocation = location
                    binding.etDrop.setText(addresses[0].getAddressLine(0))
                    binding.btnClearDrop.visibility = View.VISIBLE
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                }
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Error fetching location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAddressFromLatLng(latLng: LatLng): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses.isNullOrEmpty()) {
                "Unknown Location"
            } else {
                addresses[0].getAddressLine(0) ?: "Unknown Location"
            }
        } catch (e: IOException) {
            "Unknown Location"
        } catch (e: Exception) {
            "Unknown Location"
        }
    }

    private fun calculateDistance() {
        val guruGhasidasLatLng = LatLng(22.126461551343123, 82.1371706108157) // Guru Ghasidas University

        // Create a GeoApiContext with your API key
        val context = GeoApiContext.Builder()
            .apiKey("AIzaSyDoK6uVnsidH3hQqZkaSqclQnCgFg-MxLc") // Replace with your API key
            .build()

        // Use a coroutine to perform the network requests on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Calculate GGU to Pickup Distance
                val gguToPickupDistance = if (pickupLocation != null) {
                    val result: DirectionsResult = DirectionsApi.newRequest(context)
                        .origin("${guruGhasidasLatLng.latitude},${guruGhasidasLatLng.longitude}") // GGU coordinates
                        .destination("${pickupLocation!!.latitude},${pickupLocation!!.longitude}") // Pickup coordinates
                        .mode(TravelMode.DRIVING) // Travel mode (DRIVING, WALKING, etc.)
                        .await() // Perform the request and wait for the response

                    result.routes[0].legs[0].distance.inMeters / 1000f // Convert meters to kilometers
                } else {
                    0f // If pickup location is not set, distance is 0
                }

                // Calculate GGU to Drop Distance
                val gguToDropDistance = if (dropLocation != null) {
                    val result: DirectionsResult = DirectionsApi.newRequest(context)
                        .origin("${guruGhasidasLatLng.latitude},${guruGhasidasLatLng.longitude}") // GGU coordinates
                        .destination("${dropLocation!!.latitude},${dropLocation!!.longitude}") // Drop coordinates
                        .mode(TravelMode.DRIVING) // Travel mode (DRIVING, WALKING, etc.)
                        .await() // Perform the request and wait for the response

                    result.routes[0].legs[0].distance.inMeters / 1000f // Convert meters to kilometers
                } else {
                    0f // If drop location is not set, distance is 0
                }

                // Calculate GGU to Only Drop Distance
                val gguToOnlyDropDistance = if (onlyDropLocation != null) {
                    val result: DirectionsResult = DirectionsApi.newRequest(context)
                        .origin("${guruGhasidasLatLng.latitude},${guruGhasidasLatLng.longitude}") // GGU coordinates
                        .destination("${onlyDropLocation!!.latitude},${onlyDropLocation!!.longitude}") // Only Drop coordinates
                        .mode(TravelMode.DRIVING) // Travel mode (DRIVING, WALKING, etc.)
                        .await() // Perform the request and wait for the response

                    result.routes[0].legs[0].distance.inMeters / 1000f // Convert meters to kilometers
                } else {
                    0f // If only drop location is not set, distance is 0
                }

                // Update the UI on the main thread
                runOnUiThread {
                    val distanceText = buildString {
                        if (pickupLocation != null) {
                            append("GGU → Pickup: %.2f km\n".format(gguToPickupDistance))
                        }
                        if (dropLocation != null) {
                            append("GGU → Drop: %.2f km\n".format(gguToDropDistance))
                        }
                        if (onlyDropLocation != null) {
                            append("GGU → Only Drop: %.2f km\n".format(gguToOnlyDropDistance))
                        }
                    }

                    binding.tvDistance.text = distanceText
                }
            } catch (e: Exception) {
                // Handle errors (e.g., network issues, invalid API key, etc.)
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@PickupDropActivity,
                        "Failed to calculate distance: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}