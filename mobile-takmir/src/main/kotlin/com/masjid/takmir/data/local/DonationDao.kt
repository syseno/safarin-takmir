package com.masjid.takmir.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.masjid.takmir.data.local.entity.DonationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations WHERE masjidId = :masjidId ORDER BY createdAt DESC")
    fun getAllByMasjidId(masjidId: String): Flow<List<DonationEntity>>

    @Query("SELECT * FROM donations WHERE id = :id")
    suspend fun getById(id: String): DonationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(donations: List<DonationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(donation: DonationEntity)

    @Delete
    suspend fun delete(donation: DonationEntity)

    @Query("DELETE FROM donations WHERE masjidId = :masjidId")
    suspend fun deleteAllByMasjidId(masjidId: String)
}
