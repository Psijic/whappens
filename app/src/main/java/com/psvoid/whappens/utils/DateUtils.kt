package com.psvoid.whappens.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    /** Normal data utilities available only since API 26 */
    fun getMonthName(month: Int): String {
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        return months[month - 1]
    }

    fun getDateString(time: Long): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(time * 1000L)
}