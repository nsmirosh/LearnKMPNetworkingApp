package com.learnkmp.networking.helpers

import com.learnkmp.networking.BuildKonfig
import com.learnkmp.networking.models.Note
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

expect fun createPlatformHttpClient(): HttpClient

fun createHttpClient() =
    (if (BuildKonfig.useMockServer) buildMockClient() else createPlatformHttpClient())
        .config {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                })
            }
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }


private fun buildMockClient(): HttpClient {

    var blobUrlsWithBlobData = mutableMapOf<String, Note>()
    var latestBlobId = 1
    return HttpClient(MockEngine { request ->
        val body = when (val content = request.body) {
            is io.ktor.http.content.TextContent -> content.text
            is io.ktor.http.content.ByteArrayContent -> content.bytes().decodeToString()
            else -> content.toString()
        }

        val (content, headers) = if (request.method == io.ktor.http.HttpMethod.Get) {
            delay(500)
            val path = request.url.toString()

            Pair(
                Json.encodeToString(
                    Note.serializer(),
                    blobUrlsWithBlobData[path] as Note
                ),
                headersOf(HttpHeaders.ContentType, "application/json")
            )
        } else {
            val blobUrl = "http://www.jsonblob.com/api/jsonBlob/${latestBlobId++}"
            val newBlobUrl = blobUrl.replace("http", "https")
            val note = Json.decodeFromString(Note.serializer(), body)
            blobUrlsWithBlobData[newBlobUrl] = note
            Pair(
                "",
                headersOf(
                    Pair(HttpHeaders.ContentType, listOf("application/json")),
                    Pair("Location", listOf(blobUrl))
                )
            )
        }
        respond(
            content = content,
            headers = headers
        )
    })
}