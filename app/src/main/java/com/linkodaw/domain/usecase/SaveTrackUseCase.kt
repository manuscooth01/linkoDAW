package com.linkodaw.domain.usecase

import com.linkodaw.domain.model.Track
import com.linkodaw.domain.repository.TrackRepository
import javax.inject.Inject

class SaveTrackUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    operator fun invoke(track: Track) = repository.saveTrack(track)
}