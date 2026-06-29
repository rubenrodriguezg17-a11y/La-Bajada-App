package com.labajada.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.labajada.app.data.local.entity.BuyerEntity

@Dao
interface BuyerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuyer(buyer: BuyerEntity): Long

    @Query("SELECT * FROM buyers WHERE email = :email AND password = :password LIMIT 1")
    suspend fun loginBuyer(email: String, password: String): BuyerEntity?

    @Query("SELECT * FROM buyers WHERE id = :buyerId LIMIT 1")
    suspend fun getBuyerById(buyerId: String): BuyerEntity?
}