package com.learnkmp.networking.helpers
import io.ktor.client.*

expect fun createPlatformHttpClient(onNewBlobUrl: (String) -> Unit = {}): HttpClient