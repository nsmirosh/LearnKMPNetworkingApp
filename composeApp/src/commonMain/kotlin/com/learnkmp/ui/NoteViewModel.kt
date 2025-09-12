import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnkmp.networking.helpers.createPlatformHttpClient
import com.learnkmp.networking.helpers.createPlatformHttpClient2
import com.learnkmp.networking.models.Note
import com.learnkmp.networking.models.Metadata
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val BLOB_WEBSITE_URL = "https://www.jsonblob.com/api/jsonBlob"
const val MAX_MESSAGES = 3

class NoteViewModel : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    val blobUrls = mutableListOf<String>()
    val client = createPlatformHttpClient { blobUrl ->
        val newBlobUrls = (blobUrls + blobUrl).takeLast(MAX_MESSAGES)
        blobUrls.clear()
        blobUrls.addAll(newBlobUrls)
        _statusMessage.value = "✅ Note sent successfully!"
    }

    suspend fun fetchAllNotes() {
        try {
            val fetchedNotes = mutableListOf<Note>()
            blobUrls.forEach { url ->
                try {
                    val note: Note = client.get(url).body()
                    fetchedNotes.add(note)
                } catch (e: Exception) {
                    // Ignore individual fetch failures
                }
            }
            _notes.value = fetchedNotes
        } catch (e: Exception) {
            _statusMessage.value = "❌ Failed to fetch notes: ${e.message}"
        }
    }

    fun sendNote(message: String, author: String, tags: String = "") {
        viewModelScope.launch {
            try {
                val tagsList = if (tags.isBlank()) null else tags.split(",").map { it.trim() }
                    .filter { it.isNotEmpty() }

                val metadata = Metadata(
                    tags = tagsList
                )

                val note = Note(
                    message = message,
                    author = author.ifBlank { null },
                    metadata = metadata
                )

                client.post(BLOB_WEBSITE_URL) {
                    contentType(ContentType.Application.Json)
                    setBody(note)
                }

                // Fetch all notes after sending
                fetchAllNotes()

            } catch (e: Exception) {
                _statusMessage.value = "❌ Failed to send note: ${e.message}"
            }
        }
    }
}