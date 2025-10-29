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

    // Helper function to handle API operations - don't modify
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
                // TODO
                //  Perform a GET operation here and save the response into a local `response` variable
                //  We're not aiming to parse the response as of now, but will simply print what it returns
                //  Hint: Use the `bodyAsText()` to get the response body as text.

                //implement here

                // TODO
                //  Uncomment the code below after you implemented the GET request above

//                statusMessage = "✅ GET success: $response"
            }

            ApiButton("POST") {
                //TODO
                // In order for us to work with something
                // We first need to post a value
                // Your task is to make a POST request to https://api.jsonblob.com
                // The content type should be JSON
                // In the body you should put the `createJsonBody()` function that
                // we created above which will take what we have in our TextField
                // and package it into the JSON format

                //implement here

                //TODO
                // After you have implemented the call above and got the response
                // in the Location header of your response you will get the Blob ID
                // of where JSON will be stored
                // Store this id in the blobUrl variable above like so  "https://api.jsonblob.com/${blobId}}"

                //TODO uncomment this once you implement the above functionality
//                statusMessage = "✅ POST success! Blob created with URL = $blobUrl"
            }

            ApiButton("PUT", requiresBlobUrl = true) {
                //TODO
                // Build a PUT request to the URL that we have saved in the `blobUrl` variable
                // Make sure to set the contentType as JSON
                // Use the `createJsonBody()` function that we have above for the body.
                // Save the response into a local `response` variable

                //implement here

                //TODO uncomment this after you get the response

//                statusMessage =
//                    "✅ PUT success! Status = ${response.status}. Response: ${response.bodyAsText()}"
            }

            ApiButton("DELETE", requiresBlobUrl = true) {
                // TODO
                //  Perform a DELETE with our `blobUrl` as path
                //  We don't need any response from the delete operation

                // implement here

                // TODO
                //  Uncomment this after you implement the delete operation above

//                statusMessage = "✅ DELETE success. Blob deleted."
//                blobUrl = null // Reset blob URL after deletion
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(statusMessage)
    }
}
