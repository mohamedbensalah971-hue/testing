import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MessageValidatorTest {

    @Test
    fun `validateMessage with empty string returns null`() {
        // Given
        val text = ""

        // When
        val result = validateMessage(text)

        // Then
        assertNull(result)
    }

    @Test
    fun `validateMessage with whitespace returns null`() {
        // Given
        val text = "   "

        // When
        val result = validateMessage(text)

        // Then
        assertNull(result)
    }

    @Test
    fun `validateMessage with valid string returns trimmed string`() {
        // Given
        val text = "   Hello World   "

        // When
        val result = validateMessage(text)

        // Then
        assertEquals("Hello World", result)
    }

    @Test
    fun `validateMessage with string longer than 1000 characters returns null`() {
        // Given
        val text = "a".repeat(1001)

        // When
        val result = validateMessage(text)

        // Then
        assertNull(result)
    }

    @Test
    fun `validateMessage with string of 1000 characters returns trimmed string`() {
        // Given
        val text = "a".repeat(1000)

        // When
        val result = validateMessage(text)

        // Then
        assertEquals("a".repeat(1000), result)
    }

    @Test
    fun `validateMessage with null string throws no exception and returns null`() {
        // Given
        val text: String? = null

        // When
        val result = validateMessage(text ?: "")

        // Then
        assertNull(result)
    }
}