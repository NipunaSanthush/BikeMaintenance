package com.example.bikemaintenance

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.example.bikemaintenance.data.TripRecord
import com.example.bikemaintenance.viewmodel.MaintenanceViewModel
import com.example.bikemaintenance.viewmodel.MaintenanceViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapFragment : Fragment(), LocationListener {

    private lateinit var map: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var locationManager: LocationManager

    // Navigation සදහා
    private var destinationMarker: Marker? = null
    private var navigationRoute: Polyline? = null

    private val maintenanceViewModel: MaintenanceViewModel by viewModels {
        MaintenanceViewModelFactory((requireActivity().application as BikeApplication).repository)
    }

    private lateinit var tvSpeed: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvDuration: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    private var isTripStarted = false
    private var totalDistance = 0.0f
    private var startTime = 0L
    private var lastLocation: Location? = null
    private var tripRouteLine: Polyline? = null

    private val timerHandler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isTripStarted) {
                val millis = System.currentTimeMillis() - startTime
                val seconds = (millis / 1000).toInt()
                val minutes = seconds / 60
                val hrs = minutes / 60
                tvDuration.text = String.format("%02d:%02d:%02d", hrs, minutes % 60, seconds % 60)
                timerHandler.postDelayed(this, 1000)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                setupMapFeatures()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        Configuration.getInstance().userAgentValue = requireContext().packageName
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map = view.findViewById(R.id.map)
        tvSpeed = view.findViewById(R.id.tvSpeed)
        tvDistance = view.findViewById(R.id.tvDistance)
        tvDuration = view.findViewById(R.id.tvDuration)
        btnStart = view.findViewById(R.id.btnStartTrip)
        btnStop = view.findViewById(R.id.btnStopTrip)

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(18.0)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (checkPermissions()) {
            setupMapFeatures()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }

        btnStart.setOnClickListener { startTrip() }
        btnStop.setOnClickListener { stopTrip() }

        setupMapClickEvents()
    }

    private fun setupMapClickEvents() {
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                return false
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                p?.let { destination ->
                    val myLocation = locationOverlay.myLocation
                    if (myLocation != null) {
                        getRoute(myLocation, destination)
                    } else {
                        Toast.makeText(requireContext(), "Waiting for GPS...", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }
        })
        map.overlays.add(mapEventsOverlay)
    }

    private fun getRoute(start: GeoPoint, end: GeoPoint) {
        Toast.makeText(requireContext(), "Calculating route...", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch(Dispatchers.IO) {
            val roadManager = OSRMRoadManager(requireContext(), Configuration.getInstance().userAgentValue)
            val waypoints = ArrayList<GeoPoint>()
            waypoints.add(start)
            waypoints.add(end)

            val road = roadManager.getRoad(waypoints)

            withContext(Dispatchers.Main) {
                if (road.mStatus != Road.STATUS_OK) {
                    Toast.makeText(requireContext(), "Error finding route!", Toast.LENGTH_SHORT).show()
                } else {
                    navigationRoute?.let { map.overlays.remove(it) }
                    destinationMarker?.let { map.overlays.remove(it) }

                    navigationRoute = RoadManager.buildRoadOverlay(road)
                    navigationRoute?.outlinePaint?.color = Color.BLUE
                    navigationRoute?.outlinePaint?.strokeWidth = 15f
                    map.overlays.add(navigationRoute)

                    destinationMarker = Marker(map)
                    destinationMarker?.position = end
                    destinationMarker?.title = "Destination"
                    destinationMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    map.overlays.add(destinationMarker)

                    val distanceKm = road.mLength
                    val durationMin = road.mDuration / 60
                    Toast.makeText(requireContext(), "Distance: ${"%.1f".format(distanceKm)} km\nTime: ${"%.0f".format(durationMin)} min", Toast.LENGTH_LONG).show()

                    map.invalidate()
                }
            }
        }
    }

    private fun setupMapFeatures() {
        val provider = GpsMyLocationProvider(requireContext())
        locationOverlay = MyLocationNewOverlay(provider, map)

        val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_navigation)?.toBitmap()
        icon?.let {
            locationOverlay.setPersonIcon(it)
            locationOverlay.setDirectionIcon(it)
        }

        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        map.overlays.add(locationOverlay)

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5f, this)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun startTrip() {
        isTripStarted = true
        totalDistance = 0.0f
        startTime = System.currentTimeMillis()
        lastLocation = null

        navigationRoute?.let { map.overlays.remove(it) }
        destinationMarker?.let { map.overlays.remove(it) }
        map.invalidate()

        tripRouteLine?.let { map.overlays.remove(it) }
        tripRouteLine = Polyline().apply {
            outlinePaint.color = Color.RED
            outlinePaint.strokeWidth = 10f
        }
        map.overlays.add(tripRouteLine)

        btnStart.visibility = View.GONE
        btnStop.visibility = View.VISIBLE
        timerHandler.postDelayed(timerRunnable, 0)
    }

    private fun stopTrip() {
        isTripStarted = false
        timerHandler.removeCallbacks(timerRunnable)

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val finalDistance = tvDistance.text.toString()
        val finalDuration = tvDuration.text.toString()

        val timeInSeconds = (System.currentTimeMillis() - startTime) / 1000
        val avgSpeedVal = if (timeInSeconds > 0) (totalDistance / timeInSeconds) * 3.6f else 0f
        val finalAvgSpeed = "%.1f km/h".format(avgSpeedVal)

        if (totalDistance > 10) {
            val trip = TripRecord(
                date = currentDate,
                distance = finalDistance,
                duration = finalDuration,
                avgSpeed = finalAvgSpeed
            )
            maintenanceViewModel.insertTrip(trip)
            Toast.makeText(requireContext(), "Trip Saved Successfully!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Trip too short to save!", Toast.LENGTH_SHORT).show()
        }

        btnStart.visibility = View.VISIBLE
        btnStop.visibility = View.GONE
        tvSpeed.text = "0 km/h"
    }

    override fun onLocationChanged(location: Location) {
        if (isTripStarted) {
            val speedKmh = location.speed * 3.6f
            tvSpeed.text = "%.1f km/h".format(speedKmh)

            if (lastLocation != null) {
                val distance = lastLocation!!.distanceTo(location)
                totalDistance += distance
                tvDistance.text = "%.2f km".format(totalDistance / 1000)
                tripRouteLine?.addPoint(GeoPoint(location.latitude, location.longitude))
            }
            lastLocation = location
            map.invalidate()
        }
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}