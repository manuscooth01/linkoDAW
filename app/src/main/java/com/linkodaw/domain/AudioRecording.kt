package com.linkodaw.domain

import kotlinx.serialization.Serializable

@Serializable
data class AudioRecording(
    val id: String,
    val timestamp: Long,
    val filename: String,
    val durationMs: Long,
    val filePath: String,
    val status: RecordingStatus = RecordingStatus.PENDING
)

enum class RecordingStatus { PENDING, RECORDING, PAUSED, COMPLETED, ERROR }