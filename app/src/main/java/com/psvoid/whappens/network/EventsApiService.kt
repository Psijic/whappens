package com.psvoid.whappens.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.psvoid.whappens.model.StreetEvent
import com.psvoid.whappens.model.adapters.Eve
import kotlinx.coroutines.Deferred
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.QueryMap

enum class LoadingStatus { LOADING, ERROR, DONE }

/** A public interface that exposes the [getEventsAsync] method */
interface EventsApiService {
    /**
     * Returns a Coroutine [Deferred] [List] of [StreetEvent] which can be fetched with await() if in a Coroutine scope.
     * The @GET annotation indicates that endpoint will be requested with the GET HTTP method
     */
    @GET("?app_key=hFc7MXpW2X4ZnCqr&include=categories,subcategories,popularity,price&image_sizes=thumb,block250")
    suspend fun getEventsAsync(@QueryMap(encoded = false) options: Map<String, String>): Eve.Events
//    suspend fun getEventsAsync(@Query("where") location: String): Eve.Events
}

/** A public Api object that exposes the lazy-initialized Retrofit service */
object EventsApi {
    private const val BASE_URL = "https://api.eventful.com/json/events/search/"
    private val contentType: MediaType = MediaType.get("application/json")
    private val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true, isLenient = true))
    private val logLevel = if (Config.logs) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
    private val logging: Interceptor = HttpLoggingInterceptor().setLevel(logLevel)
    private val httpClient = OkHttpClient.Builder().addInterceptor(logging).build()

    /** Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter. */
    private val retrofit = Retrofit.Builder()
//        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(json.asConverterFactory(contentType))
        .baseUrl(BASE_URL)
        .client(httpClient)
        .build()

    val retrofitService: EventsApiService by lazy { retrofit.create(EventsApiService::class.java) }
}
