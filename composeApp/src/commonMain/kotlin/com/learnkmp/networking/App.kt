package com.learnkmp.networking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
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
fun MessageBoardScreen() {
    val client = remember { HttpClient(CIO) }
    var message by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    var blobUrl by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Helper function to handle API operations with consistent error handling
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

    // Helper function to create JSON body
    fun createJsonBody() = """{"message": "$message"}"""

    Column(modifier = Modifier.padding(top = 48.dp)) {
        TextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Blob URL = $blobUrl")
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                performApiOperation {
                    val response = client.post("https://www.jsonblob.com/api/jsonBlob") {
                        contentType(ContentType.Application.Json)
                        setBody(createJsonBody())
                    }
                    blobUrl = response.headers["Location"]?.replace("http", "https")
                    statusMessage = "✅ POST success! Blob created with URL = $blobUrl"
                }
            }) {
                Text("POST")
            }

            Button(onClick = {
                performApiOperation(requiresBlobUrl = true) {
                    val response = client.put(blobUrl!!) {
                        contentType(ContentType.Application.Json)
                        setBody(createJsonBody())
                    }
                    statusMessage =
                        "✅ PUT success! Status = ${response.status}. Response: ${response.bodyAsText()}"
                }
            }) {
                Text("PUT")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                performApiOperation(requiresBlobUrl = true) {
                    val response = client.get(blobUrl!!).bodyAsText()
                    statusMessage = "✅ GET success: $response"
                }
            }) {
                Text("GET")
            }

            Button(onClick = {
                performApiOperation(requiresBlobUrl = true) {
                    client.delete(blobUrl!!)
                    statusMessage = "✅ DELETE success. Blob deleted."
                    blobUrl = null // Reset blob URL after deletion
                }
            }) {
                Text("DELETE")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(statusMessage)
    }
}