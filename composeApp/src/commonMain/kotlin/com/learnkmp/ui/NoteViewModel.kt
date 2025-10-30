import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnkmp.networking.helpers.createPlatformHttpClient
import com.learnkmp.networking.models.Note
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val BLOB_WEBSITE_URL = "https://api.jsonblob.com"
const val MAX_MESSAGES = 3

class NoteViewModel : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    val blobUrls = mutableListOf<String>()
    val client = createPlatformHttpClient()

    suspend fun fetchAllNotes() {
        try {
            val fetchedNotes = mutableListOf<Note>()
            blobUrls.forEach { url ->
                delay(200L)
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


                //TODO use tagsList above to add tags to your Metadata

                val note = Note(
                    message = message,
                    author = author.ifBlank { null },
                )

                val response = client.post(BLOB_WEBSITE_URL) {
                    contentType(ContentType.Application.Json)
                    setBody(note)
                }

                response.headers["Location"]?.let { blobUrl ->
                    blobUrls.add("$BLOB_WEBSITE_URL/$blobUrl")
                    val newBlobUrls = blobUrls.takeLast(MAX_MESSAGES)
                    blobUrls.clear()
                    blobUrls.addAll(newBlobUrls)
                    _statusMessage.value = "✅ Note sent successfully!"
                }

                // Fetch all notes after sending
                fetchAllNotes()

            } catch (e: Exception) {
                _statusMessage.value = "❌ Failed to send note: ${e.message}"
            }
        }
    }
}