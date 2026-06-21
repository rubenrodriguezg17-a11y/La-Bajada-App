package com.labajada.app.domain.usecase.search

import com.labajada.app.data.local.entity.FavoriteRestaurantEntity
import com.labajada.app.domain.repository.LocalDishRepository
import kotlinx.coroutines.flow.Flow

class ManageFavoriteRestaurantUseCase(private val repository: LocalDishRepository) {

    fun getAll(): Flow<List<FavoriteRestaurantEntity>> {
        return repository.getFavoriteRestaurants()
    }

    suspend fun add(id: String, nombre: String, categoria: String, direccion: String) {
        val nuevoFavorito = FavoriteRestaurantEntity(
            restaurantId = id,
            restaurantName = nombre,
            category = categoria,
            address = direccion
        )
        repository.saveFavoriteRestaurant(nuevoFavorito) // ◄ Llama a la función correcta
    }

    suspend fun remove(restaurantId: String) {
        repository.removeFavoriteRestaurant(restaurantId) // ◄ Llama a la función correcta
    }
}
