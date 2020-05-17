package com.psvoid.whappens.data

import androidx.lifecycle.LiveData

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MarkerRepository(private val markerDao: MarkerDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    fun getAllMarkers(): LiveData<List<ClusterMarker>> = markerDao.getAllMarkers()
    fun getAllMarkersByCountry(abbr: String): LiveData<List<ClusterMarker>> = markerDao.getAllMarkersByCountry(abbr)

    suspend fun insert(marker: ClusterMarker) = markerDao.insert(marker)
    suspend fun insert(markers: List<ClusterMarker>) = markerDao.insert(markers)
}