package com.labajada.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderStatus

@Entity(tableName = "orders_table")
data class OrderEntity(
    @PrimaryKey val id: String,
    val buyerName: String,
    val dishName: String,
    val dishPrice: Double,
    val quantity: Int,
    val status: String,
    val timestamp: Long
)

fun OrderEntity.toDomain(): Order {
    return Order(
        id = this.id,
        buyerName = this.buyerName,
        dishName = this.dishName,
        dishPrice = this.dishPrice,
        quantity = this.quantity,
        status = OrderStatus.valueOf(this.status), // Convierte el String de vuelta a Enum
        timestamp = this.timestamp
    )
}

fun Order.toEntity(): OrderEntity {
    return OrderEntity(
        id = this.id,
        buyerName = this.buyerName,
        dishName = this.dishName,
        dishPrice = this.dishPrice,
        quantity = this.quantity,
        status = this.status.name,
        timestamp = this.timestamp
    )
}
