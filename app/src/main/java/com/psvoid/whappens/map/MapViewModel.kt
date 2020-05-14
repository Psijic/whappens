package com.psvoid.whappens.map

import android.app.Application
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
import com.psvoid.whappens.database.MarkerDatabase
import com.psvoid.whappens.database.MarkerDatabaseDao
import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.model.Country
import com.psvoid.whappens.network.Config
import com.psvoid.whappens.network.EventsApi
import com.psvoid.whappens.network.LoadingStatus
import com.psvoid.whappens.utils.HelperItemReader
import kotlinx.coroutines.*
import kotlin.collections.set

/** Possible to inline factory https://www.albertgao.xyz/2018/04/13/how-to-add-additional-parameters-to-viewmodel-via-kotlin */
class MapViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) return MapViewModel(app) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MapViewModel(private val app: Application) : ViewModel() {
    companion object {
        const val TAG = "MapViewModel"

        data class Cluster(
//            val bounds: Bounds,
            val markers: List<ClusterMarker>,
            val timestamp: Long = System.currentTimeMillis()
        )

//        data class Bounds(
//            val latMin: Double,
//            val latMax: Double,
//            val lngMin: Double,
//            val lngMax: Double
//        ) {
//            fun contains(lat: Double, lng: Double) = lat in latMin..latMax && lng in lngMin..lngMax
//        }
    }

    val algorithm = NonHierarchicalViewBasedAlgorithm<ClusterMarker>(0, 0)


    // The internal MutableLiveData that stores the status of the most recent request
    private val _clusterStatus = MutableLiveData<LoadingStatus>()
    val clusterStatus: LiveData<LoadingStatus>
        get() = _clusterStatus

    // Create a Coroutine scope using a job to be able to cancel when needed
    private val viewModelJob = Job()

    // Coroutine runs using the IO dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private var firebaseDb: DatabaseReference

    private val cachedMarkers: MutableMap<String, Cluster> = mutableMapOf()

    private val markerDb: MarkerDatabaseDao

    init {
        Firebase.database.setPersistenceEnabled(true)
        firebaseDb = Firebase.database.reference
        markerDb = MarkerDatabase.getInstance(app).markerDatabaseDao
    }

    /* Read local JSON file */
    fun readResourceJson() {
        val inputStream = app.resources.openRawResource(R.raw.event_utah)
        val items = HelperItemReader().readSerializable(inputStream)
        addClusterItems(items)
    }

    private fun fetchFirebase(countryName: String, period: String) {
        // [START single_value_read]
        firebaseDb.child("events").child(countryName).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get user value
                    val markers = dataSnapshot.getValue<List<ClusterMarker>>()
                    markers?.let {
                        coroutineScope.launch {
                            cacheMarkers(countryName, markers)
                        }
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

    fun fetchEventsByCountryList(countries: List<Country>) {
        for (country in countries) {
            val countryName = country.toString()
            if (!cachedMarkers.containsKey(countryName) /*|| cachedMarkers[countryName].timestamp > time*/) // TODO: refresh time
                fetchFirebase(countryName, Config.period)
        }
    }

    fun refreshMarkers() {

    }

    fun fetchEvents(lat: Double, lng: Double, radius: Float) {
        cachedMarkers.forEach { addClusterItems(it.value.markers) } // TODO: Add specific settings like event types selected
        val all = markerDb.getAllMarkers().value.orEmpty()
        // check if cache already contains items
//        cachedMarkers["USA"]?.let {
//            addClusterItems(it.markers)
//            return
//        }

//        fetchFirebase(lat, lng, radius, Config.period)
//        viewModelJob.cancelChildren(null)
//        val queryOptions = getQueryOptions(lat, lng, 25f, Config.period)
//        fetchEventsInternal(lat, lng, queryOptions, 1)
    }

    private suspend fun cacheMarkers(country: String, markers: List<ClusterMarker>) {
        cachedMarkers[country] = Cluster(markers = markers)

        withContext(Dispatchers.IO) {
//            for (marker in markers)
//                markerDb.insert(marker)
//            markerDb.insert(markers.forEach { return@forEach })
            markerDb.insert(markers)
        }
    }

    /**
     * The Retrofit service returns a coroutine, which we await to get the result of the transaction.
     * @param filter the [EventsApiFilter] that is sent as part of the web server request
     */
    private fun fetchEventsInternal(lat: Double, lng: Double, queryOptions: MutableMap<String, String>, page: Int = 1) {
        coroutineScope.launch {
            // Get the object for Retrofit request
            _clusterStatus.postValue(LoadingStatus.LOADING)
            try {
                queryOptions["page_number"] = page.toString()
                val listResult = EventsApi.retrofitService.getEventsAsync(queryOptions)
                val markers = listResult.events.event
                algorithm.addItems(markers)
                _clusterStatus.postValue(LoadingStatus.DONE)
//                cacheMarkers(lat, lng, markers)
                if (page < listResult.page_count.toInt())
                    fetchEventsInternal(lat, lng, queryOptions, page.inc())
                else
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
        options["app_key"] = app.resources.getString(R.string.eventful_key)
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