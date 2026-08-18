package com.linkodaw.di

import com.linkodaw.data.audio.AudioRecorderImpl
import com.linkodaw.data.audio.AudioPlayerImpl
import com.linkodaw.domain.usecase.AudioRecorder
import com.linkodaw.domain.usecase.AudioPlayer
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AudioModule {

    @Binds
    @Singleton
    abstract fun bindAudioRecorder(impl: AudioRecorderImpl): AudioRecorder

    @Binds
    @Singleton
    abstract fun bindAudioPlayer(impl: AudioPlayerImpl): AudioPlayer
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAudioRecordConfig(): AudioRecordConfig = AudioRecordConfig()

    @Provides
    @Singleton
    fun provideAudioTrackConfig(): AudioTrackConfig = AudioTrackConfig()
}

data class AudioRecordConfig(
    val sampleRate: Int = 44100,
    val channelConfig: Int = android.media.AudioFormat.CHANNEL_IN_MONO,
    val audioFormat: Int = android.media.AudioFormat.ENCODING_PCM_16BIT,
    val bufferSize: Int = android.media.AudioRecord.getMinBufferSize(
        44100,
        android.media.AudioFormat.CHANNEL_IN_MONO,
        android.media.AudioFormat.ENCODING_PCM_16BIT
    ) * 4
)

data class AudioTrackConfig(
    val sampleRate: Int = 44100,
    val channelConfig: Int = android.media.AudioFormat.CHANNEL_OUT_MONO,
    val audioFormat: Int = android.media.AudioFormat.ENCODING_PCM_16BIT,
    val bufferSize: Int = android.media.AudioTrack.getMinBufferSize(
        44100,
        android.media.AudioFormat.CHANNEL_OUT_MONO,
        android.media.AudioFormat.ENCODING_PCM_16BIT
    ) * 4
)