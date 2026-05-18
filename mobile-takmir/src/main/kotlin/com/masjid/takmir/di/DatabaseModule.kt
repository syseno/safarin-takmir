package com.masjid.takmir.di

import android.content.Context
import androidx.room.Room
import com.masjid.takmir.data.local.DonationDao
import com.masjid.takmir.data.local.EventDao
import com.masjid.takmir.data.local.InventoryDao
import com.masjid.takmir.data.local.TakmirDatabase
import com.masjid.takmir.data.local.TransactionDao
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
    fun provideTakmirDatabase(@ApplicationContext context: Context): TakmirDatabase {
        return Room.databaseBuilder(
            context,
            TakmirDatabase::class.java,
            "takmir_database"
        )
        .fallbackToDestructiveMigration()  // Dev convenience — replace with Migration in prod
        .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: TakmirDatabase): TransactionDao = database.transactionDao()

    @Provides
    @Singleton
    fun provideEventDao(database: TakmirDatabase): EventDao = database.eventDao()

    @Provides
    @Singleton
    fun provideDonationDao(database: TakmirDatabase): DonationDao = database.donationDao()

    @Provides
    @Singleton
    fun provideInventoryDao(database: TakmirDatabase): InventoryDao = database.inventoryDao()
}
