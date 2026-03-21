package com.quickchat.app.data.model

/**
 * Represents a conversation thread with a contact.
 */
data class Conversation(
    val id: String,
    val contactName: String,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val unreadCount: Int = 0,
    val avatarInitial: Char
) {
    init {
        require(id.isNotBlank()) { "Conversation ID must not be blank" }
        require(contactName.isNotBlank()) { "Contact name must not be blank" }
        require(lastMessageTimestamp >= 0) { "Timestamp must not be negative" }
        require(unreadCount >= 0) { "Unread count must not be negative" }
    }
}
