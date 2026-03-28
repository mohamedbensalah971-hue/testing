import com.google.common.truth.Truth.assertThat
import com.quickchat.app.data.model.Message
import com.quickchat.app.ui.chat.MessagesAdapter
import com.quickchat.app.util.TimeFormatter
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class MessagesAdapterTest {

    private lateinit var adapter: MessagesAdapter
    private lateinit var message: Message

    @BeforeEachEach
    fun setup() {
        adapter = MessagesAdapter()
        message = Message(
            id = 1,
            content = "Hello, World!",
            isFromMe = true,
            timestamp = Instant.now().toEpochMilli()
        )
    }

    @Test
    fun `test getItemViewType returns VIEW_TYPE_SENT when message is from me`() {
        // Given
        val position = 0
        adapter.submitList(listOf(message))

        // When
        val viewType = adapter.getItemViewType(position)

        // Then
        assertThat(viewType).isEqualTo(MessagesAdapter.VIEW_TYPE_SENT)
    }

    @Test
    fun `test getItemViewType returns VIEW_TYPE_RECEIVED when message is not from me`() {
        // Given
        val position = 0
        message.isFromMe = false
        adapter.submitList(listOf(message))

        // When
        val viewType = adapter.getItemViewType(position)

        // Then
        assertThat(viewType).isEqualTo(MessagesAdapter.VIEW_TYPE_RECEIVED)
    }

    @Test
    fun `test onCreateViewHolder returns MessageViewHolder with correct layout resource`() {
        // Given
        val parent = mockk<ViewGroup>()
        val viewType = MessagesAdapter.VIEW_TYPE_SENT

        // When
        val holder = adapter.onCreateViewHolder(parent, viewType)

        // Then
        assertThat(holder.itemView.layoutResource).isEqualTo(R.layout.item_message_sent)
    }

    @Test
    fun `test onBindViewHolder binds message to MessageViewHolder correctly`() {
        // Given
        val holder = adapter.onCreateViewHolder(mockk(), MessagesAdapter.VIEW_TYPE_SENT) as MessagesAdapter.MessageViewHolder
        val messageText = "Hello, World!"
        val timestamp = Instant.now().toEpochMilli()
        message.content = messageText
        message.timestamp = timestamp

        // When
        adapter.onBindViewHolder(holder, 0)

        // Then
        assertThat(holder.itemView.findViewById<TextView>(R.id.text_message_content).text).isEqualTo(messageText)
        assertThat(holder.itemView.findViewById<TextView>(R.id.text_message_time).text).isEqualTo(TimeFormatter.formatTime(timestamp))
    }

    @Test
    fun `test MessageDiffCallback areItemsTheSame returns true when messages have same id`() {
        // Given
        val oldMessage = message
        val newMessage = Message(
            id = oldMessage.id,
            content = "New content",
            isFromMe = oldMessage.isFromMe,
            timestamp = oldMessage.timestamp
        )

        // When
        val result = MessagesAdapter.MessageDiffCallback().areItemsTheSame(oldMessage, newMessage)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `test MessageDiffCallback areContentsTheSame returns true when messages are equal`() {
        // Given
        val oldMessage = message
        val newMessage = Message(
            id = oldMessage.id,
            content = oldMessage.content,
            isFromMe = oldMessage.isFromMe,
            timestamp = oldMessage.timestamp
        )

        // When
        val result = MessagesAdapter.MessageDiffCallback().areContentsTheSame(oldMessage, newMessage)

        // Then
        assertThat(result).isTrue()
    }
}