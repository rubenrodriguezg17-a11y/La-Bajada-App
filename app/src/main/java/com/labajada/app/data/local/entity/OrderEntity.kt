package com.labajada.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderStatus

@Entity(tableName = "orders_table")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val restaurantId: String, // ◄ CORREGIDO: Columna obligatoria para saber a qué restaurante pertenece este pedido
    val buyerName: String,
    val dishName: String,
    val dishPrice: Double,
    val quantity: Int,
    val status: String,
    val timestamp: Long
)
fun OrderEntity.toDomain() = Order(
    id = this.id,
    restaurantId = this.restaurantId, // ◄ Mapeo seguro hacia el dominio
    buyerName = this.buyerName,
    dishName = this.dishName,
    dishPrice = this.dishPrice,
    quantity = this.quantity,
    status = OrderStatus.valueOf(this.status),
    timestamp = this.timestamp
)

fun Order.toEntity() = OrderEntity(
    id = this.id,
    restaurantId = this.restaurantId, // ◄ Enlace obligatorio para Room
    buyerName = this.buyerName,
    dishName = this.dishName,
    dishPrice = this.dishPrice,
    quantity = this.quantity,
    status = this.status.name,
    timestamp = this.timestamp
)