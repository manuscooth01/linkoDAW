package com.linkodaw.domain.usecase

import com.linkodaw.domain.model.AudioState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlayAudioUseCase @Inject constructor(
    private val audioPlayer: AudioPlayer
) {
    operator fun invoke(filePath: String) = audioPlayer.startPlaying(filePath)

    fun getState(): Flow<AudioState> = audioPlayer.getState()

    suspend fun stop() = audioPlayer.stopPlaying()

    suspend fun pause() = audioPlayer.pausePlaying()

    suspend fun seekTo(position: Long) = audioPlayer.seekTo(position)
}