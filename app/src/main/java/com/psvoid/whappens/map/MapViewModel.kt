package com.psvoid.whappens.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psvoid.whappens.model.StreetEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

enum class EventsApiFilter(val value: String) { THEATRE("theatre"), MUSIC("music"), ALL("all") }

class MapViewModel : ViewModel() {

//    // The internal MutableLiveData that stores the status of the most recent request
//    private val _status = MutableLiveData<EventsApiStatus>()
//
//    // The external immutable LiveData for the request status
//    val status: LiveData<EventsApiStatus>
//        get() = _status

    // Internally, we use a MutableLiveData, because we will be updating the List of StreetEvent
    // with new values
    private val _properties = MutableLiveData<List<StreetEvent>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val properties: LiveData<List<StreetEvent>>
        get() = _properties

    // Internally, we use a MutableLiveData to handle nav_graph to the selected property
    private val _navigateToSelectedProperty = MutableLiveData<StreetEvent>()

    // The external immutable LiveData for the nav_graph property
    val navigateToSelectedProperty: LiveData<StreetEvent>
        get() = _navigateToSelectedProperty

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
            var getEventsDeferred = EventsApi.retrofitService.getEvents()
//            try {
//                _status.value = EventsApiStatus.LOADING
//                // this will run on a thread managed by Retrofit
//                val listResult = getPropertiesDeferred.await()
//                _status.value = EventsApiStatus.DONE
//                _properties.value = listResult
//            } catch (e: Exception) {
//                _status.value = EventsApiStatus.ERROR
//                _properties.value = ArrayList()
//            }
        }
    }
}



