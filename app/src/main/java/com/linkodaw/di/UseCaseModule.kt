package com.linkodaw.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object UseCaseModule {

    @Provides
    fun provideRecordAudioUseCase(recorder: AudioRecorder): RecordAudioUseCase {
        return RecordAudioUseCase(recorder)
    }

    @Provides
    fun providePlayAudioUseCase(player: AudioPlayer): PlayAudioUseCase {
        return PlayAudioUseCase(player)
    }

    @Provides
    fun provideStopAudioUseCase(recorder: AudioRecorder, player: AudioPlayer): StopAudioUseCase {
        return StopAudioUseCase(recorder, player)
    }

    @Provides
    fun provideGetTracksUseCase(repository: TrackRepository): GetTracksUseCase {
        return GetTracksUseCase(repository)
    }

    @Provides
    fun provideSaveTrackUseCase(repository: TrackRepository): SaveTrackUseCase {
        return SaveTrackUseCase(repository)
    }

    @Provides
    fun provideDeleteTrackUseCase(repository: TrackRepository): DeleteTrackUseCase {
        return DeleteTrackUseCase(repository)
    }
}