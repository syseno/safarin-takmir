package com.masjid.takmir.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.masjid.takmir.data.local.entity.DonationEntity
import com.masjid.takmir.data.local.entity.EventEntity
import com.masjid.takmir.data.local.entity.InventoryEntity
import com.masjid.takmir.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        EventEntity::class,
        DonationEntity::class,
        InventoryEntity::class
    ],
    version = 2,        // bumped from 1 → 2 (added donation + inventory tables)
    exportSchema = false
)
abstract class TakmirDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun eventDao(): EventDao
    abstract fun donationDao(): DonationDao
    abstract fun inventoryDao(): InventoryDao
}
