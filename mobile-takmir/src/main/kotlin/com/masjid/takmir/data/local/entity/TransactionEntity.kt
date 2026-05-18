package com.masjid.takmir.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val amount: Long,
    val type: String, // DEBIT, CREDIT
    val description: String?,
    val createdAt: String,
    val masjidId: String
)
