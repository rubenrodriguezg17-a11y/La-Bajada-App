package com.labajada.app.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.labajada.app.data.local.dao.AuthDao
import com.labajada.app.data.local.dao.BuyerDao
import com.labajada.app.data.local.dao.RestaurantDao
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
    private val buyerDao: BuyerDao,
    private val restaurantDao: RestaurantDao
) : AuthRepository {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override suspend fun loginBuyer(email: String, password: String): Buyer? {
        return try {
            val authResult: AuthResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUid = authResult.user?.uid ?: return null
            val numericId = firebaseUid.hashCode().toString()

            val buyerEntity = buyerDao.getBuyerById(numericId)
            if (buyerEntity != null) {
                saveSession(Session(userId = numericId, email = email, role = "BUYER"))
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
            val numericId = firebaseUid.hashCode().toString()

            val restaurantEntity = restaurantDao.getRestaurantByIdOnce(numericId)
            if (restaurantEntity != null) {
                saveSession(Session(userId = numericId, email = email, role = "RESTAURANT"))
                restaurantEntity.toDomain()
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun registerBuyer(buyer: Buyer): Long {
        return try {
            val authResult: AuthResult = firebaseAuth.createUserWithEmailAndPassword(buyer.email, buyer.password).await()
            val firebaseUid = authResult.user?.uid ?: throw Exception("No se obtuvo el UID de Firebase.")

            val entity = BuyerEntity(
                id = firebaseUid.hashCode(),
                name = buyer.name,
                phone = buyer.phone,
                email = buyer.email,
                password = ""
            )
            buyerDao.insertBuyer(entity)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun registerRestaurant(restaurant: Restaurant): Long {
        return try {
            val authResult: AuthResult = firebaseAuth.createUserWithEmailAndPassword(restaurant.email, restaurant.password).await()
            val firebaseUid = authResult.user?.uid ?: throw Exception("No se obtuvo el UID de Firebase.")

            val entity = RestaurantEntity(
                id = firebaseUid.hashCode(),
                email = restaurant.email,
                password = "",
                restaurantName = restaurant.restaurantName,
                rucNumber = restaurant.rucNumber,
                phoneNumber = restaurant.phoneNumber,
                selectedCategory = restaurant.selectedCategory,
                addressDetails = restaurant.addressDetails,
                latitude = restaurant.latitude,
                longitude = restaurant.longitude,
                offersDelivery = restaurant.offersDelivery,
                maxDeliveryDistanceKm = restaurant.maxDeliveryDistanceKm,
                imageUrl = restaurant.imageUrl,
                isOpen = false,
                businessHours = restaurant.businessHours
            )
            restaurantDao.insertRestaurant(entity)
            firebaseUid.hashCode().toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        }catch (e: Exception){
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun saveSession(session: Session) {
        authDao.logout()
        authDao.saveSession(
            SessionEntity(
                id = 1,
                userId = session.userId,
                email = session.email,
                role = session.role
            )
        )
    }

    override suspend fun logout() {
        authDao.logout()
    }

    override suspend fun getActiveSession(): Session? {
        return authDao.getActiveSession()?.toDomain()
    }
}