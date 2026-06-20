package com.labajada.app.domain.repository

import com.labajada.app.data.local.entity.FavoriteDishEntity
import com.labajada.app.data.local.entity.SearchHistoryEntity
import com.labajada.app.domain.model.Dish
import kotlinx.coroutines.flow.Flow

interface LocalDishRepository {

    // --- FLUJO COMPRADOR ---
    suspend fun saveFavoriteDish(dish: FavoriteDishEntity)
    fun getFavoriteDishes(): Flow<List<FavoriteDishEntity>>
    suspend fun removeFavoriteDish(dishId: String)
    suspend fun saveSearchQuery(query: SearchHistoryEntity)
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>
    suspend fun clearHistory()

    // --- FLUJO RESTAURANTE ---
    suspend fun saveDishToMenu(dish: Dish)
    fun getMenuOfTheDay(restaurantId: String): Flow<List<Dish>> // ◄ Firmado y corregido con éxito
    suspend fun deleteDishFromMenu(dishId: String)
    suspend fun updateDishInMenu(dish: Dish)
}
