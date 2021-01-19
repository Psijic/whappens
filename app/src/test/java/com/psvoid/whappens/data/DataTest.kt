package com.psvoid.whappens.data

import com.psvoid.whappens.data.Categories.getCategory
import com.psvoid.whappens.utils.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class DataTest {

    @Test
    fun getCategoryTest() {
        var result = getCategory(listOf("business", "performing_arts"))
        assertEquals(result.name, "Business, Networking")
        result = getCategory(listOf("performing_arts"))
        assertEquals(result.name, "Performing Arts")
        result = getCategory(listOf("Unknown category"))
        assertEquals(result.name, "Other")
    }

    @Test
    fun dateTest() {
        val ts: Long = 1620507600
        assertEquals("2021-05-09 00:00:00", DateUtils.getDateString(ts))
    }

}