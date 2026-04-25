package com.quickchat.app.util
//testing
fun validateMessage(text: String): String? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    if (trimmed.length > 1000) return null
    return trimmed
}
//jacocotestfinal