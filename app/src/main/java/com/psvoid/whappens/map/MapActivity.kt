package com.psvoid.whappens.map

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager
import com.psvoid.whappens.R
import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.network.Config

open class MapActivity : FragmentActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private var isRestore = false

    private lateinit var clusterManager: ClusterManager<ClusterMarker>

    private lateinit var viewModel: MapViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        isRestore = savedInstanceState != null
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        setupMap()
//        setupRestore()
    }

    private fun setupRestore() {
        if (!isRestore) { // first run
//            viewModel.readItems(resources)
            viewModel.getEventsAsync(EventsApiFilter.ALL)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        if (!isRestore) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(51.503186, -0.126446), 10f))
        }
        showMapLayers()
        start()
    }

    private fun showMapLayers() {
        // Shared object managers - used to support multiple layer types on the map simultaneously
        val markerManager = MarkerManager(map)
//        val groundOverlayManager = GroundOverlayManager(map)
//        val polygonManager = PolygonManager(map)
//        val polylineManager = PolylineManager(map)

        addClusters(map, markerManager)
//        addGeoJson(map, markerManager, polylineManager, polygonManager, groundOverlayManager)
//        addKml(map, markerManager, polylineManager, polygonManager, groundOverlayManager)
//        addMarker(markerManager)
    }

    private fun addClusters(map: GoogleMap, markerManager: MarkerManager) {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        viewModel.algorithm.updateViewSize(metrics.widthPixels, metrics.heightPixels)
        clusterManager = ClusterManager(this, map, markerManager)
        clusterManager.setAlgorithm(viewModel.algorithm)
        if (Config.showMarkerImages)
            clusterManager.renderer = ClusterMarkerRenderer(this, map, clusterManager, resources)
        map.setOnCameraIdleListener(clusterManager)
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /** Run the code. */
    private fun start() {
        setupRestore()
    }

//        val start = LatLng(37.42, -122.20)
//        map.addMarker(MarkerOptions().position(start).title("Marker Start"))
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 15f))


}
