package com.linkodaw.domain

import com.linkodaw.data.AudioRecording
import kotlinx.coroutines.flow.Flow

/* Interfaz del repositorio para grabación de audio.
   Define el contrato que implementará la capa data. */
interface AudioRecordingRepository {
    suspend fun recordAudio(audio: AudioRecording): Boolean
    suspend fun playRecording(id: String): Boolean
    Flow<RecordingStatus> getRecordingStatusFlow()
}