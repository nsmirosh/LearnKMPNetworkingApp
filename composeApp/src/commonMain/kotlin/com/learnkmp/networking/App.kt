package com.learnkmp.networking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learnkmp.networking.models.Note
import com.learnkmp.ui.NoteViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime


@Composable
@Preview
fun App() {
    MaterialTheme {
        NoteScreen()
    }
}

@Composable
@Preview
fun NoteScreen(viewModel: NoteViewModel = viewModel { NoteViewModel() }) {
    val notes by viewModel.notes.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    var noteText by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "📝 Note Taking App",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = noteText,
            onValueChange = { noteText = it },
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

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = tags,
            onValueChange = { tags = it },
            label = { Text("Tags (comma separated, optional)") },
            placeholder = { Text("work, urgent, ideas") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.sendNote(noteText, author, tags)
                noteText = ""
                author = ""
                tags = ""
            },
            enabled = noteText.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send")
        }

        Spacer(modifier = Modifier.height(16.dp))

        statusMessage?.let {
            Text(
                text = it,
                color = if (it.startsWith("✅")) MaterialTheme.colorScheme.primary
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
                items(notes.takeLast(3).reversed()) { note ->
                    NoteCard(note = note)
                }
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
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            note.metadata.tags?.let {
                Bubbles(it)
            }
        }
    }
}


@Composable
fun Bubbles(tags: List<String>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(tags) { tag ->
            Text(
                text = tag,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }

}