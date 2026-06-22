package com.labajada.app.domain.usecase.auth

import com.labajada.app.domain.model.Buyer
import com.labajada.app.domain.model.Session
import com.labajada.app.domain.repository.AuthRepository

class RegisterBuyerUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(buyer: Buyer): Result<Unit> {
        return try {
            // 1. Persistir comprador y obtener su ID real de la base de datos
            val generatedId = authRepository.registerBuyer(buyer)

            // 2. Crear sesión activa usando el ID correcto convertido a String (o Int según tu modelo)
            authRepository.saveSession(
                Session(
                    userId = generatedId.toString(),
                    email = buyer.email,
                    role = "BUYER"
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
