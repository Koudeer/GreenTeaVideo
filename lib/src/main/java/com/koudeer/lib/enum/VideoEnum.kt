package com.koudeer.lib.enum

import androidx.annotation.IntDef

@IntDef
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Status {
    companion object {
        const val NORMAL = 0
        const val PREPARE = 1
        const val PLAYING = 2
        const val PAUSE = 3
        const val ERROR = 4
    }
}

val Int.type
    get() = when (this) {
        Status.NORMAL -> "NORMAL"
        Status.PREPARE -> "PREPARE"
        Status.PLAYING -> "PLAYING"
        Status.PAUSE -> "PAUSE"
        Status.ERROR -> "ERROR"
        else -> "NULL"
    }

@IntDef
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Screen {
    companion object {
        const val NORMAL = 0
        const val FULL = 1
    }
}