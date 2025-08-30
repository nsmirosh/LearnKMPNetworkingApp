package com.learnkmp.networking.helpers

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.Interceptor

actual fun createPlatformHttpClient(): HttpClient = HttpClient(OkHttp) {
    engine {
        addInterceptor(
            LoggingInterceptor()
        )
    }
}

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}
