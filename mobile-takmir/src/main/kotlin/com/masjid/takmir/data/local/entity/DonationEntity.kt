package com.masjid.takmir.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donations")
data class DonationEntity(
    @PrimaryKey
    val id: String,
    val type: String,      // SADAQAH, INFAQ, ZAKAT
    val amount: Double,
    val description: String,
    val createdAt: String,
    val masjidId: String
)
