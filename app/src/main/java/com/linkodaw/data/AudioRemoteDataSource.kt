package com.linkodaw.data

import com.linkodaw.domain.AudioRecording
import kotlinx.coroutines.flow.Flow

/* Interfaz para la fuente de datos remota/externa de audio.
   Define el contrato para guardar/cargar grabaciones. */
interface AudioRemoteDataSource {
    suspend fun saveRecording(audio: AudioRecording): Boolean
    suspend fun loadRecordings(): Flow<List<AudioRecording>>
    suspend fun deleteRecording(id: String): Boolean
}