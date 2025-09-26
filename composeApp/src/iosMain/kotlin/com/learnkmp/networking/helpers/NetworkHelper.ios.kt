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

actual fun createPlatformHttpClient() = HttpClient(Darwin )
