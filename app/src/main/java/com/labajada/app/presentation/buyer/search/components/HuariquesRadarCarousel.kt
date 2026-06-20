package com.labajada.app.presentation.buyer.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.labajada.app.presentation.buyer.search.RadarHuarique
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel

@Composable
fun HuariquesRadarCarousel(
    huariquesRadar: List<RadarHuarique>,
    cameraPositionState: CameraPositionState,
    searchViewModel: BuyerSearchViewModel,
    onOpenCart: (RadarHuarique) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        items(huariquesRadar) { huarique ->
            Card(
                modifier = Modifier
                    .width(240.dp)
                    .clickable {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(huarique.latitud, huarique.longitud), 16f)
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = huarique.nombre, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121), maxLines = 1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${huarique.distancia} • ${huarique.category}", fontSize = 12.sp, color = Color(0xFF757575))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "S/. ${String.format("%.2f", huarique.precioPromedio)}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFD32F2F))

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            var isFavorite by remember { mutableStateOf(false) }
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFavorite) Color(0xFFD32F2F) else Color.Gray,
                                modifier = Modifier.size(22.dp).clickable {
                                    isFavorite = !isFavorite
                                    if (isFavorite) {
                                        searchViewModel.agregarPlatoAFavoritos(huarique.nombre, "S/. ${huarique.precioPromedio}")
                                    } else {
                                        searchViewModel.quitarPlatoDeFavoritos(huarique.id)
                                    }
                                }
                            )

                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Pedir",
                                tint = Color(0xFF263238),
                                modifier = Modifier.size(22.dp).clickable { onOpenCart(huarique) }
                            )
                        }
                    }
                }
            }
        }
    }
}