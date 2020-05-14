package com.psvoid.whappens.network

import com.psvoid.whappens.model.Country

object Config {

    const val mapStyle = -1 //R.raw.map_style
    const val showMarkerImages = false
    const val logs = true
    const val defaultSearchRadius = 15f
    const val searchRadius = 0.035f // in miles = 0.022f
    const val minSearchZoom = 11f
    const val maxMapZoom = 21f
    const val period = "Future" // "This Week", "Future"
    const val pageSize = 30
    val countries = listOf(Country.PRT)

}