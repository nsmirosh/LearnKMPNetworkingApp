package com.learnkmp.networking.models

import kotlinx.serialization.Serializable


@Serializable
data class Note(
    val message: String,
    val author: String? = null,
    //TODO add Metadata
)