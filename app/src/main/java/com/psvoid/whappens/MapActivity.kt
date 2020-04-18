package com.psvoid.whappens

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager
import com.psvoid.whappens.map.ClusteringViewModel
import com.psvoid.whappens.model.ClusterMarker
import org.json.JSONException

open class MapActivity : FragmentActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private var isRestore = false

    private lateinit var mClusterManager: ClusterManager<ClusterMarker>
    private lateinit var mViewModel: ClusteringViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        isRestore = savedInstanceState != null
        mViewModel = ViewModelProviders.of(this).get(ClusteringViewModel::class.java)


        setUpMap()
        checkRestore()
    }

    private fun checkRestore() {
        if (!isRestore) {
            try {
                mViewModel.readItems(resources)
            } catch (e: JSONException) {
                Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        if (!isRestore)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(51.503186, -0.126446), 10f))
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
        mViewModel.algorithm.updateViewSize(metrics.widthPixels, metrics.heightPixels)
        mClusterManager = ClusterManager(this, map)
        mClusterManager.setAlgorithm(mViewModel.algorithm)
        map.setOnCameraIdleListener(mClusterManager)
    }

    private fun setUpMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Run the demo-specific code.
     */
    protected fun start() {

    }

//        val start = LatLng(37.42, -122.20)
//        map.addMarker(MarkerOptions().position(start).title("Marker Start"))
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 15f))

}
