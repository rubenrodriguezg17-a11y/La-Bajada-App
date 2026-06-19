package com.labajada.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userRole: Flow<String?>
    suspend fun saveUserRole(role: String)
    suspend fun clearPreferences() // Útil si luego quieres implementar un "Cerrar Sesión"
}
