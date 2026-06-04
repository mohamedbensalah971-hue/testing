package com.quickchat.app.util

import com.quickchat.app.util.TimeFormatter


import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import java.util.Calendar

class TimeFormatterTest {

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun formatRelative_JustNow() {
        val timestamp = System.currentTimeMillis() - 30_000
        val result = TimeFormatter.formatRelative(timestamp)
        assertEquals("Just now", result)
    }

    @Test
    fun formatRelative_XmAgo() {
        val timestamp = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5)
        val result = TimeFormatter.formatRelative(timestamp)
        assertEquals("5m ago", result)
    }

    @Test
    fun formatRelative_XhAgo() {
        val timestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2)
        val result = TimeFormatter.formatRelative(timestamp)
        assertEquals("2h ago", result)
    }

    @Test
    fun formatRelative_Yesterday() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val timestamp = calendar.timeInMillis
        val result = TimeFormatter.formatRelative(timestamp)
        assertEquals("Yesterday", result)
    }

    @Test
    fun formatTime() {
        val timestamp = System.currentTimeMillis()
        val result = TimeFormatter.formatTime(timestamp)
        val expected = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
        assertEquals(expected, result)
    }
}