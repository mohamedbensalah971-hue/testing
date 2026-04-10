package com.quickchat.app.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import io.mockk.mockk
import io.mockk.every
import io.mockk.verify

class ConversationAnalyzerTest {

    private lateinit var messages: List<Message>

    @BeforeEach
    fun setUp() {
        messages = listOf(
            Message(1, 1, 1, "Hello", 1643723400),
            Message(2, 1, 2, "Hi", 1643723410),
            Message(3, 1, 1, "How are you?", 1643723420),
            Message(4, 1, 2, "I'm fine, thanks", 1643723430),
            Message(5, 1, 1, "That's great", 1643723440)
        )
    }

    @Test
    fun analyzeConversationReturnsNullWhenEmpty() {
        val result = ConversationAnalyzer.analyzeConversation(emptyList())
        assertNull(result)
    }

    @Test
    fun analyzeConversationReturnsConversationStats() {
        val result = ConversationAnalyzer.analyzeConversation(messages)
        assertNotNull(result)
        assertEquals(5, result!!.totalMessages)
    }

    @Test
    fun detectConversationPatternsReturnsEmptyMapWhenEmpty() {
        val result = ConversationAnalyzer.detectConversationPatterns(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun detectConversationPatternsReturnsPatterns() {
        val result = ConversationAnalyzer.detectConversationPatterns(messages)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun analyzeConversationReturnsCorrectAverageResponseTime() {
        val result = ConversationAnalyzer.analyzeConversation(messages)
        assertNotNull(result)
        val averageResponseTime = result!!.averageResponseTime
        assertTrue(averageResponseTime > 0)
    }

    @Test
    fun analyzeConversationReturnsCorrectMostActiveHour() {
        val result = ConversationAnalyzer.analyzeConversation(messages)
        assertNotNull(result)
        val mostActiveHour = result!!.mostActiveHour
        assertTrue(mostActiveHour >= 0 && mostActiveHour < 24)
    }
}