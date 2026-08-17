package com.linkodaw.di

import com.linkodaw.domain.AudioRecordingRepository
import com.linkodaw.data.AudioRecordingRepositoryImpl
import com.linkodaw.data.AudioRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/* Módulo Hilt para proveer dependencias a toda la aplicación.
   Configura las implementaciones concretas de los casos de uso y repositorios. */
@Module(includes = [::class])
object InjectionProvider {

    @Provides
    @Singleton
    fun provideAudioRecordingRepository(
        @SaveRecordingUseCase saves: SaveRecordingUseCase,
        remote: AudioRemoteDataSource
    ): AudioRecordingRepository {
        return AudioRecordingRepository(saveRecordingUseCase = saves, audioRemoteDataSource = remote)
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