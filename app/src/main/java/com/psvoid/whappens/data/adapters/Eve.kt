package com.psvoid.whappens.data.adapters

import com.psvoid.whappens.data.ClusterMarker
import kotlinx.serialization.Serializable

class Eve {

//    companion object {
//        val serializer = Events.serializer()
//    }

    @Serializable
    data class Events(
        val events: Event,
        val page_number: String,
        val page_size: String,
        val page_count: String
    )

    @Serializable
    data class Event(val event: List<ClusterMarker>)

}