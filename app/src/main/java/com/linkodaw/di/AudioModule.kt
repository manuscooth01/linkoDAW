package com.linkodaw.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class AudioModule {

    @Binds
    @ActivityRetainedScoped
    abstract fun bindAudioRecorder(impl: AudioRecorderImpl): AudioRecorder

    @Binds
    @ActivityRetainedScoped
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