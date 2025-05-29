package com.learnkmp.networking


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*

@Composable
@Preview
fun App2() {
    MaterialTheme {
        MessageBoardScreen2()
    }
}

private fun buildKtor() {
    val client = HttpClient(CIO)
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    coroutineScope.launch {
        val response: HttpResponse = client.get("http://example.com/")
        println("response.status = ${response.status}")
    }
}


@Composable
fun MessageBoardScreen2() {
    val client = remember { HttpClient(CIO) }
    var message by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    var blobUrl by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    //TODO BUILD A NOTE APP WITH CUSTOM URLS IN JSONBLOB.COM

    Column(modifier = Modifier.padding(top = 48.dp)) {
        TextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth()
        )
        Text("block URL = $blobUrl")

        Spacer(modifier = Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                coroutineScope.launch {
                    try {
                        val response = client.post("https://www.jsonblob.com/api/jsonBlob") {
                            contentType(ContentType.Application.Json)
                            setBody("""{"message": "$message"}""")
                        }
                        blobUrl = response.headers["Location"]?.replace("http", "https")
                        statusMessage = "✅ POST success! Blob created with url = $blobUrl."
                    } catch (e: Exception) {
                        statusMessage = "❌ POST failed: ${e.message}"
                    }
                }
            }) {
                Text("POST")
            }

            Button(onClick = {
                coroutineScope.launch {
                    if (blobUrl == null) {
                        statusMessage = "❌ Error: No blob exists. Use POST first."
                        return@launch
                    }
                    try {
                        val response = client.put(blobUrl!!) {
                            contentType(ContentType.Application.Json)
                            setBody("""{"message": "$message"}""")
                        }

                        statusMessage =
                            "✅ PUT status = ${response.status}! Message updated for $blobUrl. response body = ${response.bodyAsText()}"
                    } catch (e: Exception) {
                        statusMessage = "❌ PUT failed: ${e.message}"
                    }
                }
            }) {
                Text("PUT")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                coroutineScope.launch {
                    if (blobUrl == null) {
                        statusMessage = "❌ Error: No blob exists."
                        return@launch
                    }
                    try {
                        val response: String = client.get(blobUrl!!).bodyAsText()
                        statusMessage = "✅ GET success: $response"
                    } catch (e: Exception) {
                        statusMessage = "❌ GET failed: ${e.message}"
                    }
                }
            }) {
                Text("GET")
            }

            Button(onClick = {
                coroutineScope.launch {
                    if (blobUrl == null) {
                        statusMessage = "❌ Error: No blob to delete."
                        return@launch
                    }
                    try {
                        client.delete(blobUrl!!)
                        statusMessage = "✅ DELETE success. Blob deleted."
                    } catch (e: Exception) {
                        statusMessage = "❌ DELETE failed: ${e.message}"
                    }
                }
            }) {
                Text("DELETE")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(statusMessage)
    }
}