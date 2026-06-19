package com.labajada.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.labajada.app.domain.model.Dish

@Entity(tableName = "restaurant_menu")
data class DishEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: String,
    val imagePath: String
)

fun DishEntity.toDomain(): Dish {
    return Dish(id = this.id, name = this.name, price = this.price, imagePath = this.imagePath)
}

fun Dish.toEntity(): DishEntity {
    return DishEntity(id = this.id, name = this.name, price = this.price, imagePath = this.imagePath)
}
