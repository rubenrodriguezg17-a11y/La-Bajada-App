package com.labajada.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_dishes")
data class FavoriteDishEntity(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val price: String,
    val imagePath: String,
    val timestamp: Long = System.currentTimeMillis() // Para ordenar los más recientes primero
)
