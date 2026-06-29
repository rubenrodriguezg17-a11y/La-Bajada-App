package com.labajada.app.domain.repository

import com.labajada.app.domain.model.Buyer

interface BuyerRepository {
    suspend fun getBuyerById(id: String): Buyer?
    suspend fun insertBuyer(buyer: Buyer): Long
    suspend fun loginBuyer(email: String, password: String): Buyer?
}