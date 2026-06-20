package com.labajada.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.labajada.app.data.local.entity.DishEntity
import com.labajada.app.data.local.entity.FavoriteDishEntity
import com.labajada.app.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DishDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteDish(dish: FavoriteDishEntity)

    @Query("SELECT * FROM favorite_dishes ORDER BY timestamp DESC")
    fun getAllFavoriteDishes(): Flow<List<FavoriteDishEntity>>

    @Query("DELETE FROM favorite_dishes WHERE id = :dishId")
    suspend fun deleteFavoriteDishById(dishId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(query: SearchHistoryEntity)

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Query("DELETE FROM search_history")
    suspend fun clearAllSearchHistory()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuDish(dish: DishEntity)

    // ◄ CORREGIDO: Filtra los platos usando el ID del restaurante activo
    @Query("SELECT * FROM restaurant_menu WHERE restaurantId = :restaurantId")
    fun getRestaurantMenu(restaurantId: String): Flow<List<DishEntity>>

    @Query("DELETE FROM restaurant_menu WHERE id = :dishId")
    suspend fun deleteMenuDishById(dishId: String)
}
