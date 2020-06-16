package com.psvoid.whappens.utils

object DateUtils{
    /** Normal data utilities available only since API 26 */
    fun getMonthName(month: Int): String {
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        return months[month - 1]
    }
}