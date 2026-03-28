package com.quickchat.app.ui.conversations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickchat.app.data.model.Conversation
import com.quickchat.app.data.repository.ChatRepository

/**
 * ViewModel for the conversations list screen.
 */
class ConversationsViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _conversations = MutableLiveData<List<Conversation>>()
    val conversations: LiveData<List<Conversation>> get() = _conversations

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    init {
        loadConversations()
    }

    fun loadConversations() {
        val list = repository.getConversations()
        _conversations.value = list
        _isEmpty.value = list.isEmpty()
    }

    fun deleteConversation(conversationId: String) {
        repository.deleteConversation(conversationId)
        loadConversations()
    }

    fun getConversationCount(): Int {
        return _conversations.value?.size ?: 0
    }
}
//testing1