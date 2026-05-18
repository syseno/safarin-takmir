package com.masjid.jemaah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_masjid")
data class CachedMasjidEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String?,
    val addressDetail: String?,
    val city: String?,
    val isFavorite: Boolean = false
)
