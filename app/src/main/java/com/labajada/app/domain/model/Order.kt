package com.labajada.app.domain.model

data class Order(
    val id: String = java.util.UUID.randomUUID().toString(),
    val restaurantId: String,
    val buyerName: String,
    val dishName: String,
    val dishPrice: Double,
    val quantity: Int,
    val status: OrderStatus = OrderStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
) {
    val totalPrice: Double get() = dishPrice * quantity
}

enum class OrderStatus {
    PENDING,
    READY,
    DISPATCHED
}
