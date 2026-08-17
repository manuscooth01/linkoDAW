package com.linkodaw.data

import com.linkodaw.domain.AudioRecording
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/* Implementación concreta de AudioRemoteDataSource.
   Por ahora usa almacenamiento local simple; está preparada para extensiones remotas. */
class AudioRemoteDataSourceImpl : AudioRemoteDataSource {

    override suspend fun saveRecording(audio: AudioRecording): Boolean {
        // Lógica para guardar grabación (archivo, base de datos, etc.)
        println("Guardando grabación: ${audio.filename}")
        return true
    }

    override suspend fun loadRecordings(): Flow<List<AudioRecording>> {
        // Cargar grabaciones existentes
        return flow {
            listOf()
        }
    }

    override suspend fun deleteRecording(id: String): Boolean {
        // Eliminar grabación
        println("Eliminando grabación: $id")
        return true
    }
}