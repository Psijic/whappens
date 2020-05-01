package com.psvoid.whappens.map

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.psvoid.whappens.R
import com.psvoid.whappens.model.ClusterMarker
import com.psvoid.whappens.network.Config
import com.psvoid.whappens.network.EventsApi
import com.psvoid.whappens.network.LoadingStatus
import com.psvoid.whappens.utils.HelperItemReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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

    private var getEventsJob: Job? = null

/*    fun getEventsAsync1(queryOptions: MutableMap<String, String>) {
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            _clusterStatus.value = LoadingStatus.LOADING
            try {
                // This will run on a thread managed by Retrofit.
                var pageNumber = 0
                var pageCount = 1
                while (pageNumber < pageCount) {
                    _clusterStatus.value = LoadingStatus.LOADING
                    pageNumber++
                    queryOptions["page_number"] = pageNumber.toString()
                    Log.v("MapViewModel", "coroutineScope $pageNumber")
                    val listResult = EventsApi.retrofitService.getEventsAsync(queryOptions)
                    pageCount = listResult.page_count.toInt()
                    algorithm.addItems(listResult.events.event)
                    _clusterStatus.value = LoadingStatus.DONE
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error loading events", e)
                _clusterStatus.value = LoadingStatus.ERROR
            }
        }
    }*/

    fun loadEvents(lat: Double, long: Double, radius: Float) {
        getEventsJob?.cancel(null) //TODO: finish job without starting a new request, not cancel
        val queryOptions = getQueryOptions(lat, long, radius, Config.period)
        loadEventsInternal(queryOptions, 1)
    }

    /**
     * The Retrofit service returns a coroutine, which we await to get the result of the transaction.
     * @param filter the [EventsApiFilter] that is sent as part of the web server request
     */
    private fun loadEventsInternal(queryOptions: MutableMap<String, String>, page: Int = 1) {
        //TODO: optimize
        getEventsJob = coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            _clusterStatus.value = LoadingStatus.LOADING
            try {
                queryOptions["page_number"] = page.toString()
                val listResult = EventsApi.retrofitService.getEventsAsync(queryOptions)
                algorithm.addItems(listResult.events.event)
                _clusterStatus.value = LoadingStatus.DONE
                if (page < listResult.page_count.toInt())
                    loadEventsInternal(queryOptions, page.inc())
                else
                    Log.i("MapViewModel", "All events downloaded")
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error loading events", e)
                _clusterStatus.value = LoadingStatus.ERROR
            }
        }
    }

    /** Adding query params. */
    private fun getQueryOptions(lat: Double, long: Double, radius: Float, period: String): MutableMap<String, String> {
        val options: MutableMap<String, String> = HashMap()
        options["app_key"] = resources.getString(R.string.eventful_key)
        options["where"] = "$lat,$long"
        options["within"] = radius.toString()
        options["date"] = period
        options["page_size"] = Config.pageSize.toString()
//        options["include"] = "categories,popularity,price" //subcategories
        options["image_sizes"] = "thumb,block250"
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