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
    version = 3,        // bumped from 2 → 3 (added recurrence + poster fields to EventEntity)
    exportSchema = false

)
abstract class TakmirDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun eventDao(): EventDao
    abstract fun donationDao(): DonationDao
    abstract fun inventoryDao(): InventoryDao
}
