package com.psvoid.whappens.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.IgnoreExtraProperties
import com.google.maps.android.clustering.ClusterItem
import com.psvoid.whappens.utils.DateUtils.getMonthName
import kotlinx.serialization.Serializable

/** A data class that implements the [ClusterItem] interface so it can be clustered. */
@IgnoreExtraProperties
@Serializable
@Entity(tableName = "markers_table")
data class ClusterMarker(
//    @SerialName("title")
    val name: String = "Event",
    @PrimaryKey val id: String = "",
    val url: String? = null,
    val locale: String? = null,
//    @SerialName("image")
//    @TypeConverters(ImagesConverter::class)
    val image: String? = null, // 250*250 or 4*3 ratio
//    @SerialName("start_time") @PropertyName("start_time") @ColumnInfo(name = "start_time")
    val start_time: String = "",
    val stop_time: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String? = null,
    val price: String? = null,
//    val categories: Categories? = null,
    @TypeConverters(CategoriesConverter::class)
    val categories: List<String> = listOf("other"),
//    var categories: ArrayList<String> = arrayListOf(),
//    val popularity: String? = null,
    val popularity: Int? = null,
//    @SerialName("venue_address")
    val address: String? = null,
    val country_name: String = "",
    val country_code: String = "",
    val city: String = "",
    val region: String? = null,
//    val performers: Performer
    val place: String? = null

    // Additional stuff
//    var isSelected: Boolean = false,
//    var isFavorite: Boolean = false

) : ClusterItem {


    override fun getPosition() = LatLng(latitude, longitude)
    override fun getTitle() = name
    override fun getSnippet() = address

    fun getCategory(): String = categories.joinToString()
    fun getFullAddress(): String = listOfNotNull(address, place).joinToString("; ")


    fun getCategoryColor() = Categories.getCategory(this).color
    fun getCategoryName() = Categories.getCategory(this).name

    /**Convert date like this: "2020-05-20 20:30:00" to "17:00 - 19:00, 04 Dec" */
    fun getTimePeriod(): String {
        //TODO: Add conditions for multiple days
        val sTime = start_time.substring(11, 16)
        val month = getMonthName(start_time.substring(5, 7).toInt())
        val sDate = "${start_time.substring(8, 10)} $month"

        if (!stop_time.isNullOrEmpty()) {
            sTime.plus(" - ${stop_time.substring(11, 16)}")
            val dateEnd = stop_time.substring(5, 10)
//            if (sDate != dateEnd) sDate.plus(" - $dateEnd")
        }

        return "$sTime, $sDate"
    }


    //@Serializable
    //data class Performer(val performer: List<IdName>)
}

class CategoriesConverter {
    private val separator = ", "

    @TypeConverter
    fun fromData(value: List<String>): String = value.joinToString(separator)

    @TypeConverter
    fun toData(data: String): List<String> = data.split(separator)
}

object Categories {
    private val categories: MutableMap<String, Category> = mutableMapOf()

    init {
        categories["family_fun_kids"] = Category("Family", BitmapDescriptorFactory.HUE_RED)
        categories["learning_education"] = Category("Education", BitmapDescriptorFactory.HUE_AZURE)
        categories["other"] = Category("Other", BitmapDescriptorFactory.HUE_ROSE)
        categories["sports"] = Category("Sports", BitmapDescriptorFactory.HUE_BLUE)
        categories["performing_arts"] = Category("Performing Arts", BitmapDescriptorFactory.HUE_VIOLET)
        categories["science"] = Category("Science", BitmapDescriptorFactory.HUE_CYAN)
        categories["business"] = Category("Business, Networking", BitmapDescriptorFactory.HUE_MAGENTA)
        categories["food"] = Category("Food", BitmapDescriptorFactory.HUE_GREEN)
        categories["singles_social"] = Category("Nightlife, Singles", BitmapDescriptorFactory.HUE_VIOLET)
        categories["fundraisers"] = Category("Fundraising, Charity", BitmapDescriptorFactory.HUE_MAGENTA)
        categories["technology"] = Category("Technology", BitmapDescriptorFactory.HUE_CYAN)
        categories["comedy"] = Category("Comedy", BitmapDescriptorFactory.HUE_VIOLET)
        categories["holiday"] = Category("Holiday", BitmapDescriptorFactory.HUE_YELLOW)
        categories["music"] = Category("Music", BitmapDescriptorFactory.HUE_VIOLET)
        categories["politics_activism"] = Category("Politics", BitmapDescriptorFactory.HUE_YELLOW)
        categories["festivals_parades"] = Category("Festivals", BitmapDescriptorFactory.HUE_YELLOW)
        categories["movies_film"] = Category("Movie", BitmapDescriptorFactory.HUE_VIOLET)
        categories["support"] = Category("Health", BitmapDescriptorFactory.HUE_BLUE)
        categories["outdoors_recreation"] = Category("Outdoors, Recreation", BitmapDescriptorFactory.HUE_BLUE)
        categories["attractions"] = Category("Museums, Attractions", BitmapDescriptorFactory.HUE_VIOLET)
        categories["conference"] = Category("Conferences, Tradeshows", BitmapDescriptorFactory.HUE_ORANGE)
        categories["community"] = Category("Neighborhood", BitmapDescriptorFactory.HUE_YELLOW)
    }

    fun getCategory(item: ClusterMarker): Category = Categories.categories.getValue(item.categories.firstOrNull() ?: "other")
}