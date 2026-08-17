package com.linkodaw.data.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import com.linkodaw.di.AudioTrackConfig
import com.linkodaw.domain.model.AudioState
import com.linkodaw.domain.usecase.AudioPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

class AudioPlayerImpl @Inject constructor(
    private val config: AudioTrackConfig
) : AudioPlayer {

    private val _state = MutableStateFlow<AudioState>(AudioState.Idle())
    override val state: kotlinx.coroutines.flow.StateFlow<AudioState> = _state.asStateFlow()

    private var audioTrack: AudioTrack? = null
    private var playbackThread: Thread? = null
    private var inputFile: File? = null
    private var fileInputStream: FileInputStream? = null
    private var isPlaying = false
    private var isPaused = false
    private var totalDuration: Long = 0
    private var currentPosition: Long = 0
    private var startTime: Long = 0

    override fun getState(): kotlinx.coroutines.flow.StateFlow<AudioState> = state

    override suspend fun startPlaying(filePath: String) {
        if (isPlaying) {
            stopPlaying()
        }

        inputFile = File(filePath)
        if (!inputFile!!.exists()) {
            _state.value = AudioState.Error("File not found: $filePath")
            return
        }

        try {
            fileInputStream = FileInputStream(inputFile!!)
            totalDuration = calculateDuration(inputFile!!)
        } catch (e: Exception) {
            _state.value = AudioState.Error("Failed to open file", e)
            return
        }

        val bufferSize = config.bufferSize
        audioTrack = AudioTrack.Builder()
            .setAudioFormat(android.media.AudioFormat.Builder()
                .setEncoding(config.audioFormat)
                .setSampleRate(config.sampleRate)
                .setChannelMask(config.channelConfig)
                .build())
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
            .also {
                if (it.state != AudioTrack.STATE_INITIALIZED) {
                    _state.value = AudioState.Error("Failed to initialize AudioTrack")
                    return
                }
            }

        audioTrack?.play()
        isPlaying = true
        isPaused = false
        currentPosition = 0
        startTime = System.currentTimeMillis()

        _state.value = AudioState.Playing(0, totalDuration)

        playbackThread = Thread(playbackRunnable).apply {
            name = "AudioPlaybackThread"
            start()
        }
    }

    private fun calculateDuration(file: File): Long {
        val fileSize = file.length()
        val bytesPerFrame = (config.audioFormat == AudioFormat.ENCODING_PCM_16BIT).let { if (it) 2 else 1 }
        val channelCount = if (config.channelConfig == AudioFormat.CHANNEL_OUT_STEREO) 2 else 1
        val frameSize = bytesPerFrame * channelCount
        val totalFrames = fileSize / frameSize
        return (totalFrames * 1000 / config.sampleRate).toLong()
    }

    private val playbackRunnable = Runnable {
        val buffer = ByteArray(config.bufferSize)
        while (isPlaying && !Thread.currentThread().isInterrupted) {
            if (isPaused) {
                Thread.sleep(50)
                continue
            }

            val read = fileInputStream?.read(buffer) ?: -1
            if (read > 0) {
                audioTrack?.write(buffer, 0, read, AudioTrack.WRITE_BLOCKING)
                currentPosition += (read * 1000L / config.sampleRate / (if (config.channelConfig == AudioFormat.CHANNEL_OUT_STEREO) 2 else 1) / 2)
                _state.value = AudioState.Playing(currentPosition, totalDuration)
            } else {
                stopPlaying()
                break
            }
        }
    }

    override suspend fun stopPlaying() {
        isPlaying = false
        playbackThread?.interrupt()
        playbackThread?.join(1000)

        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null

        try {
            fileInputStream?.close()
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Close error", e)
        }
        fileInputStream = null
        inputFile = null

        _state.value = AudioState.Idle("Reproducción finalizada")
    }

    override suspend fun pausePlaying() {
        isPaused = true
        audioTrack?.pause()
        _state.value = AudioState.Paused(currentPosition, totalDuration)
    }

    override suspend fun seekTo(position: Long) {
        if (fileInputStream != null && position >= 0 && position <= totalDuration) {
            val bytesPerFrame = (config.audioFormat == AudioFormat.ENCODING_PCM_16BIT).let { if (it) 2 else 1 }
            val channelCount = if (config.channelConfig == AudioFormat.CHANNEL_OUT_STEREO) 2 else 1
            val frameSize = bytesPerFrame * channelCount
            val targetFrame = (position * config.sampleRate / 1000).toLong()
            val targetByte = targetFrame * frameSize

            try {
                fileInputStream?.getChannel()?.position(targetByte)
                currentPosition = position
                _state.value = AudioState.Playing(currentPosition, totalDuration)
            } catch (e: Exception) {
                Log.e("AudioPlayer", "Seek error", e)
            }
        }
    }
}