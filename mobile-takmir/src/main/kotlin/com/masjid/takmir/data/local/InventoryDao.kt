package com.masjid.takmir.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.masjid.takmir.data.local.entity.InventoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory WHERE masjidId = :masjidId ORDER BY createdAt DESC")
    fun getAllByMasjidId(masjidId: String): Flow<List<InventoryEntity>>

    @Query("SELECT * FROM inventory WHERE id = :id")
    suspend fun getById(id: String): InventoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InventoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InventoryEntity)

    @Delete
    suspend fun delete(item: InventoryEntity)

    @Query("DELETE FROM inventory WHERE masjidId = :masjidId")
    suspend fun deleteAllByMasjidId(masjidId: String)
}
