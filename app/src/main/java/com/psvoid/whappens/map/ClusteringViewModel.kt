package com.psvoid.whappens.map

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.psvoid.whappens.R
import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.utils.HelperItemReader

class ClusteringViewModel : ViewModel() {
    val algorithm = NonHierarchicalViewBasedAlgorithm<ClusterMarker>(0, 0)

    fun readItems(resources: Resources) {
        val inputStream = resources.openRawResource(R.raw.mock_london_even)
        val items = HelperItemReader().readSerializable(inputStream)
//        algorithm.lock()
        algorithm.addItems(items)
//        algorithm.unlock()
    }


}