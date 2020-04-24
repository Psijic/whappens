package com.psvoid.whappens.map

import android.content.Context
import android.content.res.Resources
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.psvoid.whappens.R
import com.psvoid.whappens.model.ClusterMarker

/**
 * Draws profile images inside markers (using IconGenerator).
 * When there are multiple images in the cluster, draw multiple images (using MultiDrawable).
 */
class ClusterMarkerRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<ClusterMarker>,
    resources: Resources
) :
    DefaultClusterRenderer<ClusterMarker>(context, map, clusterManager) {
    private val iconGenerator = IconGenerator(context)

    //    val clusterIconGenerator = IconGenerator(context)
    private var imageView: ImageView = ImageView(context)

    //    private var clusterImageView: ImageView

    init {
//        val multiProfile: View = LayoutInflater().inflate(R.layout.multi_profile, null)
//        mClusterIconGenerator.setContentView(multiProfile)
//        clusterImageView = multiProfile.findViewById(R.id.image)

        val dimension = resources.getDimension(R.dimen.custom_profile_image).toInt()
        imageView.layoutParams = ViewGroup.LayoutParams(dimension, dimension)
        val padding = resources.getDimension(R.dimen.custom_profile_padding).toInt()
        imageView.setPadding(padding, padding, padding, padding)
        iconGenerator.setContentView(imageView)
    }

    override fun onBeforeClusterItemRendered(marker: ClusterMarker, markerOptions: MarkerOptions) {
        // Draw a single marker - show their profile photo and set the info window to show their name
        markerOptions
            .icon(getItemIcon(marker))
            .title(marker.name)
    }

    /**
     * Get a descriptor for a single marker (marker outside a cluster) for a marker icon
     * @param marker ClusterMarker to return an BitmapDescriptor for
     * @return the marker's profile photo as a BitmapDescriptor
     */
    private fun getItemIcon(marker: ClusterMarker): BitmapDescriptor {
        imageView.setImageResource(R.drawable.john) //TODO: change to image
//        mImageView.setImageResource(marker.image)
        val icon = iconGenerator.makeIcon()
        return BitmapDescriptorFactory.fromBitmap(icon)
    }
}