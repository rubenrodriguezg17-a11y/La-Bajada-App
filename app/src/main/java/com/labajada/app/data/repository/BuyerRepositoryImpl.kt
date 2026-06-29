package com.labajada.app.data.repository

import com.labajada.app.data.local.dao.BuyerDao
import com.labajada.app.data.mapper.toDomain
import com.labajada.app.data.mapper.toEntity
import com.labajada.app.domain.model.Buyer
import com.labajada.app.domain.repository.BuyerRepository

class BuyerRepositoryImpl(
    private val buyerDao: BuyerDao
) : BuyerRepository {

    override suspend fun getBuyerById(id: String): Buyer? {
        return buyerDao.getBuyerById(id)?.toDomain()
    }

    override suspend fun insertBuyer(buyer: Buyer): Long {
        return buyerDao.insertBuyer(buyer.toEntity())
    }

    override suspend fun loginBuyer(email: String, password: String): Buyer? {
        return buyerDao.loginBuyer(email, password)?.toDomain()
    }
}