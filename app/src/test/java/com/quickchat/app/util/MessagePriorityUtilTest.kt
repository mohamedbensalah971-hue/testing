import org.junit.jupiter.api.Assertions.*
import com.quickchat.app.util.MessagePriorityUtil
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

data class Message(val isRead: Boolean, val timestamp: Long, val content: String)

class MessagePriorityUtilTest {

    @Test
    fun `calculatePriority - unread message with recent timestamp and urgent content should have high priority score`() {
        // Given
        val message = Message(false, System.currentTimeMillis(), "This is an urgent message")

        // When
        val priorityScore = MessagePriorityUtil.calculatePriority(message)

        // Then
        assertTrue(priorityScore >= 22)
    }

    @Test
    fun `calculatePriority - read message with old timestamp and non-urgent content should have low priority score`() {
        // Given
        val message = Message(true, System.currentTimeMillis() - 86400000, "This is a non-urgent message")

        // When
        val priorityScore = MessagePriorityUtil.calculatePriority(message)

        // Then
        assertEquals(0, priorityScore)
    }

    @Test
    fun `calculatePriority - unread message with recent timestamp and non-urgent content should have medium priority score`() {
        // Given
        val message = Message(false, System.currentTimeMillis(), "This is a non-urgent message")

        // When
        val priorityScore = MessagePriorityUtil.calculatePriority(message)

        // Then
        assertEquals(15, priorityScore)
    }

    @Test
    fun `sortByPriority - messages should be sorted in descending order of priority score`() {
        // Given
        val message1 = Message(false, System.currentTimeMillis(), "This is an urgent message")
        val message2 = Message(true, System.currentTimeMillis() - 86400000, "This is a non-urgent message")
        val message3 = Message(false, System.currentTimeMillis(), "This is a non-urgent message")

        // When
        val sortedMessages = MessagePriorityUtil.sortByPriority(listOf(message1, message2, message3))

        // Then
        assertEquals(message1, sortedMessages[0])
        assertEquals(message3, sortedMessages[1])
        assertEquals(message2, sortedMessages[2])
    }

    @Test
    fun `calculatePriority - edge case with empty content should not throw exception`() {
        // Given
        val message = Message(false, System.currentTimeMillis(), "")

        // When
        val priorityScore = MessagePriorityUtil.calculatePriority(message)

        // Then
        assertEquals(15, priorityScore)
    }

    @Test
    fun `calculatePriority - edge case with null content should throw exception`() {
        // Given
        val message = Message(false, System.currentTimeMillis(), null)

        // When and Then
        assertThrows(NullPointerException::class.java) {
            MessagePriorityUtil.calculatePriority(message)
        }
    }
}