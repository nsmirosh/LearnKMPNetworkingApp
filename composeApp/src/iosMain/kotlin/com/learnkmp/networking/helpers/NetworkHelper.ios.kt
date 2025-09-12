package com.learnkmp.networking.helpers

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.plugins.plugin
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun createPlatformHttpClient(onNewBlobUrl: (String) -> Unit): HttpClient =
    HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        install(ResponseObserver) {
            onResponse { response ->
                if (response.call.request.method == HttpMethod.Post) {
                    response.headers["Location"]?.replace("http", "https")
                        ?.let { onNewBlobUrl(it) }
                }
            }
        }
    }

actual fun createPlatformHttpClient2(onNewBlobUrl: (String) -> Unit): HttpClient {
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
        val call = execute(request)
        if (request.method == HttpMethod.Post) {
            call.response.headers["Location"]?.replace("http", "https")?.let { onNewBlobUrl(it) }
        }
        call
    }
    return client
}
