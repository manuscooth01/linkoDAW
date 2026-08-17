package com.linkodaw.di

import com.linkodaw.domain.AudioRecordingRepository
import com.linkodaw.data.AudioRecordingRepositoryImpl
import com.linkodaw.data.AudioRemoteDataSource
import com.linkodaw.data.AudioRemoteDataSourceImpl
import com.linkodaw.domain.usecase.SaveRecordingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InjectionProvider {

    @Provides
    @Singleton
    fun provideAudioRecordingRepository(
        saves: SaveRecordingUseCase,
        remote: AudioRemoteDataSource
    ): AudioRecordingRepository {
        return AudioRecordingRepositoryImpl(saveRecordingUseCase = saves, audioRemoteDataSource = remote)
    }

    @Provides
    @Singleton
    fun provideAudioRemoteDataSource(): AudioRemoteDataSource {
        return AudioRemoteDataSourceImpl()
    }

    @Provides
    @Singleton
    fun provideSaveRecordingUseCase(
        repository: AudioRecordingRepository
    ): SaveRecordingUseCase {
        return SaveRecordingUseCase(repository)
    }
}