package com.linkodaw.domain.usecase

import com.linkodaw.domain.repository.TrackRepository
import javax.inject.Inject

class DeleteTrackUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    operator fun invoke(trackId: String) = repository.deleteTrack(trackId)
}