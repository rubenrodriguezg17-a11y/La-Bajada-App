package com.labajada.app.presentation.restaurant.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            items(pedidosActivosList, key = { it.id }) { miPedido ->
                // Usamos la nueva tarjeta exclusiva evitando nombres repetidos
                TarjetaPedidoDashboard(
                    pedidoId = miPedido.id,
                    buyerName = miPedido.buyerName,
                    dishName = miPedido.dishName,
                    quantity = miPedido.quantity,
                    totalPrice = miPedido.totalPrice,
                    status = miPedido.status,
                    onAccionClick = {
                        viewModel.cambiarEstadoPedido(miPedido.id, miPedido.status)
                    }
                )
            }
        }
    }
}

// ◄ NUEVA FUNCIÓN: Con parámetros simples y nombre único para que no choque con nada
@Composable
fun TarjetaPedidoDashboard(
    pedidoId: String,
    buyerName: String,
    dishName: String,
    quantity: Int,
    totalPrice: Double,
    status: OrderStatus,
    onAccionClick: () -> Unit
) {
    val esPendiente = status == OrderStatus.PENDING
    val tagEstado = if (esPendiente) "[Pendiente]" else "[Listo]"
    val colorBoton = if (esPendiente) Color(0xFF1976D2) else Color(0xFF2E7D32)
    val textoBoton = if (esPendiente) "Marcar Listo" else "Despachar"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$buyerName $tagEstado",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263238)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${quantity}x $dishName",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "S/. ${String.format("%.2f", totalPrice)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32)
                )
            }

            Button(
                onClick = onAccionClick,
                colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(38.dp)
            ) {
                Text(
                    text = textoBoton,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}