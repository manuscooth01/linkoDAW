package com.linkodaw.data.repository

import android.content.Context
import com.linkodaw.domain.model.Track
import com.linkodaw.domain.repository.TrackRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class TrackRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TrackRepository {

    private val tracksDir = File(context.filesDir, "tracks").apply { mkdirs() }
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    override val tracks: kotlinx.coroutines.flow.StateFlow<List<Track>> = _tracks.asStateFlow()

    init {
        loadTracks()
    }

    override fun getAllTracks(): kotlinx.coroutines.flow.StateFlow<List<Track>> = tracks

    override suspend fun saveTrack(track: Track) = withContext(Dispatchers.IO) {
        val currentTracks = _tracks.value.toMutableList()
        val existingIndex = currentTracks.indexOfFirst { it.id == track.id }
        if (existingIndex >= 0) {
            currentTracks[existingIndex] = track
        } else {
            currentTracks.add(0, track)
        }
        _tracks.value = currentTracks
        saveTracksToDisk(currentTracks)
    }

    override suspend fun deleteTrack(trackId: String) = withContext(Dispatchers.IO) {
        val currentTracks = _tracks.value.toMutableList()
        val track = currentTracks.find { it.id == trackId }
        track?.let {
            File(it.filePath).delete()
        }
        currentTracks.removeAll { it.id == trackId }
        _tracks.value = currentTracks
        saveTracksToDisk(currentTracks)
    }

    override suspend fun deleteAllTracks() = withContext(Dispatchers.IO) {
        _tracks.value.forEach { File(it.filePath).delete() }
        _tracks.value = emptyList()
        saveTracksToDisk(emptyList())
    }

    private fun loadTracks() {
        val files = tracksDir.listFiles()?.filter { it.extension == "pcm" }?.sortedByDescending { it.lastModified() } ?: emptyArray()
        val loadedTracks = files.map { file ->
            val name = file.nameWithoutExtension
            Track(
                name = name,
                filePath = file.absolutePath,
                duration = estimateDuration(file),
                createdAt = file.lastModified()
            )
        }
        _tracks.value = loadedTracks
    }

    private fun estimateDuration(file: File): Long {
        val fileSize = file.length()
        val bytesPerFrame = 2 * 1
        val totalFrames = fileSize / bytesPerFrame
        return (totalFrames * 1000L / 44100).toLong()
    }

    private fun saveTracksToDisk(tracks: List<Track>) {
    }
}