import com.quickchat.app.ui.chat.ChatActivity
import com.quickchat.app.ui.chat.ChatViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ChatActivityTest {

    private lateinit var activity: ChatActivity
    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: ActivityChatBinding

    @BeforeEachEach
    fun setup() {
        activity = ChatActivity()
        viewModel = mockk(relaxUnitFun = true)
        binding = ActivityChatBinding.inflate(activity.layoutInflater)
        activity.binding = binding
        activity.viewModel = viewModel
    }

    @Test
    fun `test onCreate with valid conversationId`() {
        // Given
        val conversationId = "123"
        every { activity.intent.getStringExtra(any()) } returns conversationId

        // When
        activity.onCreate(null)

        // Then
        verify { viewModel.loadChat(conversationId) }
    }

    @Test
    fun `test onCreate with invalid conversationId`() {
        // Given
        every { activity.intent.getStringExtra(any()) } returns null

        // When
        activity.onCreate(null)

        // Then
        verify { activity.finish() }
    }

    @Test
    fun `test setupToolbar`() {
        // Given

        // When
        activity.setupToolbar()

        // Then
        verify { activity.setSupportActionBar(binding.toolbar) }
        verify { activity.supportActionBar?.setDisplayHomeAsUpEnabled(true) }
    }

    @Test
    fun `test setupRecyclerView`() {
        // Given

        // When
        activity.setupRecyclerView()

        // Then
        verify { binding.recyclerMessages.layoutManager = any() }
        verify { binding.recyclerMessages.adapter = any() }
    }

    @Test
    fun `test setupInput with valid message`() {
        // Given
        val message = "Hello"
        every { binding.editMessage.text } returns message

        // When
        activity.setupInput()
        binding.buttonSend.performClick()

        // Then
        verify { viewModel.sendMessage(message) }
    }

    @Test
    fun `test setupInput with empty message`() {
        // Given
        every { binding.editMessage.text } returns ""

        // When
        activity.setupInput()
        binding.buttonSend.performClick()

        // Then
        verify { viewModel.sendMessage(any()) wasNot Called }
    }
}