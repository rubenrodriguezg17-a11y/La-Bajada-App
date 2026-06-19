package com.labajada.app.domain.repository

import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun createOrder(order: Order)
    fun getActiveOrders(): Flow<List<Order>>
    fun getDispatchedOrders(): Flow<List<Order>>
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus)
}
