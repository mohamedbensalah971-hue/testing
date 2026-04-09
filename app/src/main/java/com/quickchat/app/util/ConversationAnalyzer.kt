package com.quickchat.app.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

data class Message(
    val id: Long,
    val conversationId: Long,
    val senderId: Long,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val attachmentCount: Int = 0
)

data class ConversationStats(
    val totalMessages: Int,
    val averageResponseTime: Long,
    val mostActiveHour: Int,
    val longestGap: Long,
    val messageFrequency: Map<String, Int>,
    val readRate: Double
)

object ConversationAnalyzer {

    fun analyzeConversation(messages: List<Message>): ConversationStats? {
        if (messages.isEmpty()) return null
        
        val totalMessages = messages.size
        val averageResponseTime = calculateAverageResponseTime(messages)
        val mostActiveHour = findMostActiveHour(messages)
        val longestGap = findLongestGap(messages)
        val messageFrequency = calculateMessageFrequency(messages)
        val readRate = calculateReadRate(messages)
        
        return ConversationStats(
            totalMessages = totalMessages,
            averageResponseTime = averageResponseTime,
            mostActiveHour = mostActiveHour,
            longestGap = longestGap,
            messageFrequency = messageFrequency,
            readRate = readRate
        )
    }

    private fun calculateAverageResponseTime(messages: List<Message>): Long {
        if (messages.size < 2) return 0L
        
        val sortedMessages = messages.sortedBy { it.timestamp }
        val responseTimes = mutableListOf<Long>()
        
        for (i in 1 until sortedMessages.size) {
            val currentMsg = sortedMessages[i]
            val previousMsg = sortedMessages[i - 1]
            
            if (currentMsg.senderId != previousMsg.senderId) {
                val timeDiff = currentMsg.timestamp - previousMsg.timestamp
                if (timeDiff > 0 && timeDiff < 86400000) { // Less than 24 hours
                    responseTimes.add(timeDiff)
                }
            }
        }
        
        return if (responseTimes.isNotEmpty()) {
            responseTimes.average().toLong()
        } else {
            0L
        }
    }

    private fun findMostActiveHour(messages: List<Message>): Int {
        if (messages.isEmpty()) return 0
        
        val hourCounts = mutableMapOf<Int, Int>()
        
        messages.forEach { message ->
            val hour = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(message.timestamp),
                ZoneId.systemDefault()
            ).hour
            
            hourCounts[hour] = hourCounts.getOrDefault(hour, 0) + 1
        }
        
        return hourCounts.maxByOrNull { it.value }?.key ?: 0
    }

    private fun findLongestGap(messages: List<Message>): Long {
        if (messages.size < 2) return 0L
        
        val sortedMessages = messages.sortedBy { it.timestamp }
        var longestGap = 0L
        
        for (i in 1 until sortedMessages.size) {
            val gap = sortedMessages[i].timestamp - sortedMessages[i - 1].timestamp
            if (gap > longestGap) {
                longestGap = gap
            }
        }
        
        return longestGap
    }

    private fun calculateMessageFrequency(messages: List<Message>): Map<String, Int> {
        if (messages.isEmpty()) return emptyMap()
        
        val sortedMessages = messages.sortedBy { it.timestamp }
        val firstTimestamp = sortedMessages.first().timestamp
        val lastTimestamp = sortedMessages.last().timestamp
        
        val daysDiff = ChronoUnit.DAYS.between(
            Instant.ofEpochMilli(firstTimestamp).atZone(ZoneId.systemDefault()).toLocalDate(),
            Instant.ofEpochMilli(lastTimestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        ).toInt() + 1
        
        val messagesPerDay = if (daysDiff > 0) messages.size.toDouble() / daysDiff else 0.0
        
        return mapOf(
            "total_days" to daysDiff,
            "messages_per_day" to messagesPerDay.toInt()
        )
    }

    private fun calculateReadRate(messages: List<Message>): Double {
        if (messages.isEmpty()) return 0.0
        
        val readCount = messages.count { it.isRead }
        return (readCount.toDouble() / messages.size) * 100.0
    }

    fun detectConversationPatterns(messages: List<Message>): Map<String, Any> {
        if (messages.isEmpty()) return emptyMap()
        
        val patterns = mutableMapOf<String, Any>()
        
        val hasBurst = detectBurstMessaging(messages)
        val avgMessageLength = messages.map { it.content.length }.average()
        val hasAttachments = messages.any { it.attachmentCount > 0 }
        val uniqueSenders = messages.map { it.senderId }.distinct().size
        
        patterns["burst_messaging"] = hasBurst
        patterns["average_message_length"] = avgMessageLength.toInt()
        patterns["has_attachments"] = hasAttachments
        patterns["participants_count"] = uniqueSenders
        
        return patterns
    }

    private fun detectBurstMessaging(messages: List<Message>): Boolean {
        if (messages.size < 3) return false
        
        val sortedMessages = messages.sortedBy { it.timestamp }
        var burstCount = 0
        
        for (i in 1 until sortedMessages.size) {
            val timeDiff = sortedMessages[i].timestamp - sortedMessages[i - 1].timestamp
            if (timeDiff < 60000) { // Less than 1 minute
                burstCount++
                if (burstCount >= 3) return true
            } else {
                burstCount = 0
            }
        }
        
        return false
    }

    fun rankConversationsByActivity(conversationMessages: Map<Long, List<Message>>): List<Pair<Long, Int>> {
        return conversationMessages
            .mapValues { (_, messages) ->
                val recentMessages = messages.filter { 
                    it.timestamp > System.currentTimeMillis() - 604800000 // Last 7 days
                }
                calculateActivityScore(recentMessages)
            }
            .toList()
            .sortedByDescending { it.second }
    }

    private fun calculateActivityScore(messages: List<Message>): Int {
        var score = messages.size * 10
        
        if (messages.isNotEmpty()) {
            val avgResponseTime = calculateAverageResponseTime(messages)
            if (avgResponseTime > 0 && avgResponseTime < 3600000) { // Less than 1 hour
                score += 50
            }
            
            if (detectBurstMessaging(messages)) {
                score += 30
            }
            
            val readRate = calculateReadRate(messages)
            score += (readRate / 10).toInt()
        }
        
        return score
    }

    fun predictNextMessageTime(messages: List<Message>, senderId: Long): Long? {
        val senderMessages = messages.filter { it.senderId == senderId }.sortedBy { it.timestamp }
        
        if (senderMessages.size < 3) return null
        
        val intervals = mutableListOf<Long>()
        for (i in 1 until senderMessages.size) {
            intervals.add(senderMessages[i].timestamp - senderMessages[i - 1].timestamp)
        }
        
        val averageInterval = intervals.average().toLong()
        val lastMessageTime = senderMessages.last().timestamp
        
        return lastMessageTime + averageInterval
    }
}
//testingspop