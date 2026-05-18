package com.masjid.jemaah.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.masjid.jemaah.data.local.entity.CachedMasjidEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MasjidDao {
    @Query("SELECT * FROM cached_masjid")
    fun getAllCached(): Flow<List<CachedMasjidEntity>>

    @Query("SELECT * FROM cached_masjid WHERE id = :id")
    suspend fun getById(id: String): CachedMasjidEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(masjids: List<CachedMasjidEntity>)

    @Query("DELETE FROM cached_masjid")
    suspend fun clearAll()
}
