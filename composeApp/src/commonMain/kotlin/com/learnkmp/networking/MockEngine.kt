package com.learnkmp.networking
//
//import io.ktor.client.HttpClient
//import io.ktor.client.engine.mock.MockEngine
//import io.ktor.client.request.HttpRequestBuilder
//import io.ktor.client.request.delete
//import io.ktor.client.request.get
//import io.ktor.client.request.post
//import io.ktor.client.request.put
//import io.ktor.client.statement.HttpResponse
//import io.ktor.http.HttpHeaders
//import io.ktor.http.headersOf
//import kotlinx.coroutines.delay
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//import kotlin.reflect.KProperty
//
//
//interface MockClient {
//
//    suspend fun get(
//        urlString: String,
//        block: HttpRequestBuilder.() -> Unit = {}
//    ): HttpResponse
//
//    suspend fun post(
//        urlString: String,
//        block: HttpRequestBuilder.() -> Unit = {}
//    ): HttpResponse
//
//    suspend fun put(
//        urlString: String,
//        block: HttpRequestBuilder.() -> Unit = {}
//    ): HttpResponse
//
//    suspend fun delete(
//        urlString: String,
//        block: HttpRequestBuilder.() -> Unit = {}
//    ): HttpResponse
//}
//class ClientProperty<T>(val getter: () -> T, val setter: (T) -> Unit) {
//    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getter()
//    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = setter(value)
//}
//
//class MockHttpClient : MockClient {
//
//    private val client = buildMockClient()
//
//    var blobUrl: String? = null
//
//    override suspend fun get(
//        urlString: String,
//        block: HttpRequestBuilder.() -> Unit
//    ): HttpResponse {
//        return client.get(urlString, block)
//    }
//
//    override suspend fun post(
//        urlString: String,
//        block: HttpRequestBuilder.() -> Unit
//    ): HttpResponse {
//        return client.post(urlString, block)
//    }
//
//    override suspend fun put(
//        urlString: String,
//        block: HttpRequestBuilder.() -> Unit
//    ): HttpResponse {
//        return client.put(urlString, block)
//    }
//
//    override suspend fun delete(
//        urlString: String,
//        block: HttpRequestBuilder.() -> Unit
//    ): HttpResponse {
//        return client.delete(urlString, block)
//    }
//
//}
//
//private fun buildMockClient(): HttpClient {
//
//    var latestBlobId = 1
//    return HttpClient(MockEngine { request ->
//        val body = when (val content = request.body) {
//            is io.ktor.http.content.TextContent -> content.text
//            is io.ktor.http.content.ByteArrayContent -> content.bytes().decodeToString()
//            else -> content.toString()
//        }
//        val (content, headers) = when (request.method) {
//            io.ktor.http.HttpMethod.Get -> {
//                delay(500)
//                val path = request.url.toString()
//
//                Pair(
//                    body,
//                    headersOf(HttpHeaders.ContentType, "application/json")
//                )
//            }
//
//            io.ktor.http.HttpMethod.Post -> {
//                Pair(
//                    "",
//                    headersOf(
//                        Pair(HttpHeaders.ContentType, listOf("application/json")),
//                        Pair("Location", listOf(blobUrl))
//                    )
//                )
//            }
//
////            io.ktor.http.HttpMethod.Put -> {
////
////            }
////
////            io.ktor.http.HttpMethod.Delete -> {
////
////            }
//
//            else -> {
//                Pair(
//                    "",
//                    headersOf(
//                        Pair(HttpHeaders.ContentType, listOf("application/json")),
//                        Pair("Location", listOf(blobUrl))
//                    )
//                )
//            }
//        }
//
//    })
//}