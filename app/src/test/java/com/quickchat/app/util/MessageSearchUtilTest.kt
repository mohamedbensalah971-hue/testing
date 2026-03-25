import org.junit.jupiter.api.Assertions.*
import com.quickchat.app.util.MessageSearchUtil
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MessageSearchUtilTest {

    @Test
    fun `searchMessages with query and no sender filter returns matching messages`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello world", 1643723400),
            Message("2", "conversation1", "sender2", "Hello again", 1643723410),
            Message("3", "conversation1", "sender1", "Goodbye world", 1643723420)
        )
        val query = "hello"

        // When
        val result = MessageSearchUtil.searchMessages(messages, query)

        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.content.contains("Hello world", true) })
        assertTrue(result.any { it.content.contains("Hello again", true) })
    }

    @Test
    fun `searchMessages with query and sender filter returns matching messages from sender`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello world", 1643723400),
            Message("2", "conversation1", "sender2", "Hello again", 1643723410),
            Message("3", "conversation1", "sender1", "Goodbye world", 1643723420)
        )
        val query = "hello"
        val senderFilter = "sender1"

        // When
        val result = MessageSearchUtil.searchMessages(messages, query, senderFilter = senderFilter)

        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.content.contains("Hello world", true) })
        assertTrue(result.any { it.content.contains("Goodbye world", true) })
    }

    @Test
    fun `filterByDateRange returns messages within time range`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello world", 1643723400),
            Message("2", "conversation1", "sender2", "Hello again", 1643723410),
            Message("3", "conversation1", "sender1", "Goodbye world", 1643723420)
        )
        val startTime = 1643723400
        val endTime = 1643723415

        // When
        val result = MessageSearchUtil.filterByDateRange(messages, startTime, endTime)

        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.timestamp == 1643723400L })
        assertTrue(result.any { it.timestamp == 1643723410L })
    }

    @Test
    fun `getConversationMessages returns messages from conversation`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello world", 1643723400),
            Message("2", "conversation1", "sender2", "Hello again", 1643723410),
            Message("3", "conversation2", "sender1", "Goodbye world", 1643723420)
        )
        val conversationId = "conversation1"

        // When
        val result = MessageSearchUtil.getConversationMessages(messages, conversationId)

        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.conversationId == conversationId })
    }

    @Test
    fun `countUnreadBySender returns unread message count by sender`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello world", 1643723400, isRead = false),
            Message("2", "conversation1", "sender2", "Hello again", 1643723410, isRead = false),
            Message("3", "conversation1", "sender1", "Goodbye world", 1643723420, isRead = true)
        )
        val conversationId = "conversation1"

        // When
        val result = MessageSearchUtil.countUnreadBySender(messages, conversationId)

        // Then
        assertEquals(2, result.size)
        assertTrue(result.contains("sender1"))
        assertTrue(result.contains("sender2"))
    }

    @Test
    fun `searchMessages with empty query returns empty list`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello world", 1643723400),
            Message("2", "conversation1", "sender2", "Hello again", 1643723410),
            Message("3", "conversation1", "sender1", "Goodbye world", 1643723420)
        )
        val query = ""

        // When
        val result = MessageSearchUtil.searchMessages(messages, query)

        // Then
        assertTrue(result.isEmpty())
    }
}