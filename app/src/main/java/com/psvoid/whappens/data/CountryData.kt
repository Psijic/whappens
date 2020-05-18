package com.psvoid.whappens.data

import androidx.room.Entity

@Entity(tableName = "countries_table")
data class CountryData(
    val country_code: String = "",
    val timestamp: Long = System.currentTimeMillis()
)