package com.psvoid.whappens.data

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MarkerRepository(private val markerDao: MarkerDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    suspend fun getAllMarkers(): List<ClusterMarker> = markerDao.getAllMarkers()
    suspend fun getMarkersByCountry(countryCode: String): List<ClusterMarker> = markerDao.getMarkersByCountry(countryCode)

    suspend fun insert(marker: ClusterMarker) = markerDao.insert(marker)
    suspend fun insert(markers: List<ClusterMarker>) = markerDao.insert(markers)
}