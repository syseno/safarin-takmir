package com.masjid.jemaah.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.masjid.jemaah.data.local.entity.CachedMasjidEntity
import com.masjid.jemaah.data.local.entity.EventEntity
import com.masjid.jemaah.data.local.entity.PrayerEntity

@Database(
    entities = [CachedMasjidEntity::class, EventEntity::class, PrayerEntity::class],
    version = 2,
    exportSchema = false
)
abstract class JemaahDatabase : RoomDatabase() {
    abstract fun masjidDao(): MasjidDao
    abstract fun eventDao(): EventDao
    abstract fun prayerDao(): PrayerDao
}
