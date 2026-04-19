package com.quickchat.app.util

import com.quickchat.app.data.model.Message

/**
 * Utility helpers to score and sort messages by simple priority rules.
 */
object MessagePriorityUtil {

    /**
     * Assigns a priority score to a message.
     * Higher score means higher priority.
     */
    fun calculatePriority(message: Message): Int {
        var score = 0

        // Messages from others are more important than messages from me.
        if (!message.isFromMe) score += 10

        // Slight boost for very recent messages (last 24h).
        val oneDayMs = 24L * 60L * 60L * 1000L
        if (System.currentTimeMillis() - message.timestamp <= oneDayMs) {
            score += 5
        }

        // Boost if content looks urgent.
        val content = message.content.lowercase()
        if (
            content.contains("urgent") ||
            content.contains("asap") ||
            content.contains("important")
        ) {
            score += 7
        }

        return score
    }

    /**
     * Returns messages sorted by descending priority score.
     */
    fun sortByPriority(messages: List<Message>): List<Message> {
        return messages.sortedByDescending { calculatePriority(it) }
    }
}
//testing with jacoco