package com.quickchat.app.util

data class Contact(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val email: String?,
    val lastContactedAt: Long?,
    val tags: List<String> = emptyList()
)

object ContactMergeUtil {

    fun mergeDuplicateContacts(contacts: List<Contact>): List<Contact> {
        if (contacts.isEmpty()) return emptyList()
        
        val groupedByPhone = contacts.groupBy { normalizePhoneNumber(it.phoneNumber) }
        
        return groupedByPhone.values.map { duplicates ->
            if (duplicates.size == 1) {
                duplicates.first()
            } else {
                mergeContactGroup(duplicates)
            }
        }
    }

    private fun mergeContactGroup(contacts: List<Contact>): Contact {
        val mostRecentContact = contacts.maxByOrNull { it.lastContactedAt ?: 0L } ?: contacts.first()
        
        val mergedName = contacts.maxByOrNull { it.name.length }?.name ?: mostRecentContact.name
        val mergedEmail = contacts.firstNotNullOfOrNull { it.email }
        val allTags = contacts.flatMap { it.tags }.distinct().sorted()
        val latestContactTime = contacts.mapNotNull { it.lastContactedAt }.maxOrNull()
        
        return Contact(
            id = mostRecentContact.id,
            name = mergedName,
            phoneNumber = mostRecentContact.phoneNumber,
            email = mergedEmail,
            lastContactedAt = latestContactTime,
            tags = allTags
        )
    }

    fun normalizePhoneNumber(phone: String): String {
        val digitsOnly = phone.filter { it.isDigit() }
        
        return when {
            digitsOnly.length >= 10 -> digitsOnly.takeLast(10)
            else -> digitsOnly
        }
    }

    fun findSimilarContacts(target: Contact, candidates: List<Contact>, threshold: Double = 0.7): List<Contact> {
        return candidates.filter { candidate ->
            if (candidate.id == target.id) return@filter false
            
            val phoneMatch = normalizePhoneNumber(target.phoneNumber) == normalizePhoneNumber(candidate.phoneNumber)
            val nameSimilarity = calculateNameSimilarity(target.name, candidate.name)
            val emailMatch = target.email != null && candidate.email != null && target.email == candidate.email
            
            phoneMatch || emailMatch || nameSimilarity >= threshold
        }
    }

    private fun calculateNameSimilarity(name1: String, name2: String): Double {
        val normalized1 = name1.lowercase().trim()
        val normalized2 = name2.lowercase().trim()
        
        if (normalized1 == normalized2) return 1.0
        if (normalized1.isEmpty() || normalized2.isEmpty()) return 0.0
        
        val longerLength = maxOf(normalized1.length, normalized2.length)
        val editDistance = levenshteinDistance(normalized1, normalized2)
        
        return 1.0 - (editDistance.toDouble() / longerLength)
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val costs = IntArray(s2.length + 1) { it }
        
        for (i in 1..s1.length) {
            var previousValue = i
            for (j in 1..s2.length) {
                val newValue = if (s1[i - 1] == s2[j - 1]) {
                    costs[j - 1]
                } else {
                    minOf(costs[j - 1], previousValue, costs[j]) + 1
                }
                costs[j - 1] = previousValue
                previousValue = newValue
            }
            costs[s2.length] = previousValue
        }
        
        return costs[s2.length]
    }

    fun rankContactsByRelevance(contacts: List<Contact>, searchQuery: String): List<Contact> {
        if (searchQuery.isBlank()) return contacts
        
        val query = searchQuery.lowercase().trim()
        
        return contacts
            .map { contact ->
                val score = calculateRelevanceScore(contact, query)
                contact to score
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .map { it.first }
    }

    private fun calculateRelevanceScore(contact: Contact, query: String): Int {
        var score = 0
        val nameLower = contact.name.lowercase()
        
        if (nameLower.startsWith(query)) score += 100
        else if (nameLower.contains(query)) score += 50
        
        if (contact.phoneNumber.contains(query)) score += 30
        
        contact.email?.lowercase()?.let { email ->
            if (email.contains(query)) score += 20
        }
        
        if (contact.tags.any { it.lowercase().contains(query) }) score += 15
        
        return score
    }
}
// Version 124 - JaCoCo test with working tests  for pfe 