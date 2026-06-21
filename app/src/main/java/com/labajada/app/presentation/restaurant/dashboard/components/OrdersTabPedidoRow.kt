package com.labajada.app.presentation.restaurant.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrdersTabPedidoRow(cliente: String, detalle: String, total: String, onDespachado: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFFF3E0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFFFF9800))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = cliente, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
                Text(text = detalle, fontSize = 13.sp, color = Color(0xFF757575))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = total, fontWeight = FontWeight.Black, color = Color(0xFF4CAF50), fontSize = 14.sp)
            }
            Button(
                onClick = onDespachado,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Despachar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
