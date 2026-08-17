package com.linkodaw.domain.repository

import com.linkodaw.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun getAllTracks(): Flow<List<Track>>
    suspend fun saveTrack(track: Track)
    suspend fun deleteTrack(trackId: String)
    suspend fun deleteAllTracks()
}