package com.labajada.app.data.repository

import com.labajada.app.data.local.dao.OrderDao
import com.labajada.app.data.local.entity.toDomain
import com.labajada.app.data.local.entity.toEntity
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderStatus
import com.labajada.app.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrderRepositoryImpl(
    private val orderDao: OrderDao
) : OrderRepository {

    override suspend fun createOrder(order: Order) {
        orderDao.insertOrder(order.toEntity())
    }

    override fun getActiveOrders(): Flow<List<Order>> {
        return orderDao.getActiveOrders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getDispatchedOrders(): Flow<List<Order>> {
        return orderDao.getDispatchedOrders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        orderDao.updateOrderStatusById(orderId, newStatus.name)
    }
}
