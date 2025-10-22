package com.learnkmp.networking.helpers

import com.learnkmp.networking.BuildKonfig
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


private fun buildMockClient() =
    HttpClient(MockEngine { request ->
        println("Request: $request")
        //https://medium.com/granular-engineering/mocking-http-calls-with-ktor-mockengine-3269fd807880
        respond(
            content = """{"message":"balls"}""",
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    })
