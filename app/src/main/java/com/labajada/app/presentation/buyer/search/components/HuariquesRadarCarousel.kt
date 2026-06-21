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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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
    onVerMenu: (RadarHuarique) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        items(huariquesRadar) { huarique ->
            Card(
                modifier = Modifier
                    .width(260.dp)
                    .clickable {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(huarique.latitud, huarique.longitud), 18.0f // Cambiado a tu zoom cercano 18f
                        )
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Fila Superior: Nombre del Huarique + TEXTO INTERACTIVO "Ver menú"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = huarique.nombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF212121),
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Ver menú",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F),
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .clickable { onVerMenu(huarique) }
                                .padding(start = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    // 2. Distancia y Categoría del local
                    Text(
                        text = "${huarique.distancia} • ${huarique.category}",
                        fontSize = 12.sp,
                        color = Color(0xFF757575),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Fila Inferior: Guía táctil y Botón de Favoritos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Toca para centrar en mapa",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.LightGray,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        var isFavorite by remember { mutableStateOf(false) }

                        IconButton(
                            onClick = {
                                isFavorite = !isFavorite
                                if (isFavorite) {
                                    searchViewModel.agregarRestauranteAFavoritos(
                                        id = huarique.id,
                                        nombre = huarique.nombre,
                                        categoria = huarique.category,
                                        direccion = huarique.distancia
                                    )
                                } else {
                                    searchViewModel.quitarRestauranteDeFavoritos(huarique.id)
                                }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (isFavorite) Color(0xFFD32F2F) else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
