package com.labajada.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.labajada.app.data.local.dao.AuthDao // ◄ IMPORTACIÓN AGREGADA
import com.labajada.app.data.local.dao.DishDao
import com.labajada.app.data.local.dao.OrderDao
import com.labajada.app.data.local.entity.BuyerEntity // ◄ IMPORTACIÓN AGREGADA
import com.labajada.app.data.local.entity.DishEntity
import com.labajada.app.data.local.entity.FavoriteRestaurantEntity
import com.labajada.app.data.local.entity.OrderEntity
import com.labajada.app.data.local.entity.RestaurantEntity // ◄ IMPORTACIÓN AGREGADA
import com.labajada.app.data.local.entity.SearchHistoryEntity
import com.labajada.app.data.local.entity.SessionEntity // ◄ IMPORTACIÓN AGREGADA

@Database(
    entities = [
        FavoriteRestaurantEntity::class,
        //FavoriteDishEntity::class,
        SearchHistoryEntity::class,
        DishEntity::class,
        OrderEntity::class,
        BuyerEntity::class,
        RestaurantEntity::class,
        SessionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dishDao(): DishDao
    abstract fun orderDao(): OrderDao
    abstract fun authDao(): AuthDao // ◄ NUEVO DAO EXPUESTO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "la_bajada_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
