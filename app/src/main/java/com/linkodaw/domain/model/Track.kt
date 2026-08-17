package com.linkodaw.domain.model

import android.net.Uri
import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val filePath: String,
    val duration: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val sampleRate: Int = 44100,
    val channelCount: Int = 1,
    val bitDepth: Int = 16
) {
    val uri: Uri
        get() = Uri.parse(filePath)

    val formattedDuration: String
        get() {
            val minutes = duration / 60000
            val seconds = (duration % 60000) / 1000
            val millis = duration % 1000
            return String.format("%02d:%02d.%03d", minutes, seconds, millis)
        }
}