package com.bypriyan.aaradhyaschoolbusservice.activity

import android.Manifest
import android.content.Intent
import android.content.IntentSender
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
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.TravelMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.IOException
import java.util.*

class PickupDropActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityPickupDropBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var pickupMarker: Marker? = null
    private var pickupLocation: LatLng? = null
    private var dropLocation: LatLng? = null
    private var mode: String? = null
    private var onlyDropLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickupDropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the mode from the intent
        mode = intent.getStringExtra("MODE")



        when (mode) {
            "SAME_LOCATION" -> updateUIForSameLocation()
            "ONLY_DROP" -> updateUIForOnlyDrop()
            "DIFFERENT_LOCATION" -> updateUIForDifferentLocation()
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(com.bypriyan.aaradhyaschoolbusservice.R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        binding.btnCurrentLocation.setOnClickListener { checkLocationSettings() }
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
            }
        }

        binding.btnConfirm.setOnClickListener {
            when (mode) {
                "SAME_LOCATION" -> {
                    val pickupAddress = binding.etPickup.text.toString()
                    val dropAddress = binding.etDrop.text.toString()
                    if (pickupAddress.isEmpty() || dropAddress.isEmpty()) {
                        Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Same Pickup and Drop Location Selected: $pickupAddress",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                "ONLY_DROP" -> {
                    val onlyDropAddress = binding.etOnlyDrop.text.toString()
                    if (onlyDropAddress.isEmpty()) {
                        Toast.makeText(this, "Please enter a drop location", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            this,
                            "Only Drop Location Selected: $onlyDropAddress",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                "DIFFERENT_LOCATION" -> {
                    val pickupAddress = binding.etPickup.text.toString()
                    val dropAddress = binding.etDrop.text.toString()
                    if (pickupAddress.isEmpty() && dropAddress.isEmpty()) {
                        Toast.makeText(
                            this,
                            "Please select at least one location",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Pickup: $pickupAddress, Drop: $dropAddress",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            // Check if the distance is more than 15 km
            if (totalDistance > 15) {
                Toast.makeText(this, "Your distance should be under 15 km", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Pass total distance and mode to PaymentOptionActivity
            val intent = Intent(this, PaymentOptionActivity::class.java).apply {
                putExtra("TOTAL_DISTANCE", totalDistance)
                putExtra("MODE", mode)
            }
            startActivity(intent)
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
            } else if (binding.etPickup.hasFocus()) {
                pickupLocation = centerLatLng
                val address = getAddressFromLatLng(centerLatLng)
                binding.etPickup.setText(address)
                binding.btnClearPickup.visibility = View.VISIBLE

                // Add marker for pickup location
                pickupMarker?.remove()
                pickupMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(centerLatLng)
                        .title("Pickup Location")
                )
            } else {
                dropLocation = centerLatLng
                val address = getAddressFromLatLng(centerLatLng)
                binding.etDrop.setText(address)
                binding.btnClearDrop.visibility = View.VISIBLE
            }
            calculateDistance()
        }

    }

    private fun setPickupLocation(latLng: LatLng) {
        val address = getAddressFromLatLng(latLng)
        pickupLocation = latLng

        // Remove the existing pickup marker (if any)
        pickupMarker?.remove()

        // Add a new marker with a custom pin icon
        pickupMarker = googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Pickup: $address")
        )


        // Update the pickup address in the UI
        binding.etPickup.setText(address)
        binding.btnClearPickup.visibility = View.VISIBLE



        // Move the camera to the pickup location
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        // Calculate the distance
        calculateDistance()
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch the current location
                getCurrentLocation()
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // Location settings are enabled, proceed to get the current location
            getCurrentLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not enabled, show a dialog to enable them
                try {
                    exception.startResolutionForResult(this, 2)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error
                    Toast.makeText(this, "Failed to enable location settings", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Location settings not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                when (mode) {
                    "ONLY_DROP" -> {
                        onlyDropLocation = latLng
                        val address = getAddressFromLatLng(latLng)
                        binding.etOnlyDrop.setText(address)
                        binding.btnClearOnlyDrop.visibility = View.VISIBLE
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                    else -> {
                        setPickupLocation(latLng)
                    }
                }
            } else {
                // Last location is null, request location updates
                requestLocationUpdates()
            }
        }
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        when (mode) {
                            "ONLY_DROP" -> {
                                onlyDropLocation = latLng
                                val address = getAddressFromLatLng(latLng)
                                binding.etOnlyDrop.setText(address)
                                binding.btnClearOnlyDrop.visibility = View.VISIBLE
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                            }
                            else -> {
                                setPickupLocation(latLng)
                            }
                        }
                        // Stop location updates after getting the location
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            },
            null
        )
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

    private fun updateUIForOnlyDrop() {


        // Show only drop-related views
        binding.onlyDropLocation.visibility = View.VISIBLE
        binding.etOnlyDrop.visibility = View.VISIBLE
        binding.btnClearOnlyDrop.visibility = View.VISIBLE

        // Set focus on the only drop field
        binding.etOnlyDrop.requestFocus()

        // Disable pickup and drop fields
        binding.etPickup.isEnabled = false
        binding.etDrop.isEnabled = false
    }

    private fun updateUIForDifferentLocation() {
        // Show all fields (pickup, drop, and only drop)
        binding.txtPLocation.visibility = View.VISIBLE
        binding.etPickup.visibility = View.VISIBLE
        binding.btnClearPickup.visibility = View.VISIBLE

        binding.txtDLocation.visibility = View.VISIBLE
        binding.etDrop.visibility = View.VISIBLE
        binding.btnClearDrop.visibility = View.VISIBLE
        binding.txtDLocation.setText("Different Drop Location")


        // Enable all fields
        binding.etPickup.isEnabled = true
        binding.etDrop.isEnabled = true
    }


    private fun updateUIForSameLocation() {
        // Show pickup and drop fields
//        binding.txtPLocation.visibility = View.VISIBLE
//        binding.etPickup.visibility = View.VISIBLE
//        binding.btnClearPickup.visibility = View.VISIBLE

        binding.txtDLocation.visibility = View.VISIBLE
        binding.etDrop.visibility = View.VISIBLE
        binding.btnClearDrop.visibility = View.VISIBLE
        binding.txtDLocation.setText("Same Pickup And Drop")

        // Hide only drop-related views
        binding.onlyDropLocation.visibility = View.GONE
        binding.etOnlyDrop.visibility = View.GONE
        binding.btnClearOnlyDrop.visibility = View.GONE


        // Set the same location for pickup and drop
        binding.etDrop.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.etPickup.setText(binding.etDrop.text.toString())
                true
            } else false
        }
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


    }
    private var totalDistance: Float = 0f

    private fun calculateDistance() {
        val rklGalaxySchool = LatLng(18.65355422287287, 73.88056008151783) // GGU Coordinates

        val context = GeoApiContext.Builder()
            .apiKey("AIzaSyDoK6uVnsidH3hQqZkaSqclQnCgFg-MxLc") // Replace with your API Key
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                var gguToPickupDistance = 0f
                var gguToDropDistance = 0f
                var gguToOnlyDropDistance = 0f

                if (pickupLocation != null) {
                    val result = DirectionsApi.newRequest(context)
                        .origin("${rklGalaxySchool.latitude},${rklGalaxySchool.longitude}")
                        .destination("${pickupLocation!!.latitude},${pickupLocation!!.longitude}")
                        .mode(TravelMode.DRIVING)
                        .await()
                    gguToPickupDistance = result.routes[0].legs[0].distance.inMeters / 1000f
                }

                if (dropLocation != null) {
                    val result = DirectionsApi.newRequest(context)
                        .origin("${rklGalaxySchool.latitude},${rklGalaxySchool.longitude}")
                        .destination("${dropLocation!!.latitude},${dropLocation!!.longitude}")
                        .mode(TravelMode.DRIVING)
                        .await()
                    gguToDropDistance = result.routes[0].legs[0].distance.inMeters / 1000f
                }

                if (onlyDropLocation != null) {
                    val result = DirectionsApi.newRequest(context)
                        .origin("${rklGalaxySchool.latitude},${rklGalaxySchool.longitude}")
                        .destination("${onlyDropLocation!!.latitude},${onlyDropLocation!!.longitude}")
                        .mode(TravelMode.DRIVING)
                        .await()
                    gguToOnlyDropDistance = result.routes[0].legs[0].distance.inMeters / 1000f
                }

                // Calculate total distance based on mode
                totalDistance = when (mode) {
                    "SAME_LOCATION" -> gguToPickupDistance + gguToDropDistance
                    "ONLY_DROP" -> gguToOnlyDropDistance
                    "DIFFERENT_LOCATION" -> gguToPickupDistance + gguToDropDistance
                    else -> 0f
                }

                runOnUiThread {
                    val distanceText = "Total Distance: %.2f km".format(totalDistance)
                    binding.tvDistance.text = distanceText
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@PickupDropActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
//cc1.1