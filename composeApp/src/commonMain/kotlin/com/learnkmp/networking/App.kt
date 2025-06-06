package com.learnkmp.networking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        MessageBoardScreen()
    }
}

@Composable
@Preview
fun MessageBoardScreen() {
    val client = remember { HttpClient(CIO) }
    var message by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    var blobUrl by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Helper function to handle API operations - no need to modify
    fun performApiOperation(
        requiresBlobUrl: Boolean = false,
        operation: suspend () -> Unit
    ) {
        coroutineScope.launch {
            if (requiresBlobUrl && blobUrl == null) {
                statusMessage = "❌ Error: No blob exists. Use POST first."
                return@launch
            }
            try {
                operation()
            } catch (e: Exception) {
                statusMessage = "❌ Operation failed: ${e.message}"
            }
        }
    }

    // Helper function to create JSON body - don't modify
    fun createJsonBody() = """{"message": "$message"}"""

    @Composable
    fun ApiButton(
        text: String,
        requiresBlobUrl: Boolean = false,
        onClick: suspend () -> Unit
    ) {
        Button(onClick = { performApiOperation(requiresBlobUrl, onClick) }) {
            Text(text)
        }
    }

    Column(modifier = Modifier.padding(top = 48.dp)) {
        TextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ApiButton("GET", requiresBlobUrl = true) {
                blobUrl?.let { url ->
                    val response = client.get(url).bodyAsText()

                    statusMessage = "✅ GET success: $response"
                }
            }

            ApiButton("POST") {
                val response = client.post("https://www.jsonblob.com/api/jsonBlob") {
                    contentType(ContentType.Application.Json)
                    setBody(createJsonBody())
                }

                blobUrl = response.headers["Location"]?.replace("http", "https")

                statusMessage = "✅ POST success! Blob created with URL = $blobUrl"
            }

            ApiButton("PUT", requiresBlobUrl = true) {
                val response = client.put(blobUrl!!) {
                    contentType(ContentType.Application.Json)
                    setBody(createJsonBody())
                }

                statusMessage =
                    "✅ PUT success! Status = ${response.status}. Response: ${response.bodyAsText()}"
            }

            ApiButton("DELETE", requiresBlobUrl = true) {
                client.delete(blobUrl!!)

                statusMessage = "✅ DELETE success. Blob deleted."
                blobUrl = null // Reset blob URL after deletion
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(statusMessage)
    }
}
