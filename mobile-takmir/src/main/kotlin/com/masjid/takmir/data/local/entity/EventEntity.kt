package com.masjid.takmir.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val location: String?,
    val masjidId: String
)
