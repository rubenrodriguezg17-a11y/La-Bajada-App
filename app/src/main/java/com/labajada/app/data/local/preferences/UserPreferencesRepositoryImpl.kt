package com.labajada.app.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.labajada.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "la_bajada_user_prefs")

class UserPreferencesRepositoryImpl(
    private val context: Context
) : UserPreferencesRepository {

    companion object {
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
    }

    // LECTURA CORREGIDA Y SEGURA:
    override val userRole: Flow<String?> = context.dataStore.data
        .catch { exception ->
            // Si hay un error leyendo el archivo físico, emitimos un estado vacío seguro
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Si el valor no existe (primera vez), devolvemos "" de forma explícita en vez de null
            preferences[USER_ROLE_KEY] ?: ""
        }

    override suspend fun saveUserRole(role: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ROLE_KEY] = role
        }
    }

    override suspend fun clearPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
