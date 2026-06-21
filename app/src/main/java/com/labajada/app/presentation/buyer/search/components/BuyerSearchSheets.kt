package com.labajada.app.presentation.buyer.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel
import com.labajada.app.presentation.buyer.search.RadarHuarique
import com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile.EditarProfileSection
import com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile.FavoritosProfileSection
import com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile.HistorialProfileSection
import com.labajada.app.presentation.order.OrderViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerSearchSheets(
    showCartSheet: Boolean,
    onDismissCart: () -> Unit,
    selectedHuariqueForCart: RadarHuarique?,
    selectedDish: Dish?, // ◄ CAMBIO: ahora recibimos el plato real, no un String
    cantidadSeleccionada: Int,
    onCantidadChange: (Int) -> Unit,
    orderViewModel: OrderViewModel,
    showProfileSheet: Boolean,
    onDismissProfile: () -> Unit,
    profileCurrentSection: String,
    onSectionChange: (String) -> Unit,
    searchViewModel: BuyerSearchViewModel,
    onLogout: () -> Unit
) {
    val nombreCliente by searchViewModel.currentBuyerName.collectAsState()

    if (showCartSheet && selectedHuariqueForCart != null && selectedDish != null) {
        val huarique = selectedHuariqueForCart
        val plato = selectedDish

        val precioPlato = plato.price.toDoubleOrNull() ?: 0.0 // ◄ precio REAL del plato
        val montoTotalCalculado = precioPlato * cantidadSeleccionada

        ModalBottomSheet(onDismissRequest = onDismissCart, containerColor = Color.White) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Detalle de tu pedido",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF212121)
                )
                HorizontalDivider(color = Color(0xFFEEEEEE))

                //  imagen del plato
                AsyncImage(
                    model = if (plato.imagePath.startsWith("http") || plato.imagePath.startsWith("content") || plato.imagePath.startsWith("/"))
                        plato.imagePath else "https://placeholder.com",
                    contentDescription = "Foto de ${plato.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Text(
                        text = huarique.nombre,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = plato.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF212121)
                    )
                    Text(
                        text = "Precio unitario: S/. ${String.format(Locale.US, "%.2f", precioPlato)}",
                        fontSize = 13.sp,
                        color = Color(0xFF757575)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Selecciona la cantidad",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    FilledIconButton(
                        onClick = { if (cantidadSeleccionada > 1) onCantidadChange(cantidadSeleccionada - 1) },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFEEEEEE))
                    ) {
                        Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                    }

                    Text(
                        text = "$cantidadSeleccionada",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF212121)
                    )

                    FilledIconButton(
                        onClick = { onCantidadChange(cantidadSeleccionada + 1) },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFEEEEEE))
                    ) {
                        Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total a pagar:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF757575))
                    Text(
                        text = "S/. ${String.format(Locale.US, "%.2f", montoTotalCalculado)}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD32F2F)
                    )
                }

                Button(
                    onClick = {
                        orderViewModel.enviarPedidoAlHuarique(
                            restaurantId = huarique.id,
                            cliente = nombreCliente.ifBlank { "Comensal" },
                            plato = plato.name,
                            precio = precioPlato,
                            cantidad = cantidadSeleccionada
                        )
                        onDismissCart()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirmar y Enviar Pedido al Toque", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }

    // PANEL DE PERFIL
    if (showProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissProfile,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .navigationBarsPadding()
            ) {
                when (profileCurrentSection) {
                    "MENU" -> MenuProfileSection(
                        viewModel = searchViewModel,
                        onSectionChange = onSectionChange,
                        onDismissProfile = onDismissProfile,
                        onLogout = onLogout
                    )
                    "FAVORITOS" -> FavoritosProfileSection(searchViewModel, onSectionChange)
                    "HISTORIAL" -> HistorialProfileSection(searchViewModel, onSectionChange)
                    "EDITAR" -> EditarProfileSection(onSectionChange = onSectionChange)
                }
            }
        }
    }
}
