package com.psvoid.whappens.map

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

/** Possible to inline factory https://www.albertgao.xyz/2018/04/13/how-to-add-additional-parameters-to-viewmodel-via-kotlin */
class MapViewModelFactory(private val resources: Resources) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) return MapViewModel(resources) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MapViewModel(private val resources: Resources) : ViewModel() {
    val algorithm = NonHierarchicalViewBasedAlgorithm<ClusterMarker>(0, 0)

    /* Read local JSON file */
    fun readResourceJson() {
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
    fun getEventsAsync(filter: EventsApiFilter, lat: Double, long: Double, radius: Float) {
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            _clusterStatus.value = LoadingStatus.LOADING
            try {
                // This will run on a thread managed by Retrofit.
                val listResult = EventsApi.retrofitService.getEventsAsync(getQueryOptions(lat, long, radius))
                addClusterItems(listResult.events.event)
            } catch (e: Exception) {
                Logging.e("MapViewModel", "Error loading events", e)
                _clusterStatus.value = LoadingStatus.ERROR
            }
        }
    }

    /** Adding query params. */
    private fun getQueryOptions(lat: Double, long: Double, radius:Float): MutableMap<String, String> {
        val options: MutableMap<String, String> = HashMap()
        options["where"] = "$lat,$long"
        options["within"] = radius.toString()
        options["date"] = "Future"
        options["page_size"] = "10"
        options["app_key"] = resources.getString(R.string.eventful_key)
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