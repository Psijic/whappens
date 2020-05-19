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

    private val firebaseDb: DatabaseReference
    private val markerRepo: MarkerRepository
    private val countriesRepo: CountriesRepository
    private val allMarkers: MutableMap<String, List<ClusterMarker>> = mutableMapOf()

    private val mApplication: Application = application
//    private val markersObserver = { markers: List<ClusterMarker> -> getMarkers(markers) }

    init {
//        Firebase.database.setPersistenceEnabled(true) // TODO: Move
        firebaseDb = Firebase.database.reference

        val markerDao = AppDatabase.getInstance(application).markerDao
        markerRepo = MarkerRepository(markerDao)
        val countriesDao = AppDatabase.getInstance(application).countriesDao
        countriesRepo = CountriesRepository(countriesDao)

        // Check if Android database have actual markers for current countries. If not, fetch them.
        viewModelScope.launch(Dispatchers.IO) {
            for (countryName in Config.countries) {
                val markers = markerRepo.getMarkersByCountry(countryName)
                allMarkers[countryName] = markers
                val countryTimestamp = countriesRepo.getByCountry(countryName)
                val timestamp = countryTimestamp?.timestamp ?: 0

                // Check if the database data is cleared/broken or outdated
                Log.i(TAG, "Getting database data, timestamp: $timestamp")
                if (markers.isNullOrEmpty() || timestamp < Config.launchTime - Config.cacheRefreshTime) {
                    fetchFirebase(countryName, Config.period)
                } else {
                    addClusterItems(markers)
                }
            }
        }
    }

    private fun fetchFirebase(countryName: String, period: String) {
        _clusterStatus.postValue(LoadingStatus.LOADING)
        firebaseDb.child("events").child(countryName).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val markers = dataSnapshot.getValue<List<ClusterMarker>>()
                    if (markers.isNullOrEmpty()) {
                        // Add cached markers if firebase doesn't work well and return empty list.
                        addClusterItems(allMarkers[countryName])
                    } else {
                        addClusterItems(markers)
                        saveMarkers(countryName, markers)
                    }
                    Log.v(TAG, "fetch Firebase markers: onDataChange")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "fetch Firebase markers: onCancelled", databaseError.toException())
                    addClusterItems(allMarkers[countryName])
                }
            })
    }

    /** Add specific settings like event types selected */
    fun fetchEvents(lat: Double, lng: Double, radius: Float) {

    }

    private fun saveMarkers(countryName: String, markers: List<ClusterMarker>) {
        insertMarkers(markers)
        insertCountry(CountryData(countryName))
    }

    /** Launching a new coroutine to insert the data in a non-blocking way */
    private fun insertMarkers(markers: List<ClusterMarker>) =
        viewModelScope.launch(Dispatchers.IO) { markerRepo.insert(markers) }

    private fun insertCountries(countries: List<CountryData>) =
        viewModelScope.launch(Dispatchers.IO) { countriesRepo.insert(countries) }

    private fun insertCountry(country: CountryData) =
        viewModelScope.launch(Dispatchers.IO) { countriesRepo.insert(country) }

    fun fetchEventsByHttp(lat: Double, lng: Double, radius: Float) {
        viewModelJob.cancelChildren(CancellationException("Updated"))
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

    private fun addClusterItems(items: List<ClusterMarker>?) {
        if (items.isNullOrEmpty()) {
            _clusterStatus.postValue(LoadingStatus.ERROR)
        } else {
            algorithm.addItems(items)
            _clusterStatus.postValue(LoadingStatus.DONE)
        }
    }

    /** When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the Retrofit service to stop. */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
//        allMarkers.removeObserver(markersObserver)
    }
}