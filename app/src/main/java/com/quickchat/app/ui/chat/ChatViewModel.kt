package com.quickchat.app.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickchat.app.data.model.Message
import com.quickchat.app.data.repository.ChatRepository

/**
 * ViewModel for the individual chat screen.
 */
class ChatViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private val _contactName = MutableLiveData<String>()
    val contactName: LiveData<String> get() = _contactName

    private val _messageSent = MutableLiveData<Boolean>()
    val messageSent: LiveData<Boolean> get() = _messageSent

    private var currentConversationId: String = ""

    fun loadChat(conversationId: String) {
        currentConversationId = conversationId
        val conversation = repository.getConversation(conversationId)
        _contactName.value = conversation?.contactName ?: "Unknown"
        repository.markConversationAsRead(conversationId)
        refreshMessages()
    }

    fun sendMessage(content: String) {
        if (content.isBlank() || currentConversationId.isBlank()) {
            _messageSent.value = false
            return
        }

        val message = repository.sendMessage(currentConversationId, content)
        if (message != null) {
            refreshMessages()
            _messageSent.value = true
        } else {
            _messageSent.value = false
        }
    }

    fun getMessageCount(): Int {
        return _messages.value?.size ?: 0
    }

    fun getCurrentConversationId(): String = currentConversationId

    private fun refreshMessages() {
        _messages.value = repository.getMessages(currentConversationId)
    }
}
//testing