package com.learnkmp.networking

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform