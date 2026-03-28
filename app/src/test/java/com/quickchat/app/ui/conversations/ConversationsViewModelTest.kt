import org.junit.jupiter.api.Assertions.*
import com.quickchat.app.data.model.Conversation
import com.quickchat.app.data.repository.ChatRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConversationsViewModelTest {

    private lateinit var viewModel: ConversationsViewModel
    private lateinit var repository: ChatRepository

    @BeforeEachEach
    fun setup() {
        repository = mockk()
        viewModel = ConversationsViewModel(repository)
    }

    @Test
    fun `test loadConversations with empty list`() {
        // Given
        every { repository.getConversations() } returns emptyList()

        // When
        viewModel.loadConversations()

        // Then
        assertTrue(viewModel.isEmpty.value!!)
        assertEquals(0, viewModel.getConversationCount())
    }

    @Test
    fun `test loadConversations with non-empty list`() {
        // Given
        val conversations = listOf(Conversation("id1"), Conversation("id2"))
        every { repository.getConversations() } returns conversations

        // When
        viewModel.loadConversations()

        // Then
        assertFalse(viewModel.isEmpty.value!!)
        assertEquals(conversations.size, viewModel.getConversationCount())
    }

    @Test
    fun `test deleteConversation with valid id`() {
        // Given
        val conversationId = "id1"
        val conversations = listOf(Conversation(conversationId), Conversation("id2"))
        every { repository.getConversations() } returns conversations
        every { repository.deleteConversation(conversationId) } returns Unit

        // When
        viewModel.deleteConversation(conversationId)

        // Then
        verify { repository.deleteConversation(conversationId) }
        assertEquals(conversations.size - 1, viewModel.getConversationCount())
    }

    @Test
    fun `test deleteConversation with invalid id`() {
        // Given
        val conversationId = "id3"
        val conversations = listOf(Conversation("id1"), Conversation("id2"))
        every { repository.getConversations() } returns conversations
        every { repository.deleteConversation(conversationId) } returns Unit

        // When
        viewModel.deleteConversation(conversationId)

        // Then
        verify { repository.deleteConversation(conversationId) }
        assertEquals(conversations.size, viewModel.getConversationCount())
    }

    @Test
    fun `test getConversationCount with empty list`() {
        // Given
        every { repository.getConversations() } returns emptyList()

        // When
        viewModel.loadConversations()

        // Then
        assertEquals(0, viewModel.getConversationCount())
    }

    @Test
    fun `test getConversationCount with non-empty list`() {
        // Given
        val conversations = listOf(Conversation("id1"), Conversation("id2"))
        every { repository.getConversations() } returns conversations

        // When
        viewModel.loadConversations()

        // Then
        assertEquals(conversations.size, viewModel.getConversationCount())
    }
}