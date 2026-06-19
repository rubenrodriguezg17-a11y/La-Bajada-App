package com.labajada.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.labajada.app.data.local.dao.DishDao
import com.labajada.app.data.local.dao.OrderDao
import com.labajada.app.data.local.entity.DishEntity
import com.labajada.app.data.local.entity.FavoriteDishEntity
import com.labajada.app.data.local.entity.OrderEntity
import com.labajada.app.data.local.entity.SearchHistoryEntity

@Database(
    entities = [FavoriteDishEntity::class,
        SearchHistoryEntity::class,
        DishEntity::class,
        OrderEntity::class],
    version = 2,
    exportSchema = false // Evita generar archivos adicionales de esquema en Gradle
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dishDao(): DishDao
    abstract fun orderDao(): OrderDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Si es nula, creamos la base de datos física en el disco duro del celular
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "la_bajada_database" // Nombre del archivo SQLite interno en el teléfono
                )
                    .fallbackToDestructiveMigration() // recrear tablas sin crash
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
