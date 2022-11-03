package com.example.resturantsearch.di

import android.app.Application
import android.content.Context
import com.example.resturantsearch.repository.RestaurantRepository
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
    fun provideRepository(@ApplicationContext context: Context): RestaurantRepository{
        return RestaurantRepository(context)
    }
}