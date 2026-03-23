import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class MessageValidatorTest {

    @Test
    fun `validateMessage returns null for empty string`() {
        // Given
        val input = ""
        // When
        val result = validateMessage(input)
        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `validateMessage returns null for string longer than 1000 characters`() {
        // Given
        val input = "a".repeat(1001)
        // When
        val result = validateMessage(input)
        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `validateMessage returns trimmed string for non-empty string`() {
        // Given
        val input = "   Hello, World!   "
        // When
        val result = validateMessage(input)
        // Then
        assertThat(result).isEqualTo("Hello, World!")
    }

    @Test
    fun `validateMessage returns null for string containing only whitespace characters`() {
        // Given
        val input = "   "
        // When
        val result = validateMessage(input)
        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `validateMessage returns string for single character input`() {
        // Given
        val input = "a"
        // When
        val result = validateMessage(input)
        // Then
        assertThat(result).isEqualTo("a")
    }

    @Test
    fun `validateMessage returns string for string with length 1000 characters`() {
        // Given
        val input = "a".repeat(1000)
        // When
        val result = validateMessage(input)
        // Then
        assertThat(result).isEqualTo("a".repeat(1000))
    }
}