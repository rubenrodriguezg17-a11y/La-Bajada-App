package com.labajada.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String,
    val restaurantName: String,
    val rucNumber: String,
    val phoneNumber: String,
    val selectedCategory: String,
    val addressDetails: String,
    val latitude: Double,
    val longitude: Double,
    val offersDelivery: Boolean,
    val maxDeliveryDistanceKm: Double,
    val imageUrl: String? = null,
    val isOpen: Boolean = false,
    val businessHours: String? = null
)
