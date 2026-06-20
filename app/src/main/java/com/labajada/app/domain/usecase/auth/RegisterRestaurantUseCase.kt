package com.labajada.app.domain.usecase.auth

import com.labajada.app.domain.model.Restaurant
import com.labajada.app.domain.model.Session
import com.labajada.app.domain.repository.AuthRepository

class RegisterRestaurantUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(restaurant: Restaurant): Result<Unit> {
        return try {
            // 1. Guardar el restaurante y capturar el ID real generado por Room
            val generatedId = authRepository.registerRestaurant(restaurant)

            // 2. Crear y guardar la sesión activa con el ID correcto
            authRepository.saveSession(
                Session(
                    userId = generatedId.toString(),
                    email = restaurant.email,
                    role = "RESTAURANT"
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
