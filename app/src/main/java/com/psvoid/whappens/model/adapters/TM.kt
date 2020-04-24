package com.psvoid.whappens.model.adapters

import com.psvoid.whappens.model.ClusterMarker
import kotlinx.serialization.Serializable

class TM {

    companion object {
        val serializer = Embedded.serializer()
    }
//    @Serializable
//    data class Top(val top: All)
//
//    @Serializable
//    data class All(val all: List<ClusterMarker>)

    @Serializable
    data class Embedded(val _embedded: Events)

    @Serializable
    data class Events(val events: List<ClusterMarker>)

//    @Serializable
//    data class StreetEvent(
//        val name: String,
//        val type: String,
//        val id: String,
//        val url: String,
//        val locale: String,
////        val images: List<EventImage>,
//        val dateTime: String,
//        val latitude: Double = nextDouble(51.6723432, 51.38494009999999),
//        val longitude: Double = nextDouble(0.148271, -0.3514683)
//    ) : ClusterItem {
//
//        override fun getPosition() = LatLng(latitude, longitude)
//        override fun getTitle() = name
//        override fun getSnippet() = type
//    }

//    public val serializer = Embedded.serializer()

    /*
    "_embedded": {
    "events": [
      {
        "name": "Eagles",
        "type": "event",
        "id": "1AMZAraGkdON1TD",
        "test": false,
        "url": "https://www.ticketmaster.co.uk/eagles-london-08-29-2020/event/37005789E08CAFFB",
        "locale": "en-us",
        "images": [
        */

}