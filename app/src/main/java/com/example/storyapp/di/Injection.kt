package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.db.local.room.StoryDatabase
import com.example.storyapp.db.remote.StoryRepository
import com.example.storyapp.retrofit.ApiConfig

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}