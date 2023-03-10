package com.example.osmapp

import android.Manifest
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.osmapp.databinding.ActivityMainBinding
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val startPoint = GeoPoint(55.7541, 37.6204) //Red square

    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>


    private lateinit var locationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        configurationMap()

        initMap()
        getZoomMultiTouch()

        setStartMarker(startPoint)




        binding.button.setOnClickListener {
            getLocation()

            if (checkNetwork()) {
                locationOverlay.enableFollowLocation()
                binding.map.controller.animateTo(locationOverlay.myLocation)
                binding.map.controller.setZoom(12.0)
            }
        }

    }


    private fun getZoomMultiTouch() {
        binding.map.setMultiTouchControls(true)
        binding.map.setBuiltInZoomControls(true)
    }

    private fun initMap() {
        val mapController = binding.map.controller
        mapController.setZoom(12.0)
        mapController.setCenter(startPoint)
    }

    private fun configurationMap() {
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        Configuration.getInstance().osmdroidBasePath = filesDir
    }

    private fun setStartMarker(geoPoint: GeoPoint) {
        val marker = Marker(binding.map)
        marker.position = geoPoint
        marker.title = getString(R.string.red_square)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.map.overlays.add(marker)
        binding.map.invalidate()
    }

    private fun getLocation() {

        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), binding.map)
        locationOverlay.enableMyLocation()
        binding.map.overlays.add(locationOverlay)
    }

    private fun checkNetwork(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS выключен", Toast.LENGTH_SHORT).show()
            false
        } else true
    }
}