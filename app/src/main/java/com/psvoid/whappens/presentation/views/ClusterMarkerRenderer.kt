package com.psvoid.whappens.presentation.views

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.psvoid.whappens.data.ClusterMarker
import com.psvoid.whappens.presentation.viewmodels.MapViewModel
import timber.log.Timber


/**
 * Draws profile images inside markers (using IconGenerator).
 * When there are multiple images in the cluster, draw multiple images (using MultiDrawable).
 */
class ClusterMarkerRenderer(
    context: Context, map: GoogleMap,
    clusterManager: ClusterManager<ClusterMarker>,
    private val viewModel: MapViewModel
) : DefaultClusterRenderer<ClusterMarker>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: ClusterMarker, markerOptions: MarkerOptions) {
        markerOptions.icon(getMarkerColor(item))
        Timber.v("onBeforeClusterItemRendered")
    }

    override fun onClusterItemUpdated(item: ClusterMarker, marker: Marker) {
        marker.setIcon(getMarkerColor(item))
        Timber.v("onClusterItemUpdated")
    }

    private fun getMarkerColor(item: ClusterMarker): BitmapDescriptor {
        val color = if (item.id == viewModel.selectedEvent.value?.id) BitmapDescriptorFactory.HUE_ORANGE
        else item.getCategoryColor()
        return BitmapDescriptorFactory.defaultMarker(color)

//        return if (item.id == viewModel.selectedEvent.value?.id)
//            BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_24)
//        else BitmapDescriptorFactory.defaultMarker(item.getCategoryColor())
    }
}