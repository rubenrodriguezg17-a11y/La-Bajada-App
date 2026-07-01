package com.labajada.app.presentation.restaurant.dashboard.components.pedidos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.domain.model.OrderStatus
import java.util.Locale

@Composable
fun PedidoRowItem(
    pedidoId: String,
    buyerName: String,
    dishName: String,
    quantity: Int,
    totalPrice: Double,
    status: OrderStatus,
    onAccionClick: () -> Unit
) {
    val esPendiente = status == OrderStatus.PENDING
    val colorBoton = if (esPendiente) Color(0xFF1976D2) else Color(0xFF2E7D32)
    val textoBoton = if (esPendiente) "Marcar Listo" else "Despachar"
    val tagEstado = if (esPendiente) "Pendiente" else "Listo para envío"
    val colorTag = if (esPendiente) Color(0xFFFFF3E0) else Color(0xFFE8F5E9)
    val colorTagText = if (esPendiente) Color(0xFFE65100) else Color(0xFF2E7D32)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buyerName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263238)
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = colorTag),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = tagEstado,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorTagText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "${quantity}x $dishName",
                fontSize = 14.sp,
                color = Color.Gray
            )

            HorizontalDivider(color = Color(0xFFEEEEEE))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "S/. ${String.format(Locale.US, "%.2f", totalPrice)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2E7D32)
                )
                Button(
                    onClick = onAccionClick,
                    colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
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
}