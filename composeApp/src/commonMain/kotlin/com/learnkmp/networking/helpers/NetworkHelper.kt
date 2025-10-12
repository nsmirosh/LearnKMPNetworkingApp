package com.learnkmp.networking.helpers

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect fun createPlatformHttpClient(): HttpClient

fun createHttpClient() = createPlatformHttpClient().config {

    install(ContentNegotiation) {
        json()
    }
}