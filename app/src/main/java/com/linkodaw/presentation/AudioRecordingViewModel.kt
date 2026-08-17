package com.linkodaw.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linkodaw.domain.AudioRecording
import com.linkodaw.domain.AudioRecordingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectAsLazyMap
import kotlinx.coroutines.launch
import javax.inject.Inject

/* ViewModel para coordinar la lógica de grabación/ reproducción con la UI.
   Usa StateFlow para notificar cambios de estado a la Activity. */
class AudioRecordingViewModel @Inject constructor(
    private val repository: AudioRecordingRepository
) : ViewModel() {

    // Estado actual de la grabación, observable desde la UI
    private val _status = repository.getRecordingStatusFlow()
    val status: Flow<RecordingStatus> = _status.asFlow()

    // Iniciar grabación
    fun startRecording() {
        viewModelScope.launch {
            repository.recordAudio(AudioRecording(
                id = java.util.UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                filename = "recording_${System.currentTimeMillis()}.audio",
                durationMs = 0,
                filePath = ""
            ))
        }
    }

    // Detener grabación
    fun stopRecording() {
        viewModelScope.launch {
            // Lógica de parada
        }
    }

    // Reproducir grabación
    fun playRecording(id: String) {
        viewModelScope.launch {
            repository.playRecording(id)
        }
    }
}