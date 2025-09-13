package com.learnkmp.networking.helpers

import com.learnkmp.networking.models.Note
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.body
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.http.HttpMethod
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

        //Alternative to interceptor below
//        install(ResponseObserver) {
//            onResponse { response ->
//                if (response.call.request.method == HttpMethod.Post) {
//                    response.headers["Location"]?.replace("http", "https")
//                        ?.let { onNewBlobUrl(it) }
//                }
//            }
//        }
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
    client.plugin(HttpSend).intercept { request ->
        val call = execute(request)
        if (request.method == HttpMethod.Post) {
            call.response.headers["Location"]?.replace("http", "https")?.let { onNewBlobUrl(it) }
        }
        call
    }


    return client
}
