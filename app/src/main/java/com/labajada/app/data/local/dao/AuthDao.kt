package com.labajada.app.data.local.dao

import androidx.room.*
import com.labajada.app.data.local.entity.BuyerEntity
import com.labajada.app.data.local.entity.RestaurantEntity
import com.labajada.app.data.local.entity.SessionEntity

@Dao
interface AuthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuyer(buyer: BuyerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: RestaurantEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: SessionEntity)

    @Query("SELECT * FROM user_session WHERE id = 1 LIMIT 1")
    suspend fun getActiveSession(): SessionEntity?

    @Query("SELECT * FROM buyers WHERE email = :email AND password = :password LIMIT 1")
    suspend fun loginBuyer(email: String, password: String): BuyerEntity?

    @Query("SELECT * FROM restaurants WHERE email = :email AND password = :password LIMIT 1")
    suspend fun loginRestaurant(email: String, password: String): RestaurantEntity?

    @Query("DELETE FROM user_session")
    suspend fun logout()
}
