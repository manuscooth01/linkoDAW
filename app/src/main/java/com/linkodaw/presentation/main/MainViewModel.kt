package com.linkodaw.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linkodaw.domain.model.AudioState
import com.linkodaw.domain.model.Track
import com.linkodaw.domain.usecase.DeleteTrackUseCase
import com.linkodaw.domain.usecase.GetTracksUseCase
import com.linkodaw.domain.usecase.PlayAudioUseCase
import com.linkodaw.domain.usecase.RecordAudioUseCase
import com.linkodaw.domain.usecase.SaveTrackUseCase
import com.linkodaw.domain.usecase.StopAudioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class MainViewModel @Inject constructor(
    private val recordAudioUseCase: RecordAudioUseCase,
    private val playAudioUseCase: PlayAudioUseCase,
    private val stopAudioUseCase: StopAudioUseCase,
    private val getTracksUseCase: GetTracksUseCase,
    private val saveTrackUseCase: SaveTrackUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase
) : ViewModel() {

    private val _recordingState = recordAudioUseCase.getState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), AudioState.Idle())

    private val _playingState = playAudioUseCase.getState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), AudioState.Idle())

    val uiState = combine(_recordingState, _playingState) { recording, playing ->
        when {
            recording is AudioState.Recording -> recording
            recording is AudioState.Error -> recording
            playing is AudioState.Playing -> playing
            playing is AudioState.Paused -> playing
            playing is AudioState.Error -> playing
            else -> AudioState.Idle()
        }
    }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), AudioState.Idle())

    val tracks = getTracksUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun startRecording() {
        val outputPath = "${android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_MUSIC).absolutePath}/linkoDAW/recording_${System.currentTimeMillis()}.pcm"
        recordAudioUseCase(outputPath)
    }

    fun stopRecording() {
        viewModelScope.launch {
            val filePath = recordAudioUseCase.stop()
            filePath?.let { path ->
                val track = Track(
                    name = "Grabación ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}",
                    filePath = path,
                    duration = (recordAudioUseCase.getState().value as? AudioState.Recording)?.currentDuration ?: 0
                )
                saveTrackUseCase(track)
            }
        }
    }

    fun pauseRecording() = viewModelScope.launch { recordAudioUseCase.pause() }
    fun resumeRecording() = viewModelScope.launch { recordAudioUseCase.resume() }

    fun playTrack(track: Track) {
        playAudioUseCase(track.filePath)
    }

    fun stopPlayback() = viewModelScope.launch { playAudioUseCase.stop() }
    fun pausePlayback() = viewModelScope.launch { playAudioUseCase.pause() }
    fun seekPlayback(position: Long) = viewModelScope.launch { playAudioUseCase.seekTo(position) }

    fun stopAll() = viewModelScope.launch { stopAudioUseCase(viewModelScope) }

    fun deleteTrack(track: Track) = viewModelScope.launch { deleteTrackUseCase(track.id) }
}