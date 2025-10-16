import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnkmp.networking.helpers.createHttpClient
import com.learnkmp.networking.helpers.createPlatformHttpClient
import com.learnkmp.networking.models.Note
import com.learnkmp.networking.models.Metadata
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val BLOB_WEBSITE_URL = "https://www.jsonblob.com/api/jsonBlob"
const val MAX_MESSAGES = 3

class NoteViewModel : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    val blobUrls = mutableListOf<String>()
    val client = createHttpClient()

    suspend fun fetchAllNotes() {
        try {
            val fetchedNotes = mutableListOf<Note>()
            blobUrls.forEach { url ->
                try {
                    val note: Note = client.get(url).body()
                    fetchedNotes.add(note)
                } catch (e: Exception) {
                    println("Failed to fetch note from $url: ${e.message}")
                    println(e.printStackTrace())
                }
            }
            withContext(Dispatchers.Main) {
                _notes.value = fetchedNotes
            }
        } catch (e: Exception) {
            _statusMessage.value = "❌ Failed to fetch notes: ${e.message}"
        }
    }

    fun sendNote(message: String, author: String, tags: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                client.get("https://www.jsonblob.com/")
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

                val response = client.post(BLOB_WEBSITE_URL) {
                    contentType(ContentType.Application.Json)
                    setBody(note)
                }

                response.headers["Location"]?.replace("http", "https")?.let { blobUrl ->
                    val newBlobUrls = (blobUrls + blobUrl).takeLast(MAX_MESSAGES)
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