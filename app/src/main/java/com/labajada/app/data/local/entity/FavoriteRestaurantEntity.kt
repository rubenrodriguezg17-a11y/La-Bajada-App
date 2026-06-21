package com.labajada.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_restaurants")
data class FavoriteRestaurantEntity(
    @PrimaryKey
    val restaurantId: String, // ◄ El ID del restaurante es la clave única
    val restaurantName: String,
    val category: String,
    val address: String,
    val timestamp: Long = System.currentTimeMillis()
)
