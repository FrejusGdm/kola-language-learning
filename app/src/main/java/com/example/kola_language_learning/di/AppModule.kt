package com.example.kola_language_learning.di

import android.content.Context
import com.example.kola_language_learning.data.audio.AudioRecorder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideAudioRecorder(
        context: Context,
        openAIService: OpenAIService  // Add this parameter
    ): AudioRecorder {
        return AudioRecorder(context, openAIService)
    }
}
