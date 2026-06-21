package com.labajada.app.domain.repository

import com.labajada.app.data.local.entity.FavoriteRestaurantEntity
import com.labajada.app.data.local.entity.SearchHistoryEntity
import com.labajada.app.domain.model.Dish
import com.labajada.app.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow

interface LocalDishRepository {
    suspend fun saveFavoriteRestaurant(dish: FavoriteRestaurantEntity)
    fun getFavoriteRestaurants(): Flow<List<FavoriteRestaurantEntity>>
    suspend fun removeFavoriteRestaurant(restaurantId: String)
    suspend fun saveSearchQuery(query: SearchHistoryEntity)
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>
    suspend fun clearHistory()

    // --- FLUJO RESTAURANTE ---
    suspend fun saveDishToMenu(dish: Dish)
    fun getMenuOfTheDay(restaurantId: String): Flow<List<Dish>> // ◄ Firmado y corregido con éxito
    suspend fun deleteDishFromMenu(dishId: String)
    suspend fun updateDishInMenu(dish: Dish)

    fun getRestaurantById(restaurantId: String): Flow<Restaurant?>

    suspend fun updateRestaurantProfile(restaurant: Restaurant)
}
