package com.psvoid.whappens.map

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.psvoid.whappens.R
import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.network.Config
import com.psvoid.whappens.network.EventsApi
import com.psvoid.whappens.network.LoadingStatus
import com.psvoid.whappens.utils.HelperItemReader
import kotlinx.coroutines.*
import java.lang.Math.toRadians
import kotlin.collections.set
import kotlin.math.cos
import kotlin.math.roundToInt

/** Possible to inline factory https://www.albertgao.xyz/2018/04/13/how-to-add-additional-parameters-to-viewmodel-via-kotlin */
class MapViewModelFactory(private val resources: Resources) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) return MapViewModel(resources) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MapViewModel(private val resources: Resources) : ViewModel() {
    companion object {
        const val TAG = "MapViewModel"

        data class Cluster(
            val bounds: Bounds,
            val markers: List<ClusterMarker>,
            val timestamp: Long = System.currentTimeMillis()
        )

        data class Bounds(
            val latMin: Double,
            val latMax: Double,
            val lngMin: Double,
            val lngMax: Double
        ) {
            fun contains(lat: Double, lng: Double) = lat in latMin..latMax && lng in lngMin..lngMax
        }
    }

    val algorithm = NonHierarchicalViewBasedAlgorithm<ClusterMarker>(0, 0)

    /* Read local JSON file */
    fun readResourceJson() {
        val inputStream = resources.openRawResource(R.raw.event_utah)
        val items = HelperItemReader().readSerializable(inputStream)
        addClusterItems(items)
    }

    // The internal MutableLiveData that stores the status of the most recent request
    private val _clusterStatus = MutableLiveData<LoadingStatus>()
    val clusterStatus: LiveData<LoadingStatus>
        get() = _clusterStatus

    // Create a Coroutine scope using a job to be able to cancel when needed
    private val viewModelJob = Job()

    // the Coroutine runs using the IO dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private var database: DatabaseReference
//    private var database1 = Firebase.database.getReference("events")


    //    private var cachedMarkers: List<Cluster> = emptyList()
    private var cachedMarkers: MutableList<Cluster> = mutableListOf()
//    private var cachedMarkers: Dictionary<Bounds, Cluster> =

    init {
        Firebase.database.setPersistenceEnabled(true)
        database = Firebase.database.reference
    }

    private fun fetchFirebase(lat: Double, lng: Double, radius: Float, period: String) {
        // [START single_value_read]
        database.child("events").child("LVA").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get user value
                    val markers = dataSnapshot.getValue<List<ClusterMarker>>()
                    markers?.let {
                        cacheMarkers(lat, lng, markers)
//                        cachedMarkers.addAll(markers)
                        addClusterItems(markers)
                    }
                    Log.v(TAG, "getUser:onDataChange")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException())
                    // [START_EXCLUDE]
//                    setEditingEnabled(true)
                    // [END_EXCLUDE]
                }
            })
        // [END single_value_read]
    }

    fun fetchEvents(lat: Double, lng: Double, radius: Float) {
        // check if cache already contains items
        for (cluster in cachedMarkers) {
            if (cluster.bounds.contains(lat, lng)) {
                addClusterItems(cluster.markers)
                return
            }
        }

//        fetchFirebase(lat, lng, radius, Config.period)
//        return
        viewModelJob.cancelChildren(null)
        val queryOptions = getQueryOptions(lat, lng, radius, Config.period)
        fetchEventsInternal(lat, lng, queryOptions, 1)
    }

    private fun cacheMarkers(lat: Double, lng: Double, markers: List<ClusterMarker>) {
        //check if cluster is already added. TODO: Switch to Dictionary
        for (cluster in cachedMarkers) {
            if (cluster.bounds.contains(lat, lng)) return
        }

        // Calculate cluster size in degrees. 1 latitude degree ~ 111.2km, 1 longitude degree ~ cos(rad(lat)) * 111.2km
        val latLength = 0.5 // 111.2km
        val lngLength = latLength / cos(toRadians(lat)) // secant = 1 / cos
        cachedMarkers.add(
            Cluster(
                Bounds(
                    latMin = (lat - latLength).roundToInt().toDouble(),
                    latMax = (lat + latLength).roundToInt().toDouble(),
                    lngMin = (lng - lngLength).roundToInt().toDouble(),
                    lngMax = (lng + lngLength).roundToInt().toDouble()
                ),
                markers = markers
            )
        )
    }

    /**
     * The Retrofit service returns a coroutine, which we await to get the result of the transaction.
     * @param filter the [EventsApiFilter] that is sent as part of the web server request
     */
    private fun fetchEventsInternal(lat: Double, lng: Double, queryOptions: MutableMap<String, String>, page: Int = 1) {
        //TODO: optimize, add cache
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            _clusterStatus.postValue(LoadingStatus.LOADING)
            try {
                queryOptions["page_number"] = page.toString()
                val listResult = EventsApi.retrofitService.getEventsAsync(queryOptions)
                val markers = listResult.events.event
                algorithm.addItems(markers)
                _clusterStatus.postValue(LoadingStatus.DONE)
                cacheMarkers(lat, lng, markers)
//                if (page < listResult.page_count.toInt())
//                    fetchEventsInternal(lat, lng, queryOptions, page.inc())
//                else
                Log.i(TAG, "All events downloaded")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading events", e)
                _clusterStatus.postValue(LoadingStatus.ERROR)
            }
        }
    }

    /** Adding query params. */
    private fun getQueryOptions(lat: Double, lng: Double, radius: Float, period: String): MutableMap<String, String> {
        val options: MutableMap<String, String> = HashMap()
        options["app_key"] = resources.getString(R.string.eventful_key)
        options["where"] = "$lat,$lng"
        options["within"] = radius.toString()
        options["date"] = period
        options["page_size"] = Config.pageSize.toString()
//        options["include"] = "categories,popularity,price" //subcategories
        options["image_sizes"] = "thumb,block250"
        return options
    }

    private fun addClusterItems(items: List<ClusterMarker>) {
        algorithm.addItems(items)
        _clusterStatus.postValue(LoadingStatus.DONE)
    }

    /** When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the Retrofit service to stop. */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}