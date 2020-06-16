package com.psvoid.whappens.data

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    val name: String = "",
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
//    @TypeConverters(CategoriesConverter::class)
//    val categories: List<IdName>? = null,
//    val popularity: String? = null,
    val popularity: Int? = null,
//    @SerialName("venue_address")
    val address: String? = null,
    val country_name: String = "",
    val country_code: String = "",
    val city: String = "",
    val region: String? = null
//    val performers: Performer
//    val place: Place,

) : ClusterItem {

    override fun getPosition() = LatLng(latitude, longitude)
    override fun getTitle() = name
    override fun getSnippet() = address

    @Serializable
    data class Categories(val category: List<IdName>)

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

//class ImagesConverter {
//    @TypeConverter
//    fun fromImages(images: ClusterMarker.Images?): String {
//        return images?.block250.toString()
//    }
//
//    @TypeConverter
//    fun toImages(data: String): ClusterMarker.Images {
//        val list = data.split(", ")
//        return ClusterMarker.Images(
//            block250 = EventImage(
//                list[0],
//                list[1],
//                list[2]
//            )
//        )
//    }
//}
//
//class CategoriesConverter {
//    @TypeConverter
//    fun fromData(value: List<IdName>): String {
//        return value.toString()
//    }
//
//    @TypeConverter
//    fun toData(data: String): List<IdName> {
//        val list = data.split(" ")
//        return listOf()
//    }
//}



