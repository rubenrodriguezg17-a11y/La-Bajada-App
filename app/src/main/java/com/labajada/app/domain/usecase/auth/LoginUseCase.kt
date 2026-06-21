package com.labajada.app.domain.usecase.auth

import com.labajada.app.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String, password: String): Result<String> {
        // 1. Intentar loguear como comprador
        val buyer = authRepository.loginBuyer(email, password)
        if (buyer != null) {
            // El repositorio ya guardó la sesión internamente
            return Result.success("BUYER")
        }
        // 2. Si no es comprador, intentar como restaurante
        val restaurant = authRepository.loginRestaurant(email, password)
        if (restaurant != null) {
            // El repositorio ya guardó la sesión internamente
            return Result.success("RESTAURANT")
        }

        return Result.failure(Exception("Correo o contraseña incorrectos"))
    }
}
