package com.psvoid.whappens.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.random.Random

/**
 * A data class that implements the [ClusterItem] interface so it can be clustered.
 *
 *  Unfortunately Kotlin data class fields do not override Java interface methods by default
 *  (https://youtrack.jetbrains.com/issue/KT-6653?_ga=2.30406975.1494223917.1585591891-1137021041.1573759593)
 *  so we must name our fields differently and then pass them to the ClusterItem methods.
 */
@Serializable
data class ClusterMarker1(
    val lat: Double,
    val lng: Double,
    @SerialName("title")
    val name: String = "",
    @SerialName("snippet")
    val description: String = "",
    val image: String = ""
) : ClusterItem {

    override fun getPosition() = LatLng(lat, lng)
    override fun getTitle() = name
    override fun getSnippet() = description
}

@Serializable
data class ClusterMarker(
    val name: String,
    val type: String,
    val id: String,
    val url: String,
    val locale: String,
//        val images: List<EventImage>,
    val dateTime: String = "",
    val latitude: Double = Random.nextDouble( 51.38494009999999, 51.6723432),
    val longitude: Double = Random.nextDouble(-0.3514683, 0.148271)
) : ClusterItem {

    override fun getPosition() = LatLng(latitude, longitude)
    override fun getTitle() = name
    override fun getSnippet() = type
}