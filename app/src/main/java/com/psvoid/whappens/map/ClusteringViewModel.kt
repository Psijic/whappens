/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.psvoid.whappens.map

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.psvoid.whappens.R
import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.utils.MyItemReader

class ClusteringViewModel : ViewModel() {
    val algorithm = NonHierarchicalViewBasedAlgorithm<ClusterMarker>(0, 0)//metrics.widthPixels, metrics.heightPixels

    fun readItems(resources: Resources) {
        val inputStream = resources.openRawResource(R.raw.radar_search)
        val items = MyItemReader().read(inputStream)
        algorithm.lock()
        try {
            for (item in items) {
                val position = item.position
                val offsetItem = ClusterMarker(position.latitude, position.longitude, item.title, item.snippet)
                algorithm.addItem(offsetItem)
            }
        } finally {
            algorithm.unlock()
        }
    }
}