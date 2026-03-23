import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.time.Instant

class MessageSearchUtilTest {

    @Test
    fun `searchMessages with query and no sender filter returns matching messages`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello world", Instant.now().toEpochMilli()),
            Message("2", "conversation1", "sender2", "Hello again", Instant.now().toEpochMilli() - 1000),
            Message("3", "conversation1", "sender1", "Goodbye world", Instant.now().toEpochMilli() - 2000)
        )
        val query = "hello"

        // When
        val result = MessageSearchUtil.searchMessages(messages, query)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result[0].content).isEqualTo("Hello world")
        assertThat(result[1].content).isEqualTo("Hello again")
    }

    @Test
    fun `searchMessages with query and sender filter returns matching messages from sender`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello world", Instant.now().toEpochMilli()),
            Message("2", "conversation1", "sender2", "Hello again", Instant.now().toEpochMilli() - 1000),
            Message("3", "conversation1", "sender1", "Goodbye world", Instant.now().toEpochMilli() - 2000)
        )
        val query = "hello"
        val senderFilter = "sender1"

        // When
        val result = MessageSearchUtil.searchMessages(messages, query, senderFilter = senderFilter)

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].content).isEqualTo("Hello world")
    }

    @Test
    fun `searchMessages with empty query returns empty list`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello world", Instant.now().toEpochMilli()),
            Message("2", "conversation1", "sender2", "Hello again", Instant.now().toEpochMilli() - 1000),
            Message("3", "conversation1", "sender1", "Goodbye world", Instant.now().toEpochMilli() - 2000)
        )
        val query = ""

        // When
        val result = MessageSearchUtil.searchMessages(messages, query)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `filterByDateRange returns messages within date range`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello world", Instant.now().toEpochMilli()),
            Message("2", "conversation1", "sender2", "Hello again", Instant.now().toEpochMilli() - 1000),
            Message("3", "conversation1", "sender1", "Goodbye world", Instant.now().toEpochMilli() - 2000)
        )
        val startTime = Instant.now().toEpochMilli() - 1500
        val endTime = Instant.now().toEpochMilli()

        // When
        val result = MessageSearchUtil.filterByDateRange(messages, startTime, endTime)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result[0].content).isEqualTo("Hello world")
        assertThat(result[1].content).isEqualTo("Hello again")
    }

    @Test
    fun `getConversationMessages returns messages from conversation`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello world", Instant.now().toEpochMilli()),
            Message("2", "conversation1", "sender2", "Hello again", Instant.now().toEpochMilli() - 1000),
            Message("3", "conversation2", "sender1", "Goodbye world", Instant.now().toEpochMilli() - 2000)
        )
        val conversationId = "conversation1"

        // When
        val result = MessageSearchUtil.getConversationMessages(messages, conversationId)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result[0].content).isEqualTo("Hello again")
        assertThat(result[1].content).isEqualTo("Hello world")
    }

    @Test
    fun `countUnreadBySender returns unread message count by sender`() {
        // Given
        val messages = listOf(
            Message("1", "conversation1", "sender1", "Hello world", Instant.now().toEpochMilli(), isRead = false),
            Message("2", "conversation1", "sender2", "Hello again", Instant.now().toEpochMilli() - 1000, isRead = false),
            Message("3", "conversation1", "sender1", "Goodbye world", Instant.now().toEpochMilli() - 2000, isRead = true)
        )
        val conversationId = "conversation1"

        // When
        val result = MessageSearchUtil.countUnreadBySender(messages, conversationId)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result["sender1"]).isEqualTo(1)
        assertThat(result["sender2"]).isEqualTo(1)
    }
}