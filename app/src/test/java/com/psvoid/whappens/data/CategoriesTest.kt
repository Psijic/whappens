package com.psvoid.whappens.data

import com.psvoid.whappens.data.Categories.getCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoriesTest {

    @Test
    fun getCategoryTest() {
        var result = getCategory(listOf("business", "performing_arts"))
        assertEquals(result.name, "Business, Networking")
        result = getCategory(listOf("performing_arts"))
        assertEquals(result.name, "Performing Arts")
        result = getCategory(listOf("Unknown category"))
        assertEquals(result.name, "Other")
    }
}