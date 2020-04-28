package com.psvoid.whappens.map

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager
import com.psvoid.whappens.BaseActivity
import com.psvoid.whappens.R
import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.network.Config
import com.psvoid.whappens.network.LoadingStatus

open class MapActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var viewModel: MapViewModel
    private lateinit var map: GoogleMap
    private lateinit var clusterManager: ClusterManager<ClusterMarker>
    private var isRestore = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        isRestore = savedInstanceState != null
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        setupMap()
        setupBinds()
        setupRestore()
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupBinds() {
        viewModel.clusterStatus.observe(this, Observer {
            if (LoadingStatus.DONE == it) {
                clusterManager.cluster()
            }
        })
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
        if (Config.mapStyle > 0) setMapStyle(map, Config.mapStyle)
        enableLocation()
        setupMapButtons()
        start()
    }

    private fun setupMapButtons() {
//        map.setMaxZoomPreference (15f)
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
    }

    override fun setupLocation() {
        map.isMyLocationEnabled = true
    }


    /** Set map styling and theming. */
    private fun setMapStyle(map: GoogleMap, style: Int) {
        val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, style))
        if (!success) Log.e("MapActivity", "Setting map style failed.")
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

    /** Run the code. */
    private fun start() {
//        setupRestore()
    }
}
