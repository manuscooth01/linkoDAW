package com.linkodaw.domain.usecase

import com.linkodaw.domain.model.AudioState
import kotlinx.coroutines.flow.Flow

interface AudioRecorder {
    fun getState(): Flow<AudioState>
    suspend fun startRecording(outputPath: String)
    suspend fun stopRecording(): String?
    suspend fun pauseRecording()
    suspend fun resumeRecording()
}