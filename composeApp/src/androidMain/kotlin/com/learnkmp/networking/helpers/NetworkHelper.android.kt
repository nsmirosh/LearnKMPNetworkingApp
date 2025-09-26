package com.learnkmp.networking.helpers

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual fun createPlatformHttpClient() = HttpClient(OkHttp)

