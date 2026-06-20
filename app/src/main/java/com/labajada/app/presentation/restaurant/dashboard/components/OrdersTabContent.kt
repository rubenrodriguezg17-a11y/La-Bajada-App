package com.labajada.app.presentation.restaurant.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.domain.model.Order
import com.labajada.app.domain.model.OrderStatus
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel

@Composable
fun OrdersTabContent(
    viewModel: RestaurantDashboardViewModel,
    pedidosActivosList: List<Order>
) {
    if (pedidosActivosList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay pedidos pendientes. ¡Cocina limpia!", color = Color.Gray, fontSize = 14.sp)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(pedidosActivosList, key = { it.id }) { pedido ->
                val tagEstado = if (pedido.status == OrderStatus.PENDING) "[Pendiente]" else "[Listo]"
                PedidoRowItem(
                    cliente = "${pedido.buyerName} $tagEstado",
                    detalle = "${pedido.quantity}x ${pedido.dishName}",
                    total = "S/. ${String.format("%.2f", pedido.totalPrice)}",
                    onDespachado = {
                        // Consumo directo de la acción de tu ViewModel estructurado
                        viewModel.cambiarEstadoPedido(pedido.id, pedido.status)
                    }
                )
            }
        }
    }
}