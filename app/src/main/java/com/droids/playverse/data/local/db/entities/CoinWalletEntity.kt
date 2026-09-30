package com.droids.playverse.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_wallet")
data class CoinWalletEntity(
    @PrimaryKey
    val id: Int = 0,
    val balance: Int = 0,
    val lastUpdated: Long
)