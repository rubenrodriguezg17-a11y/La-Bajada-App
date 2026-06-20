package com.labajada.app.domain.usecase.auth

import com.labajada.app.domain.repository.AuthRepository
import com.labajada.app.domain.repository.UserPreferencesRepository

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend fun execute() {
        authRepository.logout()           // Borra sesión en Room
        userPreferencesRepository.saveUserRole("") // Borra rol en DataStore
    }
}