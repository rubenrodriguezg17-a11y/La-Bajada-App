package com.labajada.app.presentation.restaurant.dashboard.components.pedidos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.domain.model.Order
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel

@Composable
fun PedidosScreen(
    viewModel: RestaurantDashboardViewModel,
    pedidosActivosList: List<Order>
) {
    if (pedidosActivosList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🍽️",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No hay pedidos pendientes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263238)
                )
                Text(
                    text = "¡Cocina limpia por el momento!",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            items(pedidosActivosList, key = { it.id }) { pedido ->
                PedidoRowItem(
                    pedidoId = pedido.id,
                    buyerName = pedido.buyerName,
                    dishName = pedido.dishName,
                    quantity = pedido.quantity,
                    totalPrice = pedido.totalPrice,
                    status = pedido.status,
                    onAccionClick = {
                        viewModel.cambiarEstadoPedido(pedido.id, pedido.status)
                    }
                )
            }
        }
    }
}