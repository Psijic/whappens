package com.psvoid.whappens.data.network

import com.psvoid.whappens.data.CountryData
import org.junit.Test

import org.junit.Assert.*
import java.util.*

class ConfigTest {

    @Test
    fun timeTest() {
        val previousDayTimestamp =  Config.launchTime - Config.cacheRefreshTime
        val today = Date(Config.launchTime)
        val previousDay = Date(previousDayTimestamp)
//        assertTrue(previousDay)
        val country = CountryData("USA")
        assertTrue(country.timestamp > System.currentTimeMillis() - 100)
        val countryDate = Date(country.timestamp)
    }
}