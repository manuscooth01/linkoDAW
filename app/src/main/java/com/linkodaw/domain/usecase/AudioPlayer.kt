package com.linkodaw.domain.usecase

import com.linkodaw.domain.model.AudioState
import kotlinx.coroutines.flow.Flow

interface AudioPlayer {
    fun getState(): Flow<AudioState>
    suspend fun startPlaying(filePath: String)
    suspend fun stopPlaying()
    suspend fun pausePlaying()
    suspend fun seekTo(position: Long)
}