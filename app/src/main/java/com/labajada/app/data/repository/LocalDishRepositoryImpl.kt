package com.labajada.app.data.repository

import com.labajada.app.data.local.dao.DishDao
import com.labajada.app.data.local.entity.FavoriteDishEntity
import com.labajada.app.data.local.entity.SearchHistoryEntity
import com.labajada.app.domain.model.Dish
import com.labajada.app.domain.repository.LocalDishRepository
import com.labajada.app.data.mapper.toDomain // ◄ CORREGIDO: Importación desde el paquete central de mappers
import com.labajada.app.data.mapper.toEntity // ◄ CORREGIDO: Importación desde el paquete central de mappers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDishRepositoryImpl(private val dishDao: DishDao) : LocalDishRepository {

    override suspend fun saveFavoriteDish(dish: FavoriteDishEntity) {
        dishDao.insertFavoriteDish(dish)
    }

    override fun getFavoriteDishes(): Flow<List<FavoriteDishEntity>> {
        return dishDao.getAllFavoriteDishes()
    }

    override suspend fun removeFavoriteDish(dishId: String) {
        dishDao.deleteFavoriteDishById(dishId)
    }

    override suspend fun saveSearchQuery(query: SearchHistoryEntity) {
        dishDao.insertSearchQuery(query)
    }

    override fun getSearchHistory(): Flow<List<SearchHistoryEntity>> {
        return dishDao.getRecentSearchHistory()
    }

    override suspend fun clearHistory() {
        dishDao.clearAllSearchHistory()
    }

    // --- Módulo Restaurante ---
    override suspend fun saveDishToMenu(dish: Dish) {
        dishDao.insertMenuDish(dish.toEntity())
    }

    // ◄ CORREGIDO: Ahora recibe el restaurantId exigido por la interfaz de Dominio y el DAO
    override fun getMenuOfTheDay(restaurantId: String): Flow<List<Dish>> {
        return dishDao.getRestaurantMenu(restaurantId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteDishFromMenu(dishId: String) {
        dishDao.deleteMenuDishById(dishId)
    }

    override suspend fun updateDishInMenu(dish: Dish) {
        dishDao.insertMenuDish(dish.toEntity())
    }
}
