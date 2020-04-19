package com.psvoid.whappens.map

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.psvoid.whappens.R
import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.utils.HelperItemReader

class ClusteringViewModel : ViewModel() {
    val algorithm = NonHierarchicalViewBasedAlgorithm<ClusterMarker>(0, 0)//metrics.widthPixels, metrics.heightPixels

    fun readItems(resources: Resources) {
        val inputStream = resources.openRawResource(R.raw.cluster_items)
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