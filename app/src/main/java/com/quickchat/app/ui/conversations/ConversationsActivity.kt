package com.quickchat.app.ui.conversations

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickchat.app.databinding.ActivityConversationsBinding
import com.quickchat.app.ui.chat.ChatActivity

/**
 * Main screen showing the list of all conversations.
 */
class ConversationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConversationsBinding
    private val viewModel: ConversationsViewModel by viewModels()
    private lateinit var adapter: ConversationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadConversations()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "QuickChat"
    }

    private fun setupRecyclerView() {
        adapter = ConversationsAdapter { conversation ->
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversation.id)
            }
            startActivity(intent)
        }

        binding.recyclerConversations.apply {
            layoutManager = LinearLayoutManager(this@ConversationsActivity)
            adapter = this@ConversationsActivity.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.conversations.observe(this) { list ->
            adapter.submitList(list)
        }

        viewModel.isEmpty.observe(this) { empty ->
            binding.textEmptyState.visibility = if (empty) View.VISIBLE else View.GONE
            binding.recyclerConversations.visibility = if (empty) View.GONE else View.VISIBLE
        }
    }
}
//jacoco test