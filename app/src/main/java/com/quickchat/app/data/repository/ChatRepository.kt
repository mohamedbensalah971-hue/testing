package com.quickchat.app.data.repository

import com.quickchat.app.data.model.Conversation
import com.quickchat.app.data.model.Message
import java.util.UUID

/**
 * In-memory repository that acts as the single source of truth for
 * conversations and messages. In a production app this would be backed
 * by a local database (Room) and/or a network API.
 */
class ChatRepository {

    private val conversations = mutableListOf<Conversation>()
    private val messages = mutableMapOf<String, MutableList<Message>>()

    init {
        seedData()
    }

    // ── Conversations ────────────────────────────────────────────────

    fun getConversations(): List<Conversation> {
        return conversations
            .sortedByDescending { it.lastMessageTimestamp }
            .toList()
    }

    fun getConversation(conversationId: String): Conversation? {
        return conversations.find { it.id == conversationId }
    }

    fun deleteConversation(conversationId: String): Boolean {
        val removed = conversations.removeAll { it.id == conversationId }
        if (removed) {
            messages.remove(conversationId)
        }
        return removed
    }

    // ── Messages ─────────────────────────────────────────────────────

    fun getMessages(conversationId: String): List<Message> {
        return messages[conversationId]
            ?.sortedBy { it.timestamp }
            ?.toList()
            ?: emptyList()
    }

    fun sendMessage(conversationId: String, content: String): Message? {
        val trimmed = content.trim()
        if (trimmed.isBlank()) return null

        val conversation = conversations.find { it.id == conversationId } ?: return null

        val message = Message(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderName = "Me",
            content = trimmed,
            timestamp = System.currentTimeMillis(),
            isFromMe = true
        )

        messages.getOrPut(conversationId) { mutableListOf() }.add(message)

        // Update conversation preview
        val index = conversations.indexOf(conversation)
        if (index != -1) {
            conversations[index] = conversation.copy(
                lastMessage = trimmed,
                lastMessageTimestamp = message.timestamp,
                unreadCount = 0
            )
        }

        return message
    }

    fun markConversationAsRead(conversationId: String) {
        val index = conversations.indexOfFirst { it.id == conversationId }
        if (index != -1) {
            conversations[index] = conversations[index].copy(unreadCount = 0)
        }
    }

    fun getMessageCount(conversationId: String): Int {
        return messages[conversationId]?.size ?: 0
    }

    // ── Seed Data ────────────────────────────────────────────────────

    private fun seedData() {
        val now = System.currentTimeMillis()

        val conv1 = Conversation(
            id = "conv_1",
            contactName = "Alice Martin",
            lastMessage = "See you tomorrow! 👋",
            lastMessageTimestamp = now - 60_000,
            unreadCount = 2,
            avatarInitial = 'A'
        )
        val conv2 = Conversation(
            id = "conv_2",
            contactName = "Bob Smith",
            lastMessage = "Thanks for the help!",
            lastMessageTimestamp = now - 3_600_000,
            unreadCount = 0,
            avatarInitial = 'B'
        )
        val conv3 = Conversation(
            id = "conv_3",
            contactName = "Clara Johnson",
            lastMessage = "Can you send the file?",
            lastMessageTimestamp = now - 7_200_000,
            unreadCount = 1,
            avatarInitial = 'C'
        )
        val conv4 = Conversation(
            id = "conv_4",
            contactName = "David Lee",
            lastMessage = "Let's grab coffee ☕",
            lastMessageTimestamp = now - 86_400_000,
            unreadCount = 0,
            avatarInitial = 'D'
        )
        val conv5 = Conversation(
            id = "conv_5",
            contactName = "Emma Wilson",
            lastMessage = "Happy birthday! 🎂",
            lastMessageTimestamp = now - 172_800_000,
            unreadCount = 0,
            avatarInitial = 'E'
        )

        conversations.addAll(listOf(conv1, conv2, conv3, conv4, conv5))

        // Seed messages for conv_1
        messages["conv_1"] = mutableListOf(
            Message("m1", "conv_1", "Alice Martin", "Hey! How are you?", now - 120_000, false),
            Message("m2", "conv_1", "Me", "I'm good, thanks! You?", now - 90_000, true),
            Message("m3", "conv_1", "Alice Martin", "Great! Want to meet tomorrow?", now - 70_000, false),
            Message("m4", "conv_1", "Me", "Sure, sounds good!", now - 65_000, true),
            Message("m5", "conv_1", "Alice Martin", "See you tomorrow! 👋", now - 60_000, false)
        )

        // Seed messages for conv_2
        messages["conv_2"] = mutableListOf(
            Message("m6", "conv_2", "Me", "Can you review the pull request?", now - 4_000_000, true),
            Message("m7", "conv_2", "Bob Smith", "Sure, I'll look at it now", now - 3_800_000, false),
            Message("m8", "conv_2", "Bob Smith", "Looks good! Approved.", now - 3_700_000, false),
            Message("m9", "conv_2", "Me", "Awesome, thanks!", now - 3_650_000, true),
            Message("m10", "conv_2", "Bob Smith", "Thanks for the help!", now - 3_600_000, false)
        )

        // Seed messages for conv_3
        messages["conv_3"] = mutableListOf(
            Message("m11", "conv_3", "Clara Johnson", "Hi, do you have the report?", now - 7_500_000, false),
            Message("m12", "conv_3", "Me", "Which one?", now - 7_400_000, true),
            Message("m13", "conv_3", "Clara Johnson", "Can you send the file?", now - 7_200_000, false)
        )
    }
}
