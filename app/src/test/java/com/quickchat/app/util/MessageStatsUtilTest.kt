import com.quickchat.app.util.MessageStatsUtil
import com.quickchat.app.util.Message
import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class MessageStatsUtilTest {

    @Test
    fun `countMessagesByConversation should return map with conversationId and message count`() {
        // Given
        val messages = listOf(
            Message("conversation1", true),
            Message("conversation1", false),
            Message("conversation2", true)
        )

        // When
        val result = MessageStatsUtil.countMessagesByConversation(messages)

        // Then
        assertEquals(2, result["conversation1"])
        assertEquals(1, result["conversation2"])
    }

    @Test
    fun `countMessagesByConversation should return empty map when input list is empty`() {
        // Given
        val messages = emptyList<Message>()

        // When
        val result = MessageStatsUtil.countMessagesByConversation(messages)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `countUnreadMessages should return count of unread messages`() {
        // Given
        val messages = listOf(
            Message("conversation1", true),
            Message("conversation1", false),
            Message("conversation2", false)
        )

        // When
        val result = MessageStatsUtil.countUnreadMessages(messages)

        // Then
        assertEquals(2, result)
    }

    @Test
    fun `countUnreadMessages should return zero when all messages are read`() {
        // Given
        val messages = listOf(
            Message("conversation1", true),
            Message("conversation1", true),
            Message("conversation2", true)
        )

        // When
        val result = MessageStatsUtil.countUnreadMessages(messages)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun `countUnreadMessages should return zero when input list is empty`() {
        // Given
        val messages = emptyList<Message>()

        // When
        val result = MessageStatsUtil.countUnreadMessages(messages)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun `countUnreadMessages should return count of unread messages when all messages are unread`() {
        // Given
        val messages = listOf(
            Message("conversation1", false),
            Message("conversation1", false),
            Message("conversation2", false)
        )

        // When
        val result = MessageStatsUtil.countUnreadMessages(messages)

        // Then
        assertEquals(3, result)
    }
}

data class Message(val conversationId: String, val isRead: Boolean)