import org.junit.jupiter.api.Assertions.*
package com.quickchat.app.ui.conversations

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import androidx.recyclerview.widget.ListAdapter
import com.quickchat.app.data.model.Conversation
import com.quickchat.app.util.TimeFormatter

class ConversationsAdapterTest {

    private lateinit var adapter: ConversationsAdapter
    private lateinit var onConversationClick: (Conversation) -> Unit

    @BeforeEach
    fun setUp() {
        onConversationClick = mockk(relaxed = true)
        adapter = ConversationsAdapter(onConversationClick)
    }

    @Test
    @DisplayName("Test areItemsTheSame returns true when conversation ids are the same")
    fun testAreItemsTheSameReturnsTrue() {
        val conversation1 = Conversation(id = 1, contactName = "John", lastMessage = "Hello", lastMessageTimestamp = 1643723400)
        val conversation2 = Conversation(id = 1, contactName = "Jane", lastMessage = "Hi", lastMessageTimestamp = 1643723400)
        val result = adapter.diffCallback.areItemsTheSame(conversation1, conversation2)
        assertTrue(result)
    }

    @Test
    @DisplayName("Test areItemsTheSame returns false when conversation ids are different")
    fun testAreItemsTheSameReturnsFalse() {
        val conversation1 = Conversation(id = 1, contactName = "John", lastMessage = "Hello", lastMessageTimestamp = 1643723400)
        val conversation2 = Conversation(id = 2, contactName = "Jane", lastMessage = "Hi", lastMessageTimestamp = 1643723400)
        val result = adapter.diffCallback.areItemsTheSame(conversation1, conversation2)
        assertFalse(result)
    }

    @Test
    @DisplayName("Test areContentsTheSame returns true when conversations are the same")
    fun testAreContentsTheSameReturnsTrue() {
        val conversation1 = Conversation(id = 1, contactName = "John", lastMessage = "Hello", lastMessageTimestamp = 1643723400)
        val conversation2 = Conversation(id = 1, contactName = "John", lastMessage = "Hello", lastMessageTimestamp = 1643723400)
        val result = adapter.diffCallback.areContentsTheSame(conversation1, conversation2)
        assertTrue(result)
    }

    @Test
    @DisplayName("Test areContentsTheSame returns false when conversations are different")
    fun testAreContentsTheSameReturnsFalse() {
        val conversation1 = Conversation(id = 1, contactName = "John", lastMessage = "Hello", lastMessageTimestamp = 1643723400)
        val conversation2 = Conversation(id = 1, contactName = "Jane", lastMessage = "Hi", lastMessageTimestamp = 1643723400)
        val result = adapter.diffCallback.areContentsTheSame(conversation1, conversation2)
        assertFalse(result)
    }

    @Test
    @DisplayName("Test submitList updates the adapter's list")
    fun testSubmitListUpdatesAdapterList() {
        val conversations = listOf(Conversation(id = 1, contactName = "John", lastMessage = "Hello", lastMessageTimestamp = 1643723400))
        adapter.submitList(conversations)
        assertEquals(1, adapter.itemCount)
    }

    @Test
    @DisplayName("Test onConversationClick is called when a conversation is clicked")
    fun testOnConversationClickIsCalled() {
        val conversation = Conversation(id = 1, contactName = "John", lastMessage = "Hello", lastMessageTimestamp = 1643723400)
        adapter.submitList(listOf(conversation))
        adapter.onBindViewHolder(ConversationsAdapter.ViewHolder(mockk(relaxed = true)), 0)
        verify { onConversationClick(conversation) }
    }
}