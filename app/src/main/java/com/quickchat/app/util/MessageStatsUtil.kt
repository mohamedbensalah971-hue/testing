package com.quickchat.app.util

import com.quickchat.app.data.model.Message

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
     * Counts messages by sender.
     */
    fun countMessagesBySender(messages: List<Message>): Map<String, Int> {
        return messages.groupingBy { it.senderName }.eachCount()
    }
}
//testing