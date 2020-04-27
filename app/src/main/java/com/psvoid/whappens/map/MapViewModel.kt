package com.psvoid.whappens.map

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.psvoid.whappens.R
import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.model.StreetEvent
import com.psvoid.whappens.model.adapters.Eve
import com.psvoid.whappens.network.EventsApi
import com.psvoid.whappens.utils.HelperItemReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

//    // The internal MutableLiveData that stores the status of the most recent request
//    private val _status = MutableLiveData<EventsApiStatus>()
//
//    // The external immutable LiveData for the request status
//    val status: LiveData<EventsApiStatus>
//        get() = _status

    // Internally, we use a MutableLiveData, because we will be updating the List of StreetEvent
    // with new values
//    private val _properties = MutableLiveData<List<StreetEvent>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
//    val properties: LiveData<List<StreetEvent>>
//        get() = _properties

    // Internally, we use a MutableLiveData to handle nav_graph to the selected property
//    private val _navigateToSelectedProperty = MutableLiveData<StreetEvent>()

    // The external immutable LiveData for the nav_graph property
//    val navigateToSelectedProperty: LiveData<StreetEvent>
//        get() = _navigateToSelectedProperty

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    private val _markers = MutableLiveData<List<StreetEvent>>()
    val markers: LiveData<List<StreetEvent>>
        get() = _markers

    init {
        getEvents(EventsApiFilter.ALL)
    }

    /**
     * Gets filtered Mars real estate property information from the Mars API Retrofit service and
     * updates the [StreetEvent] [List] and [EventsApiStatus] [LiveData]. The Retrofit service
     * returns a coroutine Deferred, which we await to get the result of the transaction.
     * @param filter the [EventsApiFilter] that is sent as part of the web server request
     */
    private fun getEvents(filter: EventsApiFilter) {
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
//            val getEventsDeferred = EventsApi.retrofitService.getEventsAsync()
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

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}

//data class MarsProperty(
//    val id: String,
//    // used to map img_src from the JSON to imgSrcUrl in our class
//    @Json(name = "img_src") val imgSrcUrl: String,
//    val type: String,
//    val price: Double
//)

@Serializable
data class MarsProperty(
    val id: String,
    // used to map img_src from the JSON to imgSrcUrl in our class
    @SerialName("img_src") val imgSrcUrl: String,
    val type: String,
    val price: Double)



