package com.labajada.app.presentation.restaurant.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun DishRowItem(name: String, price: String, imageUrl: String = "") {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // Bajamos un toque el padding para balancear con la foto
            verticalAlignment = Alignment.CenterVertically
        ) {
            // COMPONENTE FOTO INTELIGENTE:
            if (imageUrl.isNotBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Foto de $name",
                    contentScale = ContentScale.Crop, // Recorta la imagen para que llene el cuadrado sin deformarse
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)) // Bordes redondeados estéticos para el platillo
                )
            } else {
                // Si el platillo no tiene foto, muestra un ícono de comida por defecto para que no se vea vacío
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFFBDBDBD))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Textos Informativos
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                Text(text = "Disponible al toque", fontSize = 12.sp, color = Color(0xFF4CAF50))
            }

            // Precio
            Text(
                text = price,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF263238)
            )
        }
    }
}
