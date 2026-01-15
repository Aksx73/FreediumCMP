package com.absut.freediummobile

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform