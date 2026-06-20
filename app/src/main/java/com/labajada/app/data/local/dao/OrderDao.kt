package com.labajada.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.labajada.app.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("SELECT * FROM orders_table WHERE status != 'DISPATCHED' ORDER BY id ASC") // Ajustado a 'id' si no manejas timestamp en Entity
    fun getActiveOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders_table WHERE status = 'DISPATCHED'")
    fun getDispatchedOrders(): Flow<List<OrderEntity>>

    // ◄ CORREGIDO: Consulta quirúrgica para cambiar solo el estado sin romper ni borrar el pedido
    @Query("UPDATE orders_table SET status = :newStatus WHERE id = :orderId")
    suspend fun updateOrderStatusById(orderId: String, newStatus: String)
}
