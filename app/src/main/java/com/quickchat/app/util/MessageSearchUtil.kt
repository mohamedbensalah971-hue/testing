package com.quickchat.app.util

/**
 * Utility for searching and filtering messages with various criteria.
 * Medium complexity: handles multiple filter conditions and sorting.
 */
object MessageSearchUtil {
    
    /**
     * Searches messages based on content and optional filters.
     * 
     * @param messages List of messages to search in
     * @param query Search query (case-insensitive substring match)
     * @param maxResults Maximum number of results to return (default: 100)
     * @param senderFilter Optional sender ID to filter by
     * @return Filtered and sorted list of matching messages
     */
    fun searchMessages(
        messages: List<Message>,
        query: String,
        maxResults: Int = 100,
        senderFilter: String? = null
    ): List<Message> {
        if (query.isBlank()) return emptyList()
        
        val normalizedQuery = query.lowercase().trim()
        
        return messages
            .filter { message ->
                // Filter by query match
                message.content.lowercase().contains(normalizedQuery) &&
                // Filter by sender if specified
                (senderFilter == null || message.senderId == senderFilter)
            }
            // Sort by timestamp descending (newest first)
            .sortedByDescending { it.timestamp }
            // Limit results
            .take(maxResults)
    }
    
    /**
     * Filters messages by date range.
     * 
     * @param messages List of messages to filter
     * @param startTime Minimum timestamp (inclusive)
     * @param endTime Maximum timestamp (inclusive)
     * @return Messages within the time range, sorted by timestamp
     */
    fun filterByDateRange(
        messages: List<Message>,
        startTime: Long,
        endTime: Long
    ): List<Message> {
        return messages
            .filter { it.timestamp in startTime..endTime }
            .sortedByDescending { it.timestamp }
    }
    
    /**
     * Gets messages from a specific conversation.
     * 
     * @param messages List of messages to filter
     * @param conversationId ID of the conversation to filter by
     * @return Messages from that conversation, sorted by timestamp ascending
     */
    fun getConversationMessages(
        messages: List<Message>,
        conversationId: String
    ): List<Message> {
        return messages
            .filter { it.conversationId == conversationId }
            .sortedBy { it.timestamp }
    }
    
    /**
     * Counts unread messages by sender in a conversation.
     * 
     * @param messages List of messages to analyze
     * @param conversationId ID of the conversation
     * @return Map of sender ID to unread message count
     */
    fun countUnreadBySender(
        messages: List<Message>,
        conversationId: String
    ): Map<String, Int> {
        return messages
            .filter { it.conversationId == conversationId && !it.isRead }
            .groupingBy { it.senderId }
            .eachCount()
    }
}

// Placeholder data classes (normally imported from model package)
data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
