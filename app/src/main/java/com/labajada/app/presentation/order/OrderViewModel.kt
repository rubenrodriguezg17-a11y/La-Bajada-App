package com.labajada.app.presentation.order

import android.content.Context
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.data.local.AppDatabase
import com.labajada.app.data.repository.OrderRepositoryImpl
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderStatus
import com.labajada.app.domain.repository.OrderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(context: Context) : ViewModel() {

    private val database = AppDatabase.getDatabase(context)
    private val orderRepository: OrderRepository = OrderRepositoryImpl(database.orderDao())

    val pedidosActivos = orderRepository.getActiveOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pedidosDespachados = orderRepository.getDispatchedOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gananciasHoy = derivedStateOf {
        pedidosDespachados.value.sumOf { it.dishPrice * it.quantity } // O it.totalPrice según tu modelo
    }

    fun enviarPedidoAlHuarique(cliente: String, plato: String, precio: Double, cantidad: Int) {
        viewModelScope.launch {
            val nuevaOrden = Order(
                buyerName = cliente,
                dishName = plato,
                dishPrice = precio,
                quantity = cantidad,
                status = OrderStatus.PENDING
            )
            orderRepository.createOrder(nuevaOrden)
        }
    }

    fun avanzarEstadoDelPedido(orderId: String, estadoActual: OrderStatus) {
        viewModelScope.launch {
            val siguienteEstado = when (estadoActual) {
                OrderStatus.PENDING -> OrderStatus.READY        // De Pendiente pasa a Listo
                OrderStatus.READY -> OrderStatus.DISPATCHED     // De Listo pasa a Cobrado y suma ganancias 💰
                else -> OrderStatus.DISPATCHED
            }
            orderRepository.updateOrderStatus(orderId, siguienteEstado)
        }
    }
}