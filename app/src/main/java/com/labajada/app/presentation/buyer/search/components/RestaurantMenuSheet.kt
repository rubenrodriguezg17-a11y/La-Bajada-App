package com.labajada.app.presentation.buyer.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.labajada.app.domain.model.Dish
import com.labajada.app.presentation.buyer.search.RadarHuarique

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantMenuSheet(
    huarique: RadarHuarique,
    menu: List<Dish>,
    onDismiss: () -> Unit,
    onDishSelected: (Dish) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = huarique.nombre,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF212121)
            )
            Text(
                text = "Elige un plato del menú",
                fontSize = 13.sp,
                color = Color(0xFF757575)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (menu.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Este huarique aún no publicó su menú del día.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.heightIn(max = 420.dp)
                ) {
                    items(menu, key = { it.id }) { plato ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDishSelected(plato) },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = if (plato.imagePath.startsWith("http") || plato.imagePath.startsWith("content") || plato.imagePath.startsWith("/"))
                                        plato.imagePath else "https://placeholder.com",
                                    contentDescription = "Foto de ${plato.name}",
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF5F5F5)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = plato.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF212121)
                                    )
                                    Text(
                                        text = plato.price,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFD32F2F)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}