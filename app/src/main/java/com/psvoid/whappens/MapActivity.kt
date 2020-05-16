package com.psvoid.whappens

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager
import com.psvoid.whappens.data.ClusterMarker
import com.psvoid.whappens.network.Config
import com.psvoid.whappens.data.LoadingStatus
import com.psvoid.whappens.viewmodels.MapViewModel
import com.psvoid.whappens.viewmodels.MapViewModelFactory
import com.psvoid.whappens.views.ClusterMarkerRenderer
import kotlin.math.pow

open class MapActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var viewModel: MapViewModel
    private lateinit var map: GoogleMap
    private lateinit var clusterManager: ClusterManager<ClusterMarker>
    private var isRestore = false


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        isRestore = savedInstanceState != null
        viewModel = ViewModelProvider(this, MapViewModelFactory(application)).get(
            MapViewModel::class.java)

        setupMap()
        viewModel.fetchEventsByCountryList(Config.countries) // TODO: Move to MainActivity
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

    /** First run */
    private fun setupRestore() {
        if (!isRestore) {
            // set camera start point
            val location = getMyLocation()
            var latitude: Double = 32.746782
            var longitude: Double = -117.162841

            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 11f))
            }
            // get events near needed point
//            viewModel.getEventsAsync(EventsApiFilter.ALL, latitude, longitude, map.cameraPosition.zoom)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

        setupMapLayers()
        setupBinds()
        if (Config.mapStyle > 0) setMapStyle(map, Config.mapStyle)
        enableLocation()
        setupMapButtons()
        setupRestore()
        setupActions()
        start()
    }

    private fun getMyLocation(): Location? {
        if (isPermissionGranted(FINE_LOCATION)) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val provider = locationManager.getBestProvider(Criteria(), false)
            return locationManager.getLastKnownLocation(provider ?: LocationManager.NETWORK_PROVIDER)
        }
        return null

/*        var locationListener = LocationListener() {
            fun onLocationChanged(location: Location?) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location)
            }

            fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            fun onProviderEnabled(provider: String?) {}
            fun onProviderDisabled(provider: String?) {}
        }*/
    }


    private fun setupMapButtons() {
        map.setMaxZoomPreference(Config.maxMapZoom)
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMapToolbarEnabled = false

    }

    override fun setupLocation() {
        super.setupLocation()
        map.isMyLocationEnabled = true
    }


    /** Set map styling and theming. */
    private fun setMapStyle(map: GoogleMap, style: Int) {
        val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, style))
        if (!success) Log.e("MapActivity", "Setting map style failed.")
    }

    private fun setupMapLayers() {
        // Shared object managers - used to support multiple layer types on the map simultaneously
        val markerManager = MarkerManager(map)
//        val groundOverlayManager = GroundOverlayManager(map)
//        val polygonManager = PolygonManager(map)
//        val polylineManager = PolylineManager(map)

        addClusters(markerManager)
//        addGeoJson(map, markerManager, polylineManager, polygonManager, groundOverlayManager)
//        addKml(map, markerManager, polylineManager, polygonManager, groundOverlayManager)
//        addMarker(markerManager)
    }

    private fun addClusters(markerManager: MarkerManager) {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        viewModel.algorithm.updateViewSize(metrics.widthPixels, metrics.heightPixels)
        clusterManager = ClusterManager(this, map, markerManager)
        clusterManager.setAlgorithm(viewModel.algorithm)
        if (Config.showMarkerImages)
            clusterManager.renderer =
                ClusterMarkerRenderer(this, map, clusterManager, resources)

        map.setOnCameraIdleListener { onCameraIdleListener() }
    }

    /** Handle event when map camera stops moving. */
    private fun onCameraIdleListener() {
        Log.i("MapActivity", "onCameraIdle ${map.cameraPosition.zoom}")
        clusterManager.onCameraIdle()

        //TODO: Check if the position isn't changed but only zoom increased - there is no need to update. Zoom cap
        if (map.cameraPosition.zoom in Config.minSearchZoom..Config.maxMapZoom) {
            val position = map.cameraPosition.target
            viewModel.fetchEvents(position.latitude, position.longitude, radius())
        }
    }

    //TODO: Add screen size and user settings to calculations
    private fun radius() = (Config.searchRadius * 2f.pow(map.maxZoomLevel - map.cameraPosition.zoom)) // Zoom in 3..21

    /** Run the code. */
    private fun start() {

    }

    /** Setup user actions handling*/
    private fun setupActions() {
        // Long click
        map.setOnMapLongClickListener { latLng ->
            val marker = map.addMarker(
                MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
            viewModel.fetchEvents(latLng.latitude, latLng.longitude, radius())
            Handler().postDelayed({ marker.remove() }, 800)
        }
    }
}