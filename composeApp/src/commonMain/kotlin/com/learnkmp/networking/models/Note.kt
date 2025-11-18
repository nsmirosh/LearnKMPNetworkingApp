package com.learnkmp.networking.models

import kotlinx.serialization.Serializable

@Serializable
data class Metadata(
    val tags: List<String>?
)

@Serializable
data class Note(
    val message: String,
    val author: String? = null,
    val imageUrl: String = "https://picsum.photos/300/200", // getting a random image
    val metadata: Metadata
)