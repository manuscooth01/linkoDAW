package com.linkodaw.data.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.linkodaw.R
import com.linkodaw.di.AudioRecordConfig
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioRecordingService : Service() {

    @Inject
    lateinit var config: AudioRecordConfig

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingThread: Thread? = null
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "audio_recording_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            "START_RECORDING" -> startForegroundRecording(intent.getStringExtra("outputPath"))
            "STOP_RECORDING" -> stopRecording()
            "PAUSE_RECORDING" -> pauseRecording()
            "RESUME_RECORDING" -> resumeRecording()
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Grabación de Audio",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificación de grabación de audio en segundo plano"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundRecording(outputPath: String?) {
        val notification = buildNotification("Grabando audio...", "Toca para abrir la app")
        startForeground(NOTIFICATION_ID, notification)

        val file = outputPath?.let { java.io.File(it) } ?: java.io.File(
            getExternalFilesDir(android.os.Environment.DIRECTORY_MUSIC),
            "recording_${System.currentTimeMillis()}.pcm"
        )
        file.parentFile?.mkdirs()

        val bufferSize = config.bufferSize
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            config.sampleRate,
            config.channelConfig,
            config.audioFormat,
            bufferSize
        ).also {
            if (it.state != AudioRecord.STATE_INITIALIZED) {
                stopSelf()
                return
            }
        }

        audioRecord?.startRecording()
        isRecording = true

        recordingThread = Thread {
            val buffer = ByteArray(bufferSize)
            val outputStream = java.io.FileOutputStream(file)
            while (isRecording && !Thread.currentThread().isInterrupted) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    try {
                        outputStream.write(buffer, 0, read)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            try {
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.apply {
            name = "AudioRecordingServiceThread"
            start()
        }
    }

    private fun stopRecording() {
        isRecording = false
        recordingThread?.interrupt()
        recordingThread?.join(1000)

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        stopForeground(true)
        stopSelf()
    }

    private fun pauseRecording() {
        // TODO: Implement pause for foreground service
    }

    private fun resumeRecording() {
        // TODO: Implement resume for foreground service
    }

    private fun buildNotification(title: String, text: String): Notification {
        val intent = Intent(this, com.linkodaw.presentation.main.MainActivity::class.java)
        val pendingIntent = android.app.PendingIntent.getActivity(
            this, 0, intent,
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_mic)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        stopRecording()
        super.onDestroy()
    }
}