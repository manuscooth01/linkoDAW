package com.linkodaw.di

import com.linkodaw.domain.usecase.AudioPlayer
import com.linkodaw.domain.usecase.AudioRecorder
import com.linkodaw.domain.usecase.GetTracksUseCase
import com.linkodaw.domain.usecase.DeleteTrackUseCase
import com.linkodaw.domain.usecase.PlayAudioUseCase
import com.linkodaw.domain.usecase.RecordAudioUseCase
import com.linkodaw.domain.usecase.SaveTrackUseCase
import com.linkodaw.domain.usecase.StopAudioUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideRecordAudioUseCase(
        recorder: AudioRecorder
    ): RecordAudioUseCase {
        return RecordAudioUseCase(recorder)
    }

    @Provides
    @Singleton
    fun providePlayAudioUseCase(
        player: AudioPlayer
    ): PlayAudioUseCase {
        return PlayAudioUseCase(player)
    }

    @Provides
    @Singleton
    fun provideStopAudioUseCase(
        recorder: AudioRecorder,
        player: AudioPlayer
    ): StopAudioUseCase {
        return StopAudioUseCase(recorder, player)
    }

    @Provides
    @Singleton
    fun provideGetTracksUseCase(
        repository: TrackRepository
    ): GetTracksUseCase {
        return GetTracksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveTrackUseCase(
        repository: TrackRepository
    ): SaveTrackUseCase {
        return SaveTrackUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteTrackUseCase(
        repository: TrackRepository
    ): DeleteTrackUseCase {
        return DeleteTrackUseCase(repository)
    }
}