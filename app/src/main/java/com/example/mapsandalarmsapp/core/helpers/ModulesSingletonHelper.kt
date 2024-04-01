package com.example.mapsandalarmsapp.core.helpers

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mapsandalarmsapp.core.room.RoomDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModulesSingletonHelper {
    @Provides
    @Singleton
    fun provideRoomDatabase(application: Application): RoomDB {
        return Room.databaseBuilder(
            application,
            RoomDB::class.java,
            DBNamesHelper.NAME
        ).setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}