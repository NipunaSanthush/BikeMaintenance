package com.example.bikemaintenance

import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class MapFragment : Fragment() {

    private lateinit var map: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // OSM Configuration ලෝඩ් කිරීම
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        map = view.findViewById(R.id.map)

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(6.9271, 79.8612)
        mapController.setCenter(startPoint)

    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}