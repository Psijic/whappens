package com.psvoid.whappens.model.adapters

import com.psvoid.whappens.model.ClusterMarker
import kotlinx.serialization.Serializable

/* This should be on server side */
class Eve {

    companion object {
        val serializer = Events.serializer()
    }

    @Serializable
    data class Events(val events: Event)

    @Serializable
    data class Event(val event: List<ClusterMarker>)

}