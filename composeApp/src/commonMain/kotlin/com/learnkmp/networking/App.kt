package com.learnkmp.networking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.learnkmp.networking.helpers.createPlatformHttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

import org.jetbrains.compose.ui.tooling.preview.Preview


const val BLOB_WEBSITE_URL = "https://www.jsonblob.com/api/jsonBlob"
const val MAX_MESSAGES = 3

@Serializable
data class Note(
    val message: String,
    val author: String? = null,
)

@Composable
@Preview
fun App() {
    MaterialTheme {
        NoteTakingScreen()
    }
}

@Composable
@Preview
fun NoteTakingScreen() {
    val client = remember { createPlatformHttpClient() }
    var message by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    var notesBlobUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    suspend fun fetchAllNotes() {
        try {
            val fetchedNotes = mutableListOf<Note>()
            notesBlobUrls.forEach { url ->
                try {
                    val note: Note = client.get(url).body()
                    fetchedNotes.add(note)
                } catch (e: Exception) {
                    // Ignore individual fetch failures
                }
            }
            notes = fetchedNotes
        } catch (e: Exception) {
            statusMessage = "❌ Failed to fetch notes: ${e.message}"
        }
    }

    fun sendNote() {
        coroutineScope.launch {
            try {
//                val timestamp = Clock.System.now().toString()
                val note = Note(
                    message = message,
                    author = author.ifBlank { null },
//                    timestamp = timestamp
                )

                val response = client.post(BLOB_WEBSITE_URL) {
                    contentType(ContentType.Application.Json)
                    setBody(note)
                }

                response.headers["Location"]?.replace("http", "https")?.let { blobUrl ->
                    //Taking only the last 3 messages to avoid making too many network calls at once.
                    notesBlobUrls = (notesBlobUrls + blobUrl).takeLast(MAX_MESSAGES)
                    statusMessage = "✅ Note sent successfully!"
                    message = ""
                    author = ""
                }

                // Fetch all notes after sending
                fetchAllNotes()

            } catch (e: Exception) {
                statusMessage = "❌ Failed to send note: ${e.message}"
            }
        }
    }


    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "📝 Note Taking App",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Author (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { sendNote() },
            enabled = message.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                color = if (statusMessage.startsWith("✅")) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (notes.isNotEmpty()) {
            Text(
                text = "Recent Notes",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes) { NoteCard(it) }
            }
        }
    }
}

@Composable
fun NoteCard(note: Note) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.message,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (note.author != null) {
                Text(
                    text = "— ${note.author}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

//            Text(
//                text = formatTimestamp(note.timestamp),
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
        }
    }
}
//TODO implement automatic serialization to Dates / other objects

fun formatTimestamp(timestamp: String): String {
    return try {
        val instant = Instant.parse(timestamp)
        instant.toString().replace("T", " ").replace("Z", "").substringBefore(".")
    } catch (e: Exception) {
        timestamp
    }
}
