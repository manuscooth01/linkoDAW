package com.linkodaw.data.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.linkodaw.di.AudioRecordConfig
import com.linkodaw.domain.model.AudioState
import com.linkodaw.domain.usecase.AudioRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AudioRecorderImpl @Inject constructor(
    private val config: AudioRecordConfig
) : AudioRecorder {

    private val _state = MutableStateFlow<AudioState>(AudioState.Idle())
    override val state: kotlinx.coroutines.flow.StateFlow<AudioState> = _state.asStateFlow()

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var outputFile: File? = null
    private var fileOutputStream: FileOutputStream? = null
    private var isRecording = false
    private var isPaused = false
    private val amplitudeChannel = Channel<Float>(kotlinx.coroutines.channels.Channel.BUFFERED)
    private var startTime: Long = 0

    override fun getState(): kotlinx.coroutines.flow.StateFlow<AudioState> = state

    override suspend fun startRecording(outputPath: String) {
        if (isRecording) return

        outputFile = File(outputPath).apply {
            parentFile?.mkdirs()
        }

        val bufferSize = config.bufferSize
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            config.sampleRate,
            config.channelConfig,
            config.audioFormat,
            bufferSize
        ).also {
            if (it.state != AudioRecord.STATE_INITIALIZED) {
                _state.value = AudioState.Error("Failed to initialize AudioRecord")
                return
            }
        }

        try {
            fileOutputStream = FileOutputStream(outputFile!!)
        } catch (e: Exception) {
            _state.value = AudioState.Error("Failed to create output file", e)
            return
        }

        audioRecord?.startRecording()
        isRecording = true
        isPaused = false
        startTime = System.currentTimeMillis()

        _state.value = AudioState.Recording(0, 0f)

        recordingThread = Thread(recordingRunnable).apply {
            name = "AudioRecordingThread"
            start()
        }

        CoroutineScope(Dispatchers.IO).launch {
            amplitudeChannel.consumeEach { amplitude ->
                val duration = System.currentTimeMillis() - startTime
                _state.value = AudioState.Recording(duration, amplitude)
            }
        }
    }

    private val recordingRunnable = Runnable {
        val buffer = ByteArray(config.bufferSize)
        while (isRecording && !Thread.currentThread().isInterrupted) {
            if (isPaused) {
                Thread.sleep(50)
                continue
            }

            val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
            if (read > 0) {
                try {
                    fileOutputStream?.write(buffer, 0, read)
                    val amplitude = calculateAmplitude(buffer, read)
                    amplitudeChannel.trySend(amplitude)
                } catch (e: Exception) {
                    Log.e("AudioRecorder", "Write error", e)
                }
            } else if (read < 0) {
                Log.e("AudioRecorder", "Read error: $read")
            }
        }
    }

    private fun calculateAmplitude(buffer: ByteArray, read: Int): Float {
        var sum = 0L
        for (i in 0 until read step 2) {
            val sample = buffer[i].toInt() + (buffer[i + 1].toInt() shl 8)
            sum += (sample * sample).toLong()
        }
        val mean = sum / (read / 2)
        return Math.sqrt(mean.toDouble()).toFloat() / 32768f
    }

    override suspend fun stopRecording(): String? {
        isRecording = false
        recordingThread?.interrupt()
        recordingThread?.join(1000)

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        try {
            fileOutputStream?.close()
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Close error", e)
        }
        fileOutputStream = null

        val path = outputFile?.absolutePath
        outputFile = null

        _state.value = AudioState.Idle("Grabación guardada")
        return path
    }

    override suspend fun pauseRecording() {
        isPaused = true
        val duration = System.currentTimeMillis() - startTime
        _state.value = AudioState.Recording(duration, 0f)
    }

    override suspend fun resumeRecording() {
        isPaused = false
        startTime = System.currentTimeMillis() - (state.value as? AudioState.Recording)?.currentDuration ?: 0
    }
}