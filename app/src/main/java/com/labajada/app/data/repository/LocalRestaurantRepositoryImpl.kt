package com.labajada.app.data.repository

import com.labajada.app.data.local.dao.DishDao
import com.labajada.app.data.local.entity.FavoriteRestaurantEntity // ◄ CORREGIDO: Nueva entidad de restaurante favorito
import com.labajada.app.data.local.entity.RestaurantEntity
import com.labajada.app.data.local.entity.SearchHistoryEntity
import com.labajada.app.domain.model.Dish
import com.labajada.app.domain.repository.LocalDishRepository
import com.labajada.app.data.mapper.toDomain
import com.labajada.app.data.mapper.toEntity
import com.labajada.app.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class LocalRestaurantRepositoryImpl(private val dishDao: DishDao) : LocalDishRepository {


    override suspend fun saveFavoriteRestaurant(restaurant: FavoriteRestaurantEntity) {
        dishDao.insertFavoriteRestaurant(restaurant)
    }

    override fun getFavoriteRestaurants(): Flow<List<FavoriteRestaurantEntity>> {
        return dishDao.getAllFavoriteRestaurants()
    }

    override suspend fun removeFavoriteRestaurant(restaurantId: String) {
        dishDao.deleteFavoriteRestaurantById(restaurantId)
    }
    // --- MÓDULO: HISTORIAL DE BÚSQUEDAS ---

    override suspend fun saveSearchQuery(query: SearchHistoryEntity) {
        dishDao.insertSearchQuery(query)
    }

    override fun getSearchHistory(): Flow<List<SearchHistoryEntity>> {
        return dishDao.getRecentSearchHistory()
    }

    override suspend fun clearHistory() {
        dishDao.clearAllSearchHistory()
    }

    // --- MÓDULO: RESTAURANTE ---

    override suspend fun saveDishToMenu(dish: Dish) {
        dishDao.insertMenuDish(dish.toEntity())
    }

    // Filtra reactivamente el menú basándose en el identificador único del huarique
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


    override fun getRestaurantById(restaurantId: String): Flow<Restaurant?> {
        return dishDao.getRestaurantById(restaurantId).map {
            entity ->
            entity?.toDomain()        }
    }

    override suspend fun updateRestaurantProfile(restaurant: Restaurant) {
        dishDao.updateRestaurantProfile(restaurant.toEntity())
    }


}
