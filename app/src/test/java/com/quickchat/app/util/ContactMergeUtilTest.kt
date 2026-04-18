package com.quickchat.app.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

class ContactMergeUtilTest {

    private lateinit var contacts: List<Contact>

    @BeforeEach
    fun setUp() {
        contacts = listOf(
            Contact(1, "John Doe", "1234567890", "john@example.com", 1643723400, listOf("friend", "family")),
            Contact(2, "Jane Doe", "9876543210", "jane@example.com", 1643723400, listOf("friend", "colleague")),
            Contact(3, "John Doe", "1234567890", "john2@example.com", 1643723400, listOf("friend", "family"))
        )
    }

    @Test
    fun testMergeDuplicateContacts() {
        val mergedContacts = ContactMergeUtil.mergeDuplicateContacts(contacts)
        assertEquals(2, mergedContacts.size)
    }

    @Test
    fun testNormalizePhoneNumber() {
        val phoneNumber = "(123) 456-7890"
        val normalizedPhoneNumber = ContactMergeUtil.normalizePhoneNumber(phoneNumber)
        assertEquals("1234567890", normalizedPhoneNumber)
    }

    @Test
    fun testFindSimilarContacts() {
        val targetContact = contacts[0]
        val similarContacts = ContactMergeUtil.findSimilarContacts(targetContact, contacts)
        assertEquals(1, similarContacts.size)
    }

    @Test
    fun testRankContactsByRelevance() {
        val searchQuery = "John"
        val rankedContacts = ContactMergeUtil.rankContactsByRelevance(contacts, searchQuery)
        assertEquals(2, rankedContacts.size)
    }

    @Test
    fun testRankContactsByRelevanceEmptyQuery() {
        val searchQuery = ""
        val rankedContacts = ContactMergeUtil.rankContactsByRelevance(contacts, searchQuery)
        assertEquals(3, rankedContacts.size)
    }

    @Test
    fun testRankContactsByRelevanceNoMatches() {
        val searchQuery = "Unknown"
        val rankedContacts = ContactMergeUtil.rankContactsByRelevance(contacts, searchQuery)
        assertTrue(rankedContacts.isEmpty())
    }
}