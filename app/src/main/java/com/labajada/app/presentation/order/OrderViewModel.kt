package com.labajada.app.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderStatus
import com.labajada.app.domain.repository.OrderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepository // ◄ INYECTADO CORRECTAMENTE
) : ViewModel() {

    val pedidosActivos = orderRepository.getActiveOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pedidosDespachados = orderRepository.getDispatchedOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ◄ CORREGIDO: Combinación reactiva y óptima usando operadores nativos de Flow
    val gananciasHoy: StateFlow<Double> = pedidosDespachados
        .combine(pedidosActivos) { despachados, _ ->
            despachados.sumOf { it.dishPrice * it.quantity }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun enviarPedidoAlHuarique(
        restaurantId: String, // ◄ Agregar este parámetro
        cliente: String,
        plato: String,
        precio: Double,
        cantidad: Int
    ) {
        viewModelScope.launch {
            val nuevaOrden = Order(
                restaurantId = restaurantId,
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
                OrderStatus.PENDING -> OrderStatus.READY
                OrderStatus.READY -> OrderStatus.DISPATCHED
                else -> OrderStatus.DISPATCHED
            }
            orderRepository.updateOrderStatus(orderId, siguienteEstado)
        }
    }
}
