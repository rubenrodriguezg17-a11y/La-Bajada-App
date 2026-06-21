package com.labajada.app.data.repository

import com.labajada.app.data.local.dao.AuthDao
import com.labajada.app.data.local.entity.SessionEntity
import com.labajada.app.domain.model.Buyer
import com.labajada.app.domain.model.Restaurant
import com.labajada.app.domain.model.Session
import com.labajada.app.domain.repository.AuthRepository
import com.labajada.app.data.mapper.toDomain
import com.labajada.app.data.mapper.toEntity

class AuthRepositoryImpl(
    private val authDao: AuthDao
) : AuthRepository {

    override suspend fun loginBuyer(email: String, password: String): Buyer? {
        val buyerEntity = authDao.loginBuyer(email, password)

        if (buyerEntity != null) {
            val session = SessionEntity(
                id = 1,
                userId = buyerEntity.id.toString(),
                role = "BUYER",
                email = buyerEntity.email
            )
            authDao.saveSession(session)
        }

        return buyerEntity?.toDomain()
    }

    override suspend fun loginRestaurant(email: String, password: String): Restaurant? {
        val restaurantEntity = authDao.loginRestaurant(email, password)

        if (restaurantEntity != null) {
            val session = SessionEntity(
                id = 1,
                userId = restaurantEntity.id.toString(),
                role = "RESTAURANT",
                email = restaurantEntity.email
            )
            authDao.saveSession(session)
        }

        return restaurantEntity?.toDomain()
    }

    override suspend fun saveSession(session: Session) {
        authDao.saveSession(session.toEntity())
    }

    override suspend fun getActiveSession(): Session? {
        return authDao.getActiveSession()?.toDomain()
    }

    override suspend fun logout() {
        authDao.logout()
    }

    override suspend fun registerBuyer(buyer: Buyer): Long {
        return authDao.insertBuyer(buyer.toEntity())
    }


    override suspend fun registerRestaurant(restaurant: Restaurant): Long {
        return authDao.insertRestaurant(restaurant.toEntity())
    }

}
