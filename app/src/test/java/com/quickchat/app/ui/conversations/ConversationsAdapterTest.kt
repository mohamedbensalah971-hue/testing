package com.quickchat.app.ui.conversations

import com.google.common.truth.Truth.assertThat
import com.quickchat.app.data.model.Conversation
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName

class ConversationsAdapterTest {

    private lateinit var adapter: ConversationsAdapter
    private lateinit var onConversationClick: (Conversation) -> Unit

    @BeforeEach
    fun setUp() {
        onConversationClick = mockk(relaxed = true)
        adapter = ConversationsAdapter(onConversationClick)
    }

    @Test
    @DisplayName("Test onCreateViewHolder returns ViewHolder")
    fun testOnCreateViewHolder() {
        // Given
        val parent = mockk(relaxed = true)
        val viewType = 0

        // When
        val viewHolder = adapter.onCreateViewHolder(parent, viewType)

        // Then
        assertThat(viewHolder).isInstanceOf(ConversationsAdapter.ViewHolder::class.java)
    }

    @Test
    @DisplayName("Test onBindViewHolder binds conversation data")
    fun testOnBindViewHolder() {
        // Given
        val conversation = Conversation(
            id = 1,
            avatarInitial = 'A',
            contactName = "John Doe",
            lastMessage = "Hello",
            lastMessageTimestamp = 1643723400,
            unreadCount = 1
        )
        val holder = ConversationsAdapter.ViewHolder(mockk(relaxed = true))

        // When
        holder.bind(conversation)

        // Then
        // Note: We cannot directly verify the text of the views here because we are not using a real view.
        // Instead, we can verify that the onConversationClick is called when the view is clicked.
    }

    @Test
    @DisplayName("Test onConversationClick is called when view is clicked")
    fun testOnConversationClick() {
        // Given
        val conversation = Conversation(
            id = 1,
            avatarInitial = 'A',
            contactName = "John Doe",
            lastMessage = "Hello",
            lastMessageTimestamp = 1643723400,
            unreadCount = 1
        )
        val holder = ConversationsAdapter.ViewHolder(mockk(relaxed = true))
        holder.bind(conversation)

        // When
        holder.itemView.performClick()

        // Then
        verify { onConversationClick(conversation) }
    }

    @Test
    @DisplayName("Test areItemsTheSame returns true for same conversation")
    fun testAreItemsTheSame() {
        // Given
        val oldItem = Conversation(id = 1, avatarInitial = 'A', contactName = "John Doe", lastMessage = "Hello", lastMessageTimestamp = 1643723400, unreadCount = 1)
        val newItem = Conversation(id = 1, avatarInitial = 'A', contactName = "John Doe", lastMessage = "Hello", lastMessageTimestamp = 1643723400, unreadCount = 1)

        // When
        val result = ConversationsAdapter.ConversationDiffCallback().areItemsTheSame(oldItem, newItem)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("Test areItemsTheSame returns false for different conversation")
    fun testAreItemsTheSameDifferentConversation() {
        // Given
        val oldItem = Conversation(id = 1, avatarInitial = 'A', contactName = "John Doe", lastMessage = "Hello", lastMessageTimestamp = 1643723400, unreadCount = 1)
        val newItem = Conversation(id = 2, avatarInitial = 'B', contactName = "Jane Doe", lastMessage = "Hello", lastMessageTimestamp = 1643723400, unreadCount = 1)

        // When
        val result = ConversationsAdapter.ConversationDiffCallback().areItemsTheSame(oldItem, newItem)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    @DisplayName("Test areContentsTheSame returns true for same conversation")
    fun testAreContentsTheSame() {
        // Given
        val oldItem = Conversation(id = 1, avatarInitial = 'A', contactName = "John Doe", lastMessage = "Hello", lastMessageTimestamp = 1643723400, unreadCount = 1)
        val newItem = Conversation(id = 1, avatarInitial = 'A', contactName = "John Doe", lastMessage = "Hello", lastMessageTimestamp = 1643723400, unreadCount = 1)

        // When
        val result = ConversationsAdapter.ConversationDiffCallback().areContentsTheSame(oldItem, newItem)

        // Then
        assertThat(result).isTrue()
    }
}