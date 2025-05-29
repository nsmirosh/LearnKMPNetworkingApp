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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                performApiOperation(requiresBlobUrl = true) {
                    // TODO
                    //  4. Perform a GET operation
                    //  We're not aiming to parse the response as of now, but simply print what it returns
                    //  Hint: Use the `bodyAsText()` to get the response body as text.

                    val response = client.get(blobUrl!!).bodyAsText()
                    statusMessage = "✅ GET success: $response"
                }
            }) {
                Text("GET")
            }

            Button(onClick = {
                performApiOperation {

                    //TODO
                    // 1. In order for us to work with something
                    // we first need to post a value
                    // Your task is to build a POST with a json content type
                    // In the body you should put the `createJsonBody()` function that
                    // we created above which will represent the json we're trying to send

                    val response = client.post("https://www.jsonblob.com/api/jsonBlob") {
                        contentType(ContentType.Application.Json)
                        setBody(createJsonBody())
                    }
                    // we have to replace further calls with https for this to work
                    //TODO
                    // 2. In the header of our response we get a blob URL of our JSON
                    // We need to save it in order to perform further operations
                    // Your job is to retrieve this from the header and save it into the
                    // blobUrl variable that we have above.
                    // Also, you will need to replace the "http" part in the blob URL with "https"

                    blobUrl = response.headers["Location"]?.replace("http", "https")
                    statusMessage = "✅ POST success! Blob created with URL = $blobUrl"
                }
            }) {
                Text("POST")
            }

            Button(onClick = {
                performApiOperation(requiresBlobUrl = true) {
                    //TODO
                    // 3. Build a PUT operation that takes our blobUrl variable
                    // Make sure to set the contentType as JSON
                    // and the body should be the `createJsonBody()` function that we have above.

                    val response = client.put(blobUrl!!) {
                        contentType(ContentType.Application.Json)
                        setBody(createJsonBody())
                    }

//                    statusMessage =
//                        "✅ PUT success! Status = ${response.status}. Response: ${response.bodyAsText()}"
                }
            }) {
                Text("PUT")
            }

            Button(onClick = {
                performApiOperation(requiresBlobUrl = true) {
                    // TODO
                    //  5. Perform a DELETE operation by supplying our blobUrl as the urlString

                    client.delete(blobUrl!!)
                    statusMessage = "✅ DELETE success. Blob deleted."
                    blobUrl = null // Reset blob URL after deletion
                }
            }) {
                Text("DELETE")
            }

        Spacer(modifier = Modifier.height(8.dp))


        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(statusMessage)
    }
}