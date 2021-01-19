package com.psvoid.whappens.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.psvoid.whappens.BuildConfig
import com.psvoid.whappens.network.Config
import com.psvoid.whappens.network.EventsApi
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber


// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class EventsRepository(private val markerDao: MarkerDao) {
    private val firebaseDb: DatabaseReference = Firebase.database.reference

    /** Create a Coroutine scope using a job to be able to cancel when needed */
    private val job = Job()

    /** Coroutine runs using the IO dispatcher */
    private val coroutineScope = CoroutineScope(job + Dispatchers.IO)

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
    suspend fun fetchFirebase(countryName: String, period: EventFilter.Period): List<ClusterMarker>? =
        suspendCancellableCoroutine {
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
    fun fetchFirebaseFlow(countryName: String, period: EventFilter.Period): Flow<List<ClusterMarker>?> = callbackFlow {
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

    fun fetchEventsByHttp(lat: Double, lng: Double, radius: Float) {
        job.cancelChildren(CancellationException("Updated"))
        val queryOptions = getEventfulQueryOptions(lat, lng, radius, Config.period)
        fetchEventsInternal(lat, lng, queryOptions, 1)
    }

    /**
     * The Retrofit service returns a coroutine, which we await to get the result of the transaction.
     * @param filter the [EventsApiFilter] that is sent as part of the web server request
     * @param page the events list page within the response, usually started from 1
     */
    private fun fetchEventsInternal(lat: Double, lng: Double, queryOptions: MutableMap<String, String>, page: Int = 1) {
        coroutineScope.launch {
            // Get the object for Retrofit request
//            _clusterStatus.postValue(LoadingStatus.LOADING)
            try {
                queryOptions["page_number"] = page.toString()
                val listResult = EventsApi.retrofitService.getEventsAsync(queryOptions)
                val markers = listResult.events.event
//                addClusterItems(markers) // TODO: return markers
                if (page < listResult.page_count.toInt())
                    fetchEventsInternal(lat, lng, queryOptions, page.inc())
                else
                    Timber.i("All events downloaded")
            } catch (e: Exception) {
                Timber.e("Error loading events $e")
//                _clusterStatus.postValue(LoadingStatus.ERROR)
            }
        }
    }

    /** Adding query params. */
    private fun getEventfulQueryOptions(lat: Double, lng: Double, radius: Float, period: EventFilter.Period): MutableMap<String, String> {
        val options = mutableMapOf<String, String>()
        options["app_key"] = BuildConfig.EVENTFUL_KEY
        options["where"] = "$lat,$lng"
        options["within"] = radius.toString()
        options["date"] = period.name // Need to check actual names
        options["page_size"] = Config.pageSize.toString()
//        options["include"] = "categories,popularity,price" //subcategories
        options["image_sizes"] = "thumb,block250"
        return options
    }

    fun dispose() {
        job.cancel() // Cancel our coroutine [job], which tells the Retrofit service to stop.
    }

}