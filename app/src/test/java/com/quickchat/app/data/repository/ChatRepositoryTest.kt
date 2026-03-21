package com.quickchat.app.data.repository

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ChatRepository.
 * Covers conversation retrieval, message operations, and edge cases.
 */
class ChatRepositoryTest {

    private lateinit var repository: ChatRepository

    @Before
    fun setUp() {
        repository = ChatRepository()
    }

    // ── Conversation Tests ───────────────────────────────────────────

    @Test
    fun `getConversations returns seeded conversations`() {
        val conversations = repository.getConversations()
        assertEquals(5, conversations.size)
    }

    @Test
    fun `getConversations returns conversations sorted by timestamp descending`() {
        val conversations = repository.getConversations()
        for (i in 0 until conversations.size - 1) {
            assertTrue(
                "Conversations should be sorted by timestamp descending",
                conversations[i].lastMessageTimestamp >= conversations[i + 1].lastMessageTimestamp
            )
        }
    }

    @Test
    fun `getConversation returns correct conversation by id`() {
        val conversation = repository.getConversation("conv_1")
        assertNotNull(conversation)
        assertEquals("Alice Martin", conversation?.contactName)
    }

    @Test
    fun `getConversation returns null for unknown id`() {
        val conversation = repository.getConversation("nonexistent")
        assertNull(conversation)
    }

    @Test
    fun `deleteConversation removes conversation`() {
        val deleted = repository.deleteConversation("conv_1")
        assertTrue(deleted)
        assertEquals(4, repository.getConversations().size)
        assertNull(repository.getConversation("conv_1"))
    }

    @Test
    fun `deleteConversation also removes messages`() {
        repository.deleteConversation("conv_1")
        val messages = repository.getMessages("conv_1")
        assertTrue(messages.isEmpty())
    }

    @Test
    fun `deleteConversation returns false for unknown id`() {
        val deleted = repository.deleteConversation("nonexistent")
        assertFalse(deleted)
        assertEquals(5, repository.getConversations().size)
    }

    // ── Message Tests ────────────────────────────────────────────────

    @Test
    fun `getMessages returns seeded messages for conv_1`() {
        val messages = repository.getMessages("conv_1")
        assertEquals(5, messages.size)
    }

    @Test
    fun `getMessages returns messages sorted by timestamp ascending`() {
        val messages = repository.getMessages("conv_1")
        for (i in 0 until messages.size - 1) {
            assertTrue(
                "Messages should be sorted by timestamp ascending",
                messages[i].timestamp <= messages[i + 1].timestamp
            )
        }
    }

    @Test
    fun `getMessages returns empty list for unknown conversation`() {
        val messages = repository.getMessages("nonexistent")
        assertTrue(messages.isEmpty())
    }

    @Test
    fun `sendMessage adds message to conversation`() {
        val initialCount = repository.getMessages("conv_1").size
        val message = repository.sendMessage("conv_1", "Test message")

        assertNotNull(message)
        assertEquals("Test message", message?.content)
        assertTrue(message?.isFromMe == true)
        assertEquals(initialCount + 1, repository.getMessages("conv_1").size)
    }

    @Test
    fun `sendMessage updates conversation last message`() {
        repository.sendMessage("conv_1", "Updated preview")
        val conversation = repository.getConversation("conv_1")
        assertEquals("Updated preview", conversation?.lastMessage)
    }

    @Test
    fun `sendMessage trims whitespace from content`() {
        val message = repository.sendMessage("conv_1", "  Hello World  ")
        assertEquals("Hello World", message?.content)
    }

    @Test
    fun `sendMessage returns null for blank content`() {
        val message = repository.sendMessage("conv_1", "   ")
        assertNull(message)
    }

    @Test
    fun `sendMessage returns null for empty content`() {
        val message = repository.sendMessage("conv_1", "")
        assertNull(message)
    }

    @Test
    fun `sendMessage returns null for unknown conversation`() {
        val message = repository.sendMessage("nonexistent", "Hello")
        assertNull(message)
    }

    @Test
    fun `markConversationAsRead sets unread count to zero`() {
        // conv_1 starts with unreadCount = 2
        repository.markConversationAsRead("conv_1")
        val conversation = repository.getConversation("conv_1")
        assertEquals(0, conversation?.unreadCount)
    }

    @Test
    fun `getMessageCount returns correct count`() {
        assertEquals(5, repository.getMessageCount("conv_1"))
        assertEquals(5, repository.getMessageCount("conv_2"))
        assertEquals(3, repository.getMessageCount("conv_3"))
    }

    @Test
    fun `getMessageCount returns zero for unknown conversation`() {
        assertEquals(0, repository.getMessageCount("nonexistent"))
    }
}
