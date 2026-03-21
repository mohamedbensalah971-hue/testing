package com.quickchat.app.data.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the Message data class.
 * Tests validation logic in the init block.
 */
class MessageTest {

    @Test
    fun `create valid message successfully`() {
        val message = Message(
            id = "msg_1",
            conversationId = "conv_1",
            senderName = "Alice",
            content = "Hello!",
            timestamp = 1000L,
            isFromMe = false
        )

        assertEquals("msg_1", message.id)
        assertEquals("conv_1", message.conversationId)
        assertEquals("Alice", message.senderName)
        assertEquals("Hello!", message.content)
        assertEquals(1000L, message.timestamp)
        assertFalse(message.isFromMe)
    }

    @Test
    fun `create message from me`() {
        val message = Message(
            id = "msg_2",
            conversationId = "conv_1",
            senderName = "Me",
            content = "Hi there",
            timestamp = 2000L,
            isFromMe = true
        )

        assertTrue(message.isFromMe)
        assertEquals("Me", message.senderName)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank id throws exception`() {
        Message(
            id = "   ",
            conversationId = "conv_1",
            senderName = "Alice",
            content = "Hello",
            timestamp = 1000L,
            isFromMe = false
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty content throws exception`() {
        Message(
            id = "msg_1",
            conversationId = "conv_1",
            senderName = "Alice",
            content = "",
            timestamp = 1000L,
            isFromMe = false
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero timestamp throws exception`() {
        Message(
            id = "msg_1",
            conversationId = "conv_1",
            senderName = "Alice",
            content = "Hello",
            timestamp = 0L,
            isFromMe = false
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative timestamp throws exception`() {
        Message(
            id = "msg_1",
            conversationId = "conv_1",
            senderName = "Alice",
            content = "Hello",
            timestamp = -100L,
            isFromMe = false
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank sender name throws exception`() {
        Message(
            id = "msg_1",
            conversationId = "conv_1",
            senderName = "",
            content = "Hello",
            timestamp = 1000L,
            isFromMe = false
        )
    }

    @Test
    fun `two messages with same data are equal`() {
        val msg1 = Message("id1", "conv1", "Alice", "Hi", 1000L, false)
        val msg2 = Message("id1", "conv1", "Alice", "Hi", 1000L, false)
        assertEquals(msg1, msg2)
    }

    @Test
    fun `two messages with different ids are not equal`() {
        val msg1 = Message("id1", "conv1", "Alice", "Hi", 1000L, false)
        val msg2 = Message("id2", "conv1", "Alice", "Hi", 1000L, false)
        assertNotEquals(msg1, msg2)
    }
}
