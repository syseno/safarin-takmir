package com.masjid.jemaah.di

import android.content.Context
import androidx.room.Room
import com.masjid.jemaah.data.local.EventDao
import com.masjid.jemaah.data.local.JemaahDatabase
import com.masjid.jemaah.data.local.MasjidDao
import com.masjid.jemaah.data.local.PrayerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideJemaahDatabase(@ApplicationContext context: Context): JemaahDatabase {
        return Room.databaseBuilder(
            context,
            JemaahDatabase::class.java,
            "jemaah_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMasjidDao(database: JemaahDatabase): MasjidDao {
        return database.masjidDao()
    }

    @Provides
    @Singleton
    fun provideEventDao(database: JemaahDatabase): EventDao {
        return database.eventDao()
    }

    @Provides
    @Singleton
    fun providePrayerDao(database: JemaahDatabase): PrayerDao {
        return database.prayerDao()
    }
}
