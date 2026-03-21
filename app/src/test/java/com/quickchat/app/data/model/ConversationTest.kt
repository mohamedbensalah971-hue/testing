package com.quickchat.app.data.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the Conversation data class.
 * Tests validation logic in the init block.
 */
class ConversationTest {

    @Test
    fun `create valid conversation successfully`() {
        val conversation = Conversation(
            id = "conv_1",
            contactName = "Alice Martin",
            lastMessage = "Hello!",
            lastMessageTimestamp = 1000L,
            unreadCount = 2,
            avatarInitial = 'A'
        )

        assertEquals("conv_1", conversation.id)
        assertEquals("Alice Martin", conversation.contactName)
        assertEquals("Hello!", conversation.lastMessage)
        assertEquals(1000L, conversation.lastMessageTimestamp)
        assertEquals(2, conversation.unreadCount)
        assertEquals('A', conversation.avatarInitial)
    }

    @Test
    fun `default unread count is zero`() {
        val conversation = Conversation(
            id = "conv_1",
            contactName = "Alice",
            lastMessage = "Hi",
            lastMessageTimestamp = 1000L,
            avatarInitial = 'A'
        )

        assertEquals(0, conversation.unreadCount)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank id throws exception`() {
        Conversation(
            id = "",
            contactName = "Alice",
            lastMessage = "Hi",
            lastMessageTimestamp = 1000L,
            avatarInitial = 'A'
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank contact name throws exception`() {
        Conversation(
            id = "conv_1",
            contactName = "  ",
            lastMessage = "Hi",
            lastMessageTimestamp = 1000L,
            avatarInitial = 'A'
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative timestamp throws exception`() {
        Conversation(
            id = "conv_1",
            contactName = "Alice",
            lastMessage = "Hi",
            lastMessageTimestamp = -1L,
            avatarInitial = 'A'
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative unread count throws exception`() {
        Conversation(
            id = "conv_1",
            contactName = "Alice",
            lastMessage = "Hi",
            lastMessageTimestamp = 1000L,
            unreadCount = -1,
            avatarInitial = 'A'
        )
    }

    @Test
    fun `conversation allows zero timestamp`() {
        val conversation = Conversation(
            id = "conv_1",
            contactName = "Alice",
            lastMessage = "",
            lastMessageTimestamp = 0L,
            avatarInitial = 'A'
        )

        assertEquals(0L, conversation.lastMessageTimestamp)
    }

    @Test
    fun `conversations with same data are equal`() {
        val c1 = Conversation("id1", "Alice", "Hi", 1000L, 0, 'A')
        val c2 = Conversation("id1", "Alice", "Hi", 1000L, 0, 'A')
        assertEquals(c1, c2)
    }

    @Test
    fun `copy updates fields correctly`() {
        val original = Conversation("id1", "Alice", "Hi", 1000L, 0, 'A')
        val updated = original.copy(unreadCount = 3, lastMessage = "New message")

        assertEquals(3, updated.unreadCount)
        assertEquals("New message", updated.lastMessage)
        assertEquals(original.id, updated.id)
        assertEquals(original.contactName, updated.contactName)
    }
}
