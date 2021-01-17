package com.psvoid.whappens.network

import com.psvoid.whappens.data.EventFilter

object Config {
    const val animateCameraDuration = 300
    const val mapStyle = -1 //R.raw.map_style
    const val showMarkerImages = false
    const val logs = true
    const val defaultSearchRadius = 15f
    const val searchRadius = 0.035f // in miles = 0.022f
    const val minSearchZoom = 11f
    const val maxMapZoom = 21f
    val period = EventFilter.Period.FUTURE
    const val pageSize = 30
    const val cacheRefreshTime = 86400000 // 24 hours
    var countries = listOf("DEU", "USA", "LVA")
    val launchTime = System.currentTimeMillis()
}