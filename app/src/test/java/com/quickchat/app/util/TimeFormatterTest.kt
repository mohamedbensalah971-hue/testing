import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TimeFormatterTest {

    @Test
    fun `formatRelative - just now`() {
        // Given
        val timestamp = System.currentTimeMillis()

        // When
        val result = TimeFormatter.formatRelative(timestamp)

        // Then
        assertThat(result).isEqualTo("Just now")
    }

    @Test
    fun `formatRelative - less than 1 minute ago`() {
        // Given
        val timestamp = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1) / 2

        // When
        val result = TimeFormatter.formatRelative(timestamp)

        // Then
        assertThat(result).isEqualTo("0m ago")
    }

    @Test
    fun `formatRelative - less than 60 minutes ago`() {
        // Given
        val timestamp = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30)

        // When
        val result = TimeFormatter.formatRelative(timestamp)

        // Then
        assertThat(result).isEqualTo("30m ago")
    }

    @Test
    fun `formatRelative - yesterday`() {
        // Given
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.getDefault())
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val timestamp = calendar.timeInMillis

        // When
        val result = TimeFormatter.formatRelative(timestamp)

        // Then
        assertThat(result).isEqualTo("Yesterday")
    }

    @Test
    fun `formatTime - valid timestamp`() {
        // Given
        val timestamp = System.currentTimeMillis()

        // When
        val result = TimeFormatter.formatTime(timestamp)

        // Then
        val expected = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formatRelative - invalid timestamp`() {
        // Given
        val timestamp = 0L

        // When
        val result = TimeFormatter.formatRelative(timestamp)

        // Then
        assertThat(result).isEqualTo("")
    }
}