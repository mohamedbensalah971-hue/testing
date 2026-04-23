package com.quickchat.app.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.Calendar
import java.util.Date

class TimeFormatterTest {

    private lateinit var calendar: Calendar

    @BeforeEach
    fun setUp() {
        calendar = Calendar.getInstance()
    }

    @Test
    fun testFormatRelativeJustNow() {
        val timestamp = System.currentTimeMillis()
        val result = TimeFormatter.formatRelative(timestamp)
        assertEquals("Just now", result)
    }

    @Test
    fun testFormatRelativeMinutesAgo() {
        val timestamp = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5)
        val result = TimeFormatter.formatRelative(timestamp)
        assertEquals("5m ago", result)
    }

    @Test
    fun testFormatRelativeHoursAgo() {
        val timestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(5)
        val result = TimeFormatter.formatRelative(timestamp)
        assertEquals("5h ago", result)
    }

    @Test
    fun testFormatRelativeYesterday() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val timestamp = calendar.timeInMillis
        val result = TimeFormatter.formatRelative(timestamp)
        assertEquals("Yesterday", result)
    }

    @Test
    fun testFormatTime() {
        val timestamp = System.currentTimeMillis()
        val result = TimeFormatter.formatTime(timestamp)
        val expected = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
        assertEquals(expected, result)
    }

    @Test
    fun testFormatRelativeInvalidTimestamp() {
        val timestamp = 0L
        val result = TimeFormatter.formatRelative(timestamp)
        assertEquals("", result)
    }
}