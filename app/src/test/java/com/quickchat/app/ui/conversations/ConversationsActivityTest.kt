import org.junit.jupiter.api.Assertions.*
package com.quickchat.app.ui.conversations

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [28])
class ConversationsActivityTest {

    private lateinit var activity: ConversationsActivity
    private lateinit var viewModel: ConversationsViewModel

    @BeforeEach
    fun setup() {
        activity = Robolectric.buildActivity(ConversationsActivity::class.java).create().get()
        viewModel = mockk()
        activity.viewModel = viewModel
    }

    @Test
    fun testOnCreate() {
        activity.onCreate(null)
        verify { viewModel.loadConversations() }
    }

    @Test
    fun testOnResume() {
        activity.onResume()
        verify { viewModel.loadConversations() }
    }

    @Test
    fun testSetupToolbar() {
        activity.setupToolbar()
        assertTrue(activity.supportActionBar?.title == "QuickChat")
    }

    @Test
    fun testSetupRecyclerView() {
        activity.setupRecyclerView()
        assertTrue(activity.binding.recyclerConversations.adapter != null)
    }

    @Test
    fun testObserveViewModel() {
        val conversations = listOf(Conversation(1, "Conversation 1"), Conversation(2, "Conversation 2"))
        every { viewModel.conversations.value } returns conversations
        activity.observeViewModel()
        assertTrue(activity.adapter.itemCount == conversations.size)
    }

    @Test
    fun testObserveViewModelEmpty() {
        every { viewModel.isEmpty.value } returns true
        activity.observeViewModel()
        assertTrue(activity.binding.textEmptyState.visibility == View.VISIBLE)
        assertTrue(activity.binding.recyclerConversations.visibility == View.GONE)
    }
}

data class Conversation(val id: Int, val name: String)
//azerty