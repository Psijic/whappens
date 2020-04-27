package com.psvoid.whappens.map

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.psvoid.whappens.R
import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.model.StreetEvent
import com.psvoid.whappens.network.EventsApi
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

    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

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
    fun getEvents(filter: EventsApiFilter) {
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            try {
//                _status.value = EventsApiStatus.LOADING
                // this will run on a thread managed by Retrofit
                val listResult = EventsApi.retrofitService.getEventsAsync()
//                _status.value = EventsApiStatus.DONE
                _response.value = "Success: events retrieved"
                addClusterItems(listResult.events.event)
            } catch (e: Exception) {
                _response.value = "Failure: ${e.message}"
            }
        }
    }

    private fun addClusterItems(items: List<ClusterMarker>) {
        algorithm.lock()
        algorithm.addItems(items)
        algorithm.unlock()
    }

    /** When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the Retrofit service to stop. */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}