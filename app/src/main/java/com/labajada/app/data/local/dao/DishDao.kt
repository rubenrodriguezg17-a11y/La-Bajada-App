package com.labajada.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.labajada.app.data.local.entity.DishEntity
import com.labajada.app.data.local.entity.FavoriteRestaurantEntity
import com.labajada.app.data.local.entity.RestaurantEntity
import com.labajada.app.data.local.entity.SearchHistoryEntity
import com.labajada.app.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow

@Dao
interface DishDao {
    // --- SECCIÓN DE RESTAURANTES FAVORITOS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRestaurant(restaurant: FavoriteRestaurantEntity)

    @Query("SELECT * FROM favorite_restaurants ORDER BY timestamp DESC")
    fun getAllFavoriteRestaurants(): kotlinx.coroutines.flow.Flow<List<FavoriteRestaurantEntity>>

    @Query("DELETE FROM favorite_restaurants WHERE restaurantId = :restaurantId")
    suspend fun deleteFavoriteRestaurantById(restaurantId: String)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(query: SearchHistoryEntity)

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Query("DELETE FROM search_history")
    suspend fun clearAllSearchHistory()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuDish(dish: DishEntity)

    // Filtra los platos usando el ID del restaurante activo
    @Query("SELECT * FROM restaurant_menu WHERE restaurantId = :restaurantId")
    fun getRestaurantMenu(restaurantId: String): Flow<List<DishEntity>>

    @Query("DELETE FROM restaurant_menu WHERE id = :dishId")
    suspend fun deleteMenuDishById(dishId: String)

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): kotlinx.coroutines.flow.Flow<List<RestaurantEntity>>

    @Query("SELECT * FROM buyers WHERE id = :buyerId LIMIT 1")
    suspend fun getBuyerById(buyerId: String): com.labajada.app.data.local.entity.BuyerEntity?

    @Query("SELECT * FROM restaurant_menu")
    suspend fun getAllMenuDishesOnce(): List<DishEntity>

    @Query("SELECT * FROM restaurants WHERE id = :restaurantId LIMIT 1")
    fun getRestaurantById(restaurantId: String): Flow<RestaurantEntity?>

    @Update
    suspend fun updateRestaurantProfile(restaurant: RestaurantEntity)
}
