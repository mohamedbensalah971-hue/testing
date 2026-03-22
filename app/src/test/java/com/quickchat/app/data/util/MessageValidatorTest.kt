import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class MessageValidatorTest {

    @Test
    fun `validateMessage returns null for empty string`() {
        // Given
        val input = ""
        // When
        val result = validateMessage(input)
        // Then
        assertNull(result)
    }

    @Test
    fun `validateMessage returns null for string longer than 1000 characters`() {
        // Given
        val input = "a".repeat(1001)
        // When
        val result = validateMessage(input)
        // Then
        assertNull(result)
    }

    @Test
    fun `validateMessage returns trimmed string for non-empty string`() {
        // Given
        val input = "   Hello World   "
        // When
        val result = validateMessage(input)
        // Then
        assertEquals("Hello World", result)
    }

    @Test
    fun `validateMessage returns null for whitespace-only string`() {
        // Given
        val input = "   "
        // When
        val result = validateMessage(input)
        // Then
        assertNull(result)
    }

    @Test
    fun `validateMessage returns original string for single-character string`() {
        // Given
        val input = "a"
        // When
        val result = validateMessage(input)
        // Then
        assertEquals("a", result)
    }

    @Test
    fun `validateMessage returns original string for string with length less than 1000 characters`() {
        // Given
        val input = "a".repeat(999)
        // When
        val result = validateMessage(input)
        // Then
        assertEquals(input, result)
    }
}