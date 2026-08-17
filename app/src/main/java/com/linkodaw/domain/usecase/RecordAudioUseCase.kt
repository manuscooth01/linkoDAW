package com.linkodaw.domain.usecase

import com.linkodaw.domain.model.AudioState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecordAudioUseCase @Inject constructor(
    private val audioRecorder: AudioRecorder
) {
    operator fun invoke(outputPath: String) = audioRecorder.startRecording(outputPath)

    fun getState(): Flow<AudioState> = audioRecorder.getState()

    suspend fun stop(): String? = audioRecorder.stopRecording()

    suspend fun pause() = audioRecorder.pauseRecording()

    suspend fun resume() = audioRecorder.resumeRecording()
}