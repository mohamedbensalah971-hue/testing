import com.quickchat.app.ui.conversations.ConversationsActivity
import com.quickchat.app.ui.conversations.ConversationsAdapter
import com.quickchat.app.ui.conversations.ConversationsViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ConversationsActivityTest {

    private lateinit var activity: ConversationsActivity
    private lateinit var viewModel: ConversationsViewModel
    private lateinit var adapter: ConversationsAdapter

    @BeforeEachEach
    fun setup() {
        activity = ConversationsActivity()
        viewModel = mockk(relaxed = true)
        adapter = mockk(relaxed = true)
        activity.viewModel = viewModel
        activity.adapter = adapter
    }

    @Test
    fun `test setupToolbar sets title correctly`() {
        // Given
        val title = "QuickChat"

        // When
        activity.setupToolbar()

        // Then
        verify { activity.supportActionBar?.title = title }
    }

    @Test
    fun `test setupRecyclerView sets adapter correctly`() {
        // Given
        val recyclerView = mockk(relaxed = true)

        // When
        activity.setupRecyclerView()

        // Then
        verify { recyclerView.adapter = adapter }
    }

    @Test
    fun `test observeViewModel updates adapter with conversations list`() {
        // Given
        val conversations = listOf(mockk(relaxed = true), mockk(relaxed = true))

        // When
        every { viewModel.conversations.value } returns conversations
        activity.observeViewModel()

        // Then
        verify { adapter.submitList(conversations) }
    }

    @Test
    fun `test observeViewModel shows empty state when conversations list is empty`() {
        // Given
        val conversations = emptyList<Any>()
        every { viewModel.isEmpty.value } returns true

        // When
        activity.observeViewModel()

        // Then
        verify { activity.binding.textEmptyState.visibility = View.VISIBLE }
        verify { activity.binding.recyclerConversations.visibility = View.GONE }
    }

    @Test
    fun `test observeViewModel hides empty state when conversations list is not empty`() {
        // Given
        val conversations = listOf(mockk(relaxed = true))
        every { viewModel.isEmpty.value } returns false

        // When
        activity.observeViewModel()

        // Then
        verify { activity.binding.textEmptyState.visibility = View.GONE }
        verify { activity.binding.recyclerConversations.visibility = View.VISIBLE }
    }

    @Test
    fun `test setupRecyclerView starts ChatActivity when conversation is clicked`() {
        // Given
        val conversation = mockk(relaxed = true)
        val intent = Intent(activity, ChatActivity::class.java)

        // When
        adapter.onConversationClick(conversation)

        // Then
        verify { activity.startActivity(intent) }
    }
}