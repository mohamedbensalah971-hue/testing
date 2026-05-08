package com.quickchat.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Utility for formatting timestamps into human-readable strings.
 */
object TimeFormatter {

    /**
     * Formats a timestamp into a short display string.
     * - Less than 1 minute ago: "Just now"
     * - Less than 60 minutes ago: "Xm ago"
     * - Less than 24 hours ago: "Xh ago"
     * - Yesterday: "Yesterday"
     * - Otherwise: "MMM dd"
     */
    fun formatRelative(timestamp: Long): String {
        val now = System.currentTimeMillis()
        if (timestamp > now || timestamp <= 0) return ""

        val diff = now - timestamp

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "${minutes}m ago"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "${hours}h ago"
            }
            isYesterday(timestamp) -> "Yesterday"
            else -> {
                val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
        }
    }

    /**
     * Formats a timestamp into a time-only string for chat bubbles (e.g. "2:35 PM").
     */
    fun formatTime(timestamp: Long): String {
        if (timestamp <= 0) return ""
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun isYesterday(timestamp: Long): Boolean {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
        return cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)
    }
}
//test jacoco final final now yes yes test now try aloo