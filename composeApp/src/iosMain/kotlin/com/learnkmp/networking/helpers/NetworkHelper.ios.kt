package com.learnkmp.networking.helpers

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClient(): HttpClient {
    return HttpClient(Darwin)
}