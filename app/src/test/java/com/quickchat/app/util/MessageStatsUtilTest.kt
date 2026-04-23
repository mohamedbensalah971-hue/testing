package com.quickchat.app.util

import com.quickchat.app.data.model.Message
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class MessageStatsUtilTest {

    private lateinit var messages: List<Message>

    @BeforeEach
    fun setUp() {
        messages = listOf(
            Message(conversationId = "1", senderName = "John"),
            Message(conversationId = "1", senderName = "John"),
            Message(conversationId = "2", senderName = "Jane")
        )
    }

    @Test
    fun `countMessagesByConversation returns correct count`() {
        val result = MessageStatsUtil.countMessagesByConversation(messages)
        assertEquals(2, result["1"])
        assertEquals(1, result["2"])
    }

    @Test
    fun `countMessagesByConversation returns empty map when input is empty`() {
        val result = MessageStatsUtil.countMessagesByConversation(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `countMessagesBySender returns correct count`() {
        val result = MessageStatsUtil.countMessagesBySender(messages)
        assertEquals(2, result["John"])
        assertEquals(1, result["Jane"])
    }

    @Test
    fun `countMessagesBySender returns empty map when input is empty`() {
        val result = MessageStatsUtil.countMessagesBySender(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `countMessagesByConversation handles null input`() {
        val result = MessageStatsUtil.countMessagesByConversation(null)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `countMessagesBySender handles null input`() {
        val result = MessageStatsUtil.countMessagesBySender(null)
        assertTrue(result.isEmpty())
    }
}