package com.labajada.app.domain.repository

import com.labajada.app.domain.model.FavoriteRestaurant
import com.labajada.app.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {
    suspend fun saveFavoriteRestaurant(
        restaurantId: String,
        restaurantName: String,
        category: String,
        address: String,
        timestamp: Long
    )
    fun getFavoriteRestaurants(): Flow<List<FavoriteRestaurant>>
    suspend fun removeFavoriteRestaurant(restaurantId: String)
    fun getRestaurantById(restaurantId: String): Flow<Restaurant?>
    suspend fun updateRestaurantProfile(restaurant: Restaurant)
    fun getAllRestaurants(): Flow<List<Restaurant>>
}