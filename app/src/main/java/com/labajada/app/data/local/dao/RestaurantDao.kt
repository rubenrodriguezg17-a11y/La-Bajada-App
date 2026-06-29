package com.labajada.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.labajada.app.data.local.entity.FavoriteRestaurantEntity
import com.labajada.app.data.local.entity.RestaurantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {
    // Restaurantes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: RestaurantEntity): Long

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): Flow<List<RestaurantEntity>>

    @Query("SELECT * FROM restaurants WHERE id = :restaurantId LIMIT 1")
    fun getRestaurantById(restaurantId: String): Flow<RestaurantEntity?>

    @Query("SELECT * FROM restaurants WHERE id = :restaurantId LIMIT 1")
    suspend fun getRestaurantByIdOnce(restaurantId: String): RestaurantEntity?

    @Query("SELECT * FROM restaurants WHERE email = :email AND password = :password LIMIT 1")
    suspend fun loginRestaurant(email: String, password: String): RestaurantEntity?

    @Update
    suspend fun updateRestaurantProfile(restaurant: RestaurantEntity)

    // Favoritos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRestaurant(restaurant: FavoriteRestaurantEntity)

    @Query("SELECT * FROM favorite_restaurants ORDER BY timestamp DESC")
    fun getAllFavoriteRestaurants(): Flow<List<FavoriteRestaurantEntity>>

    @Query("DELETE FROM favorite_restaurants WHERE restaurantId = :restaurantId")
    suspend fun deleteFavoriteRestaurantById(restaurantId: String)
}