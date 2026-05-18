package com.masjid.jemaah.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.masjid.jemaah.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE masjidId = :masjidId ORDER BY date DESC")
    fun getEventsByMasjid(masjidId: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)

    @Query("DELETE FROM events WHERE masjidId = :masjidId")
    suspend fun deleteByMasjid(masjidId: String)
}
