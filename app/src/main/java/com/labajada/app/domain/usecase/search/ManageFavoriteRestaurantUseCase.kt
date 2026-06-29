package com.labajada.app.domain.usecase.search

import com.labajada.app.domain.model.FavoriteRestaurant
import com.labajada.app.domain.repository.RestaurantRepository
import kotlinx.coroutines.flow.Flow

class ManageFavoriteRestaurantUseCase(private val repository: RestaurantRepository) {

    fun getAll(): Flow<List<FavoriteRestaurant>> {
        return repository.getFavoriteRestaurants()  // ← retorna FavoriteRestaurant, no entity
    }

    suspend fun add(id: String, nombre: String, categoria: String, direccion: String) {
        repository.saveFavoriteRestaurant(
            restaurantId = id,
            restaurantName = nombre,
            category = categoria,
            address = direccion,
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun remove(restaurantId: String) {
        repository.removeFavoriteRestaurant(restaurantId)
    }
}