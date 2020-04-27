package com.psvoid.whappens.map

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.datatransport.runtime.logging.Logging
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.psvoid.whappens.R
import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.model.StreetEvent
import com.psvoid.whappens.network.EventsApi
import com.psvoid.whappens.network.LoadingStatus
import com.psvoid.whappens.utils.HelperItemReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

enum class EventsApiFilter(val value: String) { THEATRE("theatre"), MUSIC("music"), ALL("all") }

class MapViewModel : ViewModel() {

    val algorithm = NonHierarchicalViewBasedAlgorithm<ClusterMarker>(0, 0)

    /* Read local JSON file */
    fun readResourceJson(resources: Resources) {
        val inputStream = resources.openRawResource(R.raw.event1)
        val items = HelperItemReader().readSerializable(inputStream)
        addClusterItems(items)
    }

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadingStatus>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private val viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
//        getEvents(EventsApiFilter.ALL)
    }

    /**
     * Gets filtered Mars real estate property information from the Mars API Retrofit service and
     * updates the [StreetEvent] [List] and [EventsApiStatus] [LiveData]. The Retrofit service
     * returns a coroutine Deferred, which we await to get the result of the transaction.
     * @param filter the [EventsApiFilter] that is sent as part of the web server request
     */
    fun getEventsAsync(filter: EventsApiFilter) {
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            _status.value = LoadingStatus.LOADING
            try {
                // this will run on a thread managed by Retrofit
                val listResult = EventsApi.retrofitService.getEventsAsync()
                _status.value = LoadingStatus.DONE
                addClusterItems(listResult.events.event)
            } catch (e: Exception) {
                Logging.e("MapViewModel", "Error loading events", e)
                _status.value = LoadingStatus.ERROR
            }
        }
    }

    private fun addClusterItems(items: List<ClusterMarker>) {
//        algorithm.lock()
        algorithm.addItems(items)
//        algorithm.unlock()
//        clusterManager.cluster()
    }

    /** When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the Retrofit service to stop. */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}