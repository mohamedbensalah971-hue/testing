package com.quickchat.app.util

/**
 * Validates a chat message before it is sent.
 *
 * @param text The raw input from the user.
 * @return The trimmed message if valid, or null if it should be rejected.
 * 
 * Updated: Added max length validation
 * test 4
 */
fun validateMessage(text: String): String? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    if (trimmed.length > 1000) return null
    return trimmed
}
