package com.labajada.app.domain.usecase.auth

import com.labajada.app.domain.model.Buyer
import com.labajada.app.domain.repository.AuthRepository
import com.labajada.app.domain.repository.BuyerRepository

class GetActiveBuyerUseCase(
    private val authRepository: AuthRepository,
    private val buyerRepository: BuyerRepository
) {
    suspend operator fun invoke(): Buyer? {
        val session = authRepository.getActiveSession() ?: return null
        return buyerRepository.getBuyerById(session.userId)
    }
}