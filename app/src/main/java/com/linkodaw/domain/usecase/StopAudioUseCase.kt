package com.linkodaw.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class StopAudioUseCase @Inject constructor(
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer
) {
    operator fun invoke(scope: CoroutineScope) {
        scope.launch { audioRecorder.stopRecording() }
        scope.launch { audioPlayer.stopPlaying() }
    }

    suspend fun stopAll() {
        audioRecorder.stopRecording()
        audioPlayer.stopPlaying()
    }
}