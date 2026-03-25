package com.quickchat.app.util

/**
 * Utility helpers for lightweight message analytics.
 */
object MessageStatsUtil {

    /**
     * Counts messages per conversation.
     *
     * @param messages List of messages to analyze.
     * @return Map where key is conversationId and value is number of messages.
     */
    fun countMessagesByConversation(messages: List<Message>): Map<String, Int> {
        return messages.groupingBy { it.conversationId }.eachCount()
    }

    /**
     * Counts unread messages in the provided list.
     */
    fun countUnreadMessages(messages: List<Message>): Int {
        return messages.count { !it.isRead }
    }
}
