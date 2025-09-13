package com.learnkmp.networking.helpers

import com.learnkmp.networking.models.Note
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.body
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun createPlatformHttpClient(onNewBlobUrl: (String) -> Unit): HttpClient {
    val client = HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    client.plugin(HttpSend).intercept { request ->
        // Simple request log
        println("[HTTP] -> ${request.method.value} ${request.url}")
        println("[HTTP] -> ${request.body}")

        val call: HttpClientCall = execute(request)

        // Simple response log
        println("[HTTP] <- ${call.response.status.value} ${call.request.url}")
        val note = call.response.body<Note>()
        println("[HTTP] <- $note")
        call
    }

    //TODO define your interceptor here and use the onNewBlobUrl() callback to update the blobUrls
    // in the NoteViewModel

    return client
}
