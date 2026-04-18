package com.quickchat.app.ui.chat

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertEquals
import io.mockk.mockk
import io.mockk.every
import io.mockk.verify

class ChatViewModelTest {

    private lateinit var mockRepository: ChatRepository
    private lateinit var viewModel: ChatViewModel

    @BeforeEach
    fun setUp() {
        mockRepository = mockk(relaxed = true)
        viewModel = ChatViewModel(mockRepository)
    }

    @Test
    fun loadChat_setsContactName() {
        val conversationId = "123"
        val contactName = "John Doe"
        every { mockRepository.getConversation(conversationId) } returns ChatConversation(contactName = contactName)

        viewModel.loadChat(conversationId)

        assertEquals(contactName, viewModel.contactName.value)
    }

    @Test
    fun loadChat_marksConversationAsRead() {
        val conversationId = "123"
        every { mockRepository.getConversation(conversationId) } returns ChatConversation()

        viewModel.loadChat(conversationId)

        verify { mockRepository.markConversationAsRead(conversationId) }
    }

    @Test
    fun sendMessage_sendsMessageWhenContentIsNotBlank() {
        val conversationId = "123"
        val content = "Hello, world!"
        every { mockRepository.sendMessage(conversationId, content) } returns Message()

        viewModel.loadChat(conversationId)
        viewModel.sendMessage(content)

        verify { mockRepository.sendMessage(conversationId, content) }
    }

    @Test
    fun sendMessage_doesNotSendMessageWhenContentIsBlank() {
        val conversationId = "123"
        val content = ""

        viewModel.loadChat(conversationId)
        viewModel.sendMessage(content)

        verify { mockRepository.sendMessage(conversationId, content) wasNot Called }
    }

    @Test
    fun getMessageCount_returnsMessageCount() {
        val conversationId = "123"
        val messages = listOf(Message(), Message())
        every { mockRepository.getMessages(conversationId) } returns messages

        viewModel.loadChat(conversationId)

        assertEquals(messages.size, viewModel.getMessageCount())
    }

    @Test
    fun getCurrentConversationId_returnsCurrentConversationId() {
        val conversationId = "123"

        viewModel.loadChat(conversationId)

        assertEquals(conversationId, viewModel.getCurrentConversationId())
    }
}

data class ChatConversation(val contactName: String = "")
data class Message(val id: Int = 0)