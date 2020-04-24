package com.psvoid.whappens.map

import android.content.res.Resources
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.psvoid.whappens.R
import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.utils.HelperItemReader

class ClusteringViewModel : ViewModel() {
    val algorithm = NonHierarchicalViewBasedAlgorithm<ClusterMarker>(0, 0)//metrics.widthPixels, metrics.heightPixels

    fun readItems(resources: Resources) {
        val inputStream = resources.openRawResource(R.raw.mock_london)
        val items = HelperItemReader().readSerializable(inputStream)
//        algorithm.lock()
//        try {
        algorithm.addItems(items)
//            for (item in items) {
//                val position = item.position
//                algorithm.addItem(ClusterMarker(position.latitude, position.longitude, item.title, item.snippet))
//
//            }
//        } finally {
//        algorithm.unlock()
//        }
    }



}