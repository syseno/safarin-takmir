package com.masjid.takmir.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class InventoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val quantity: Int,
    val condition: String,   // GOOD, DAMAGED, LOST
    val createdAt: String,
    val masjidId: String
)
