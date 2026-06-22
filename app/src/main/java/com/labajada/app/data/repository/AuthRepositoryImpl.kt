package com.labajada.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.labajada.app.data.local.dao.AuthDao
import com.labajada.app.data.local.dao.DishDao
import com.labajada.app.data.local.entity.BuyerEntity
import com.labajada.app.data.local.entity.RestaurantEntity
import com.labajada.app.data.local.entity.SessionEntity
import com.labajada.app.data.mapper.toDomain
import com.labajada.app.domain.model.Buyer
import com.labajada.app.domain.model.Restaurant
import com.labajada.app.domain.model.Session
import com.labajada.app.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val authDao: AuthDao,
    private val dishDao: DishDao
) : AuthRepository {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // ◄ 1. LOGIN COMPRADOR CORREGIDO
    override suspend fun loginBuyer(email: String, password: String): Buyer? {
        return try {
            val authResult: AuthResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUid = authResult.user?.uid ?: return null

            val numericIdString = firebaseUid.hashCode().toString()
            val buyerEntity = dishDao.getBuyerById(numericIdString)

            if (buyerEntity != null) {
                saveSession(Session(userId = numericIdString, email = email, role = "BUYER"))
                buyerEntity.toDomain()
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun loginRestaurant(email: String, password: String): Restaurant? {
        return try {
            val authResult: AuthResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUid = authResult.user?.uid ?: return null

            val numericIdString = firebaseUid.hashCode().toString()
            val restaurantEntity = dishDao.getRestaurantByIdOnce(numericIdString)

            if (restaurantEntity != null) {
                saveSession(Session(userId = numericIdString, email = email, role = "RESTAURANT"))
                restaurantEntity.toDomain()
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun registerBuyer(buyer: Buyer): Long {
        try {
            val authResult: AuthResult = firebaseAuth.createUserWithEmailAndPassword(buyer.email, buyer.password).await()
            val firebaseUid = authResult.user?.uid ?: throw Exception("No se obtuvo el UID de Firebase.")

            val buyerEntity = BuyerEntity(
                id = firebaseUid.hashCode(),
                name = buyer.name,
                phone = buyer.phone,
                departamento = buyer.departamento,
                provincia = buyer.provincia,
                email = buyer.email,
                password = ""
            )
            return authDao.insertBuyer(buyerEntity)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    // ◄ 4. REGISTRO RESTAURANTE
    override suspend fun registerRestaurant(restaurant: Restaurant): Long {
        try {
            val authResult: AuthResult = firebaseAuth.createUserWithEmailAndPassword(restaurant.email, restaurant.password).await()
            val firebaseUid = authResult.user?.uid ?: throw Exception("No se obtuvo el UID de Firebase.")

            val restaurantEntity = RestaurantEntity(
                id = firebaseUid.hashCode(), // ◄ Este es el valor real (Ej: 4829302)
                restaurantName = restaurant.restaurantName,
                rucNumber = restaurant.rucNumber,
                phoneNumber = restaurant.phoneNumber,
                selectedCategory = restaurant.selectedCategory,
                addressDetails = restaurant.addressDetails,
                latitude = restaurant.latitude,
                longitude = restaurant.longitude,
                email = restaurant.email,
                password = ""
            )

            authDao.insertRestaurant(restaurantEntity)
            return firebaseUid.hashCode().toLong() // ◄ CORREGIDO: Retornamos el hash real insertado, no el index de SQLite
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun saveSession(session: Session) {
        authDao.logout()
        val entity = SessionEntity(
            id = 1,
            userId = session.userId,
            email = session.email,
            role = session.role
        )
        authDao.saveSession(entity)
    }

    override suspend fun logout() {
        authDao.logout()
    }

    override suspend fun getActiveSession(): Session? {
        return authDao.getActiveSession()?.toDomain()
    }
}