package com.learnkmp.networking.helpers

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun createPlatformHttpClient(): HttpClient = HttpClient(Darwin) {
    engine {
        //configure a challenge handler for NSURLSession.
        handleChallenge {
                        session,
                        task,
                        challenge,
                        completionHandler ->
        }
    }
}

//    install(ContentNegotiation) {
//        json(Json {
//            prettyPrint = true
//            isLenient = true
//            ignoreUnknownKeys = true
//        })
//    }
//}
