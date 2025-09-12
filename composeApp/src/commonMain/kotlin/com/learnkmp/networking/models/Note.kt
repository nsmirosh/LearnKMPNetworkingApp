package com.learnkmp.networking.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Metadata(
    val tags: List<String>?
)

@Serializable
data class Note(
    val message: String,
    val author: String? = null,
    val metadata: Metadata
)