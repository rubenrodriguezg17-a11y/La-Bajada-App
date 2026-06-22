package com.labajada.app.domain.usecase.auth

import com.labajada.app.domain.model.Restaurant
import com.labajada.app.domain.model.Session
import com.labajada.app.domain.repository.AuthRepository

class RegisterRestaurantUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(restaurant: Restaurant): Result<Unit> {
        return try {
            val realHashId = authRepository.registerRestaurant(restaurant)

            authRepository.saveSession(
                Session(
                    userId = realHashId.toString(),
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