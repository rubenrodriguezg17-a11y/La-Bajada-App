package com.labajada.app.domain.repository

import com.labajada.app.domain.model.Buyer
import com.labajada.app.domain.model.Restaurant
import com.labajada.app.domain.model.Session

interface AuthRepository {
    suspend fun loginBuyer(email: String, password: String): Buyer?
    suspend fun loginRestaurant(email: String, password: String): Restaurant?
    suspend fun saveSession(session: Session)
    suspend fun getActiveSession(): Session?
    suspend fun logout()
    suspend fun registerBuyer(buyer: Buyer): Long
    suspend fun registerRestaurant(restaurant: Restaurant): Long
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}