package com.psvoid.whappens.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber


// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MarkerRepository(private val markerDao: MarkerDao) {
    private val firebaseDb: DatabaseReference = Firebase.database.reference

    init {
//        Firebase.database.setPersistenceEnabled(true) // TODO: Move
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    suspend fun getAllMarkers(): List<ClusterMarker> = markerDao.getAllMarkers()
    suspend fun getMarkersByCountry(countryCode: String): List<ClusterMarker> = markerDao.getMarkersByCountry(countryCode)

    suspend fun insert(marker: ClusterMarker) = markerDao.insert(marker)
    suspend fun insert(markers: List<ClusterMarker>) = markerDao.insert(markers)

    @ExperimentalCoroutinesApi
    suspend fun fetchFirebase(countryName: String, period: String): List<ClusterMarker>? = suspendCancellableCoroutine {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Timber.v("fetch Firebase markers: onDataChange")
                val data = dataSnapshot.getValue<List<ClusterMarker>>()
                it.resume(data) {}
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.w("fetch Firebase markers: onCancelled ${databaseError.toException()}")
                it.cancel()
            }
        }

        val db = firebaseDb.child("events").child(countryName)
        db.addListenerForSingleValueEvent(listener)
        it.invokeOnCancellation { db.removeEventListener(listener) }
    }

    @ExperimentalCoroutinesApi
    fun fetchFirebaseFlow(countryName: String, period: String): Flow<List<ClusterMarker>?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Timber.v("fetch Firebase markers: onDataChange")
                val data = dataSnapshot.getValue<List<ClusterMarker>>()
                offer(data)
                close()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.w("fetch Firebase markers: onCancelled ${databaseError.toException()}")
                cancel()
            }
        }
        val ref = firebaseDb.child("events").child(countryName)
        ref.addListenerForSingleValueEvent(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

}