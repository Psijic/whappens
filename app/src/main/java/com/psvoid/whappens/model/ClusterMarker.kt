package com.psvoid.whappens.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A data class that implements the [ClusterItem] interface so it can be clustered. */
@Serializable
data class ClusterMarker(
//    @SerialName("title")
    val name: String,
    val id: String,
    val url: String? = null,
    val locale: String = "en",
//    @SerialName("image")
    val images: Images? = null,
    @SerialName("start_time")
    val startTime: String,
    val latitude: Double,
    val longitude: Double,
    val description: String? = null,
    val price: String? = null,
    val categories: Categories? = null,
//    val popularity: String? = null,
    val popularity: Int? = null,
//    @SerialName("venue_address")
    val address: String? = null,
    val country_name: String,
    val country_abbr: String,
    val city_name: String? = null,
    val region_name: String? = null
//    val performers: Performer
//    val place: Place,

) : ClusterItem {

    override fun getPosition() = LatLng(latitude, longitude)
    override fun getTitle() = name
    override fun getSnippet() = address ?: startTime

    @Serializable
    data class Categories(val category: List<IdName>)

    @Serializable
    data class Images(val thumb: EventImage, val block250: EventImage)

    //@Serializable
    //data class Performer(val performer: List<IdName>)
}



