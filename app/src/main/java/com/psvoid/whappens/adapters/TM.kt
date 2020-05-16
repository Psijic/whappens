package com.psvoid.whappens.adapters

import com.psvoid.whappens.data.ClusterMarker
import kotlinx.serialization.Serializable

class TM {

    companion object {
        val serializer = Embedded.serializer()
    }

    @Serializable
    data class Embedded(val _embedded: Events)

    @Serializable
    data class Events(val events: List<ClusterMarker>)
}