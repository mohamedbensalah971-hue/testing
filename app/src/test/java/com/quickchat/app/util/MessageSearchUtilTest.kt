import org.junit.jupiter.api.Assertions.*
import com.quickchat.app.util.MessageSearchUtil
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MessageSearchUtilTest {

    @Test
    fun `searchMessages returns empty list when query is blank`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello", 1643723400),
            Message("2", "conversation1", "sender2", "World", 1643723401)
        )
        val query = ""

        // When
        val result = MessageSearchUtil.searchMessages(messages, query)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `searchMessages returns filtered messages when query matches content`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello World", 1643723400),
            Message("2", "conversation1", "sender2", "Hello", 1643723401),
            Message("3", "conversation1", "sender3", "World", 1643723402)
        )
        val query = "hello"

        // When
        val result = MessageSearchUtil.searchMessages(messages, query)

        // Then
        assertEquals(3, result.size)
    }

    @Test
    fun `searchMessages returns filtered messages when sender filter is applied`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello World", 1643723400),
            Message("2", "conversation1", "sender2", "Hello", 1643723401),
            Message("3", "conversation1", "sender1", "World", 1643723402)
        )
        val query = "hello"
        val senderFilter = "sender1"

        // When
        val result = MessageSearchUtil.searchMessages(messages, query, senderFilter = senderFilter)

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `filterByDateRange returns messages within the specified time range`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello World", 1643723400),
            Message("2", "conversation1", "sender2", "Hello", 1643723401),
            Message("3", "conversation1", "sender3", "World", 1643723402),
            Message("4", "conversation1", "sender4", "Test", 1643723403)
        )
        val startTime = 1643723400
        val endTime = 1643723402

        // When
        val result = MessageSearchUtil.filterByDateRange(messages, startTime, endTime)

        // Then
        assertEquals(3, result.size)
    }

    @Test
    fun `getConversationMessages returns messages from the specified conversation`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello World", 1643723400),
            Message("2", "conversation1", "sender2", "Hello", 1643723401),
            Message("3", "conversation2", "sender3", "World", 1643723402)
        )
        val conversationId = "conversation1"

        // When
        val result = MessageSearchUtil.getConversationMessages(messages, conversationId)

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `countUnreadBySender returns unread message count by sender`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello World", 1643723400, isRead = false),
            Message("2", "conversation1", "sender2", "Hello", 1643723401, isRead = true),
            Message("3", "conversation1", "sender1", "World", 1643723402, isRead = false)
        )
        val conversationId = "conversation1"

        // When
        val result = MessageSearchUtil.countUnreadBySender(messages, conversationId)

        // Then
        assertEquals(2, result["sender1"])
        assertEquals(0, result["sender2"])
    }
}