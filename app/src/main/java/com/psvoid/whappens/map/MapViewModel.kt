package com.psvoid.whappens.map

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.datatransport.runtime.logging.Logging
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.psvoid.whappens.R
import com.psvoid.whappens.model.ClusterMarker
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
    private val _clusterStatus = MutableLiveData<LoadingStatus>()
    val clusterStatus: LiveData<LoadingStatus>
        get() = _clusterStatus

    // Create a Coroutine scope using a job to be able to cancel when needed
    private val viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * The Retrofit service returns a coroutine, which we await to get the result of the transaction.
     * @param filter the [EventsApiFilter] that is sent as part of the web server request
     */
    fun getEventsAsync(filter: EventsApiFilter, lat: Double, long: Double) {
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            _clusterStatus.value = LoadingStatus.LOADING
            try {
                // This will run on a thread managed by Retrofit.
                val listResult = EventsApi.retrofitService.getEventsAsync(getQueryOptions(lat, long))
                addClusterItems(listResult.events.event)
            } catch (e: Exception) {
                Logging.e("MapViewModel", "Error loading events", e)
                _clusterStatus.value = LoadingStatus.ERROR
            }
        }
    }

    /** Adding query params. */
    private fun getQueryOptions(lat: Double, long: Double): MutableMap<String, String> {
        val options: MutableMap<String, String> = HashMap()
        options["where"] = "$lat,$long"
        options["within"] = "25"
        options["date"] = "Future"
        options["page_size"] = "10"
        return options
    }

    private fun addClusterItems(items: List<ClusterMarker>) {
//        algorithm.lock()
        algorithm.addItems(items)
//        algorithm.unlock()
        _clusterStatus.value = LoadingStatus.DONE
    }

    /** When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the Retrofit service to stop. */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}