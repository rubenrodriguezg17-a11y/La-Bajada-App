package com.labajada.app.domain.repository

import com.labajada.app.data.local.entity.FavoriteDishEntity
import com.labajada.app.data.local.entity.SearchHistoryEntity
import com.labajada.app.domain.model.Dish
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderStatus
import kotlinx.coroutines.flow.Flow

interface LocalDishRepository {


    //COMPRADOR
    suspend fun saveFavoriteDish(dish: FavoriteDishEntity)
    fun getFavoriteDishes(): Flow<List<FavoriteDishEntity>>
    suspend fun removeFavoriteDish(dishId: String)
    suspend fun saveSearchQuery(query: SearchHistoryEntity)
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>
    suspend fun clearHistory()

    // RESTAURANTE
    suspend fun saveDishToMenu(dish: Dish)
    fun getMenuOfTheDay(): Flow<List<Dish>>
    suspend fun deleteDishFromMenu(dishId: String)
    suspend fun updateDishInMenu(dish: Dish)

}
