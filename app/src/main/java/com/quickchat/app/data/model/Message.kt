package com.quickchat.app.data.model

/**
 * Represents a single chat message.
 */
data class Message(
    val id: String,
    val conversationId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long,
    val isFromMe: Boolean
) {
    init {
        require(id.isNotBlank()) { "Message ID must not be blank" }
        require(conversationId.isNotBlank()) { "Conversation ID must not be blank" }
        require(senderName.isNotBlank()) { "Sender name must not be blank" }
        require(content.isNotBlank()) { "Message content must not be blank" }
        require(timestamp > 0) { "Timestamp must be positive" }
    }
}
