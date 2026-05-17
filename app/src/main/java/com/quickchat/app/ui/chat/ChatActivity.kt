package com.quickchat.app.ui.chat

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickchat.app.databinding.ActivityChatBinding

/**
 * Chat screen showing messages for a single conversation.
 */
class ChatActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CONVERSATION_ID = "extra_conversation_id"
    }

    private lateinit var binding: ActivityChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID)
        if (conversationId.isNullOrBlank()) {
            Toast.makeText(this, "Conversation not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        setupInput()
        observeViewModel()

        viewModel.loadChat(conversationId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = MessagesAdapter()
        binding.recyclerMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            adapter = this@ChatActivity.adapter
        }
    }

    private fun setupInput() {
        binding.buttonSend.setOnClickListener {
            val text = binding.editMessage.text?.toString() ?: ""
            if (text.isNotBlank()) {
                viewModel.sendMessage(text)
                binding.editMessage.text?.clear()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.contactName.observe(this) { name ->
            supportActionBar?.title = name
        }

        viewModel.messages.observe(this) { messages ->
            adapter.submitList(messages) {
                // Scroll to bottom after new message
                if (messages.isNotEmpty()) {
                    binding.recyclerMessages.scrollToPosition(messages.size - 1)
                }
            }
        }

        viewModel.messageSent.observe(this) { success ->
            if (!success) {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
//test demo pfe.4