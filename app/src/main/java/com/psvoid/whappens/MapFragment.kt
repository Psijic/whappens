package com.psvoid.whappens

import android.annotation.SuppressLint
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager
import com.psvoid.whappens.data.ClusterMarker
import com.psvoid.whappens.data.EventFilter
import com.psvoid.whappens.utils.LoadingStatus
import com.psvoid.whappens.databinding.FragmentMapBinding
import com.psvoid.whappens.network.Config
import com.psvoid.whappens.viewmodels.MapViewModel
import com.psvoid.whappens.views.ClusterMarkerRenderer
import com.psvoid.whappens.views.ClusterMarkerRendererPhoto
import timber.log.Timber
import kotlin.math.pow


class MapFragment : BaseFragment() {
    private val viewModel: MapViewModel by viewModels()
    private var isRestore = false

    private lateinit var map: GoogleMap
    private lateinit var clusterManager: ClusterManager<ClusterMarker>
    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        isRestore = savedInstanceState != null
        setupMap()
        setupTopAppBar()
        return binding.root
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
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
    }

    private fun setupBinds() {
        viewModel.clusterStatus.observe(viewLifecycleOwner, {
            if (LoadingStatus.DONE == it) {
                clusterManager.cluster()
            }
        })

        // bottomSheet
        viewModel.selectedEvent.observe(viewLifecycleOwner, {
            binding.event = it
            val behavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheet)
            binding.bottomSheetState = when {
                it == null -> BottomSheetBehavior.STATE_HIDDEN
                behavior.state != BottomSheetBehavior.STATE_HIDDEN -> behavior.state
                else -> BottomSheetBehavior.STATE_COLLAPSED
            }
        })

        // topBar UI
        viewModel.isHideUI.observe(viewLifecycleOwner, {
            binding.isHideUI = it
        })
    }

    /** First run */
    private fun setupRestore() {
        if (!isRestore) {
            // set camera start point
            val location = getMyLocation()
            var latitude = 33.753746
            var longitude = -84.386330

            location?.let {
                latitude = location.latitude
                longitude = location.longitude
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 11f))

            // get events near needed point
//            viewModel.getEventsAsync(EventsApiFilter.ALL, latitude, longitude, map.cameraPosition.zoom)
        } else {
            // pass
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation(): Location? {
        if (isPermissionGranted(FINE_LOCATION)) {
//            val locationManager = getSystemService(requireContext(), Context.LOCATION_SERVICE) as LocationManager
            val locationManager = getSystemService(requireContext(), LocationManager::class.java) as LocationManager
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
        val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, style))
        if (!success) Timber.e("Setting map style failed.")
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
        context?.display?.getRealMetrics(metrics)
        viewModel.algorithm.updateViewSize(metrics.widthPixels, metrics.heightPixels)
        clusterManager = ClusterManager(context, map, markerManager)
        clusterManager.setAlgorithm(viewModel.algorithm)
        clusterManager.renderer = if (Config.showMarkerImages)
            ClusterMarkerRendererPhoto(requireContext(), map, clusterManager, resources)
        else
            ClusterMarkerRenderer(requireContext(), map, clusterManager, viewModel)


//        clusterManager.renderer.setOnClusterItemClickListener {  }
//        // Get the icon for the feature
//        val pointIcon = BitmapDescriptorFactory
//            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
    }

    /** Handle event when map camera stops moving. */
    private fun onCameraIdleListener() {
        Timber.i("onCameraIdle ${map.cameraPosition.zoom}")
        clusterManager.onCameraIdle()

        //TODO: Check if the position hasn't changed but only zoom increased - there is no need to update. Zoom cap
        if (map.cameraPosition.zoom in Config.minSearchZoom..Config.maxMapZoom) {
            val position = map.cameraPosition.target
            viewModel.fetchEvents(position.latitude, position.longitude, radius())
        }
    }

    //TODO: Add screen size and user settings to calculations?
    private fun radius() = (Config.searchRadius * 2f.pow(map.maxZoomLevel - map.cameraPosition.zoom)) // Zoom in 3..21

    /** Run the code. */
    private fun start() {

    }

    /** Setup user actions handling*/
    private fun setupActions() {
        map.setOnCameraIdleListener { onCameraIdleListener() }

        // Long click
        map.setOnMapLongClickListener { latLng ->
            val marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )

            viewModel.fetchEvents(latLng.latitude, latLng.longitude, radius())
            Handler(Looper.getMainLooper()).postDelayed({ marker.remove() }, 800)
        }

        // Click
        clusterManager.setOnClusterItemClickListener(::onClusterItemClick)
        map.setOnMapClickListener { onMapClickListener() }

//        map.setOnMarkerClickListener(clusterManager)
    }

    /** Called when the user clicks a ClusterMarker.  */
    private fun onClusterItemClick(item: ClusterMarker): Boolean {
        viewModel.selectedEvent.postValue(item)
        binding.event = item

        viewModel.isHideUI.postValue(false)
//        binding.executePendingBindings()

        // add custom behaviour
        val update = CameraUpdateFactory.newLatLng(LatLng(item.latitude, item.longitude))
        map.animateCamera(update, Config.animateCameraDuration, null)
        // remove default marker behaviour (move camera and show a popup)

//        clusterManager.updateItem(item)
        return true
    }

    private fun onMapClickListener() {
        Timber.v("onMapClickListener")

        //remove marker selected status
        if (viewModel.selectedEvent.value != null) clusterManager.cluster() //clusterManager.updateItem(viewModel.selectedEvent.value)
        viewModel.selectedEvent.postValue(null)
        viewModel.isHideUI.postValue(viewModel.isHideUI.value?.not())
    }

    /** Menu */
    private fun setupTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            // Handle navigation icon press
            showMenu(it, R.menu.menu_main)
            Timber.d("TopAppBar Navigation Clicked")
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.category -> {
                    true
                }
                R.id.date_range -> {
                    val dialog = MaterialAlertDialogBuilder(requireContext())
                        .setView(R.layout.fragment_dialog)
                        .show()
                    dialog.findViewById<MaterialButton>(R.id.button_range)?.setOnClickListener {
                        dialog.dismiss()
                        showDatePicker()
                    }
                    setDialogAction(dialog, R.id.button_future, EventFilter.Period.FUTURE)
                    setDialogAction(dialog, R.id.button_today, EventFilter.Period.TODAY)
                    setDialogAction(dialog, R.id.button_week, EventFilter.Period.WEEK)
                    setDialogAction(dialog, R.id.button_month, EventFilter.Period.MONTH)

                    true
                }
                R.id.search -> {
                    true
                }
                R.id.list -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun setDialogAction(dialog: AlertDialog, @IdRes id: Int, period: EventFilter.Period) =
        dialog.findViewById<MaterialButton>(id)?.setOnClickListener {
            dialog.dismiss()
            viewModel.setPeriod(period)
        }

    private fun showDatePicker() {
        val picker = MaterialDatePicker.Builder.dateRangePicker().build()
        picker.addOnPositiveButtonClickListener {
            Timber.d("DatePicker: ${it.first} to ${it.second}")
        }
        picker.show(parentFragmentManager, picker.toString())
    }


    private fun showSnackbar(view: View, text: CharSequence) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
    }

}