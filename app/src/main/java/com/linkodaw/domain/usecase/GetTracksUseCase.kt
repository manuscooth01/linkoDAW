package com.linkodaw.domain.usecase

import com.linkodaw.domain.model.Track
import com.linkodaw.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTracksUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    operator fun invoke(): Flow<List<Track>> = repository.getAllTracks()
}