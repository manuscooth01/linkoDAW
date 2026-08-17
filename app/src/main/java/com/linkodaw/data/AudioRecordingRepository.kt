package com.linkodaw.data

import com.linkodaw.domain.AudioRecording
import com.linkodaw.domain.AudioRecordingRepository
import com.linkodaw.domain.usecase.SaveRecordingUseCase
import javax.inject.Inject

/* Implementación del repositorio para grabación de audio.
   Coordina entre fuentes de datos locales y remotas, aplicando Clean Architecture. */
class AudioRecordingRepository @Inject constructor(
    private val saveRecordingUseCase: SaveRecordingUseCase,
    private val audioRemoteDataSource: AudioRemoteDataSource
) : AudioRecordingRepository {

    override suspend fun recordAudio(audio: AudioRecording): Boolean {
        // Guardar la grabación usando el caso de uso
        return saveRecordingUseCase.execute(audio)
    }

    override suspend fun playRecording(id: String): Boolean {
        // Reproducir grabaciones guardadas
        return saveRecordingUseCase.execute(AudioRecording(id, System.currentTimeMillis()))
    }
}