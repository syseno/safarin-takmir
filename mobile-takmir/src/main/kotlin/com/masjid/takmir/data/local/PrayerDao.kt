package com.masjid.takmir.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.masjid.takmir.data.local.entity.PrayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayer_schedules WHERE date = :date")
    suspend fun getByDate(date: String): PrayerEntity?

    @Query("SELECT * FROM prayer_schedules ORDER BY date ASC")
    fun getAll(): Flow<List<PrayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prayer: PrayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prayers: List<PrayerEntity>)

    @Query("DELETE FROM prayer_schedules")
    suspend fun deleteAll()

    @Query("DELETE FROM prayer_schedules WHERE date < :today")
    suspend fun deleteOldSchedules(today: String)
}
