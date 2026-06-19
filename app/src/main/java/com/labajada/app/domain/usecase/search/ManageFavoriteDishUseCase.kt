package com.labajada.app.domain.usecase.search

import com.labajada.app.data.local.entity.FavoriteDishEntity
import com.labajada.app.domain.repository.LocalDishRepository
import kotlinx.coroutines.flow.Flow

class ManageFavoriteDishUseCase(private val repository: LocalDishRepository) {

    fun getAll(): Flow<List<FavoriteDishEntity>> {
        return repository.getFavoriteDishes()
    }

    suspend fun add(nombre: String, precio: String) {
        val nuevoFavorito = FavoriteDishEntity(
            name = nombre,
            price = precio,
            imagePath = ""
        )
        repository.saveFavoriteDish(nuevoFavorito)
    }

    suspend fun remove(id: String) {
        repository.removeFavoriteDish(id)
    }
}
