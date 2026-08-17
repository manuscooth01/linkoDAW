package com.linkodaw.domain

import com.linkodaw.data.AudioRecording
import kotlinx.coroutines.flow MutableStateFlow
import javax.inject.Inject

/* Caso de uso para guardar una grabación de audio.
   Ejecuta la lógica de negocio y coordina con el repositorio. */
class SaveRecordingUseCase @Inject constructor(
    private val repository: AudioRecordingRepository
) {

    // Estado observable del caso de uso
    private val _status = MutableStateFlow(RecordingStatus.PENDING)
    val statusFlow: MutableStateFlow<RecordingStatus> = _status

    suspend fun execute(audio: AudioRecording): Boolean {
        // Ejecutar lógica de guardado en el repositorio
        return repository.recordAudio(audio)
    }
}