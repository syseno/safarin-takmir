package com.masjid.jemaah.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.masjid.jemaah.data.local.entity.CachedMasjidEntity
import com.masjid.jemaah.data.local.entity.EventEntity

@Database(
    entities = [CachedMasjidEntity::class, EventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class JemaahDatabase : RoomDatabase() {
    abstract fun masjidDao(): MasjidDao
    abstract fun eventDao(): EventDao
}
