package com.psvoid.whappens.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.psvoid.whappens.R
import com.psvoid.whappens.data.*
import com.psvoid.whappens.network.Config
import com.psvoid.whappens.network.EventsApi
import kotlinx.coroutines.*
import kotlin.collections.set

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private companion object {
        const val TAG = "MapViewModel"
    }

    val algorithm = NonHierarchicalViewBasedAlgorithm<ClusterMarker>(0, 0)

    /** The internal MutableLiveData that stores the status of the most recent request */
    private val _clusterStatus = MutableLiveData<LoadingStatus>()
    val clusterStatus: LiveData<LoadingStatus>
        get() = _clusterStatus

    /** Create a Coroutine scope using a job to be able to cancel when needed */
    private val viewModelJob = Job()

    /** Coroutine runs using the IO dispatcher */
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private val markerDao: MarkerDao
    private val firebaseDb: DatabaseReference
    private val repository: MarkerRepository
    private val allMarkers: LiveData<List<ClusterMarker>>
    private val mApplication: Application = application
    private val markersObserver = { markers: List<ClusterMarker> -> getMarkers(markers) }

    init {
        Firebase.database.setPersistenceEnabled(true)
        firebaseDb = Firebase.database.reference
        markerDao = MarkerDatabase.getInstance(application).markerDatabaseDao
        repository = MarkerRepository(markerDao)
        allMarkers = repository.getAllMarkers()

        // Check if Android database have actual data for current countries. If not, fetch them.
        // Use observeForever https://stackoverflow.com/questions/47515997/observing-livedata-from-viewmodel
        // or some options https://developer.android.com/topic/libraries/architecture/coroutines
        allMarkers.observeForever(markersObserver)
    }

    private fun getMarkers(markers: List<ClusterMarker>) {
        if (markers.isNullOrEmpty() /*|| markers.timestamp > time*/) { // TODO: refresh outdated markers
            fetchEventsByCountryList(Config.countries)
        } else {
            //Need to check if there are no outdated data and delete old. So, store markers in tables by date
            for (country in Config.countries) {
                addClusterItems(markers)
            }
        }
    }

    private fun fetchFirebase(countryName: String, period: String) {
        firebaseDb.child("events").child(countryName).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val markers = dataSnapshot.getValue<List<ClusterMarker>>()
                    markers?.let {
                        insertMarkers(markers)
                        addClusterItems(markers)
                    }
                    Log.v(TAG, "getUser:onDataChange")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException())
                }
            })
    }

    private fun fetchEventsByCountryList(countries: List<Country>) {
        for (country in countries) {
            fetchFirebase(country.toString(), Config.period)
        }
    }

    /** Add specific settings like event types selected */
    fun fetchEvents(lat: Double, lng: Double, radius: Float) {

    }

    /** Launching a new coroutine to insert the data in a non-blocking way */
    fun insertMarkers(markers: List<ClusterMarker>) = viewModelScope.launch(Dispatchers.IO) { repository.insert(markers) }

    fun fetchEventsByHttp(lat: Double, lng: Double, radius: Float) {
        viewModelJob.cancelChildren(null)
        val queryOptions = getQueryOptions(lat, lng, radius, Config.period)
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
            _clusterStatus.postValue(LoadingStatus.LOADING)
            try {
                queryOptions["page_number"] = page.toString()
                val listResult = EventsApi.retrofitService.getEventsAsync(queryOptions)
                val markers = listResult.events.event
                addClusterItems(markers)
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
        options["app_key"] = mApplication.resources.getString(R.string.eventful_key)
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
        allMarkers.removeObserver(markersObserver)
    }
}