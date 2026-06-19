package com.labajada.app.presentation.restaurant.dashboard

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.labajada.app.domain.model.OrderStatus
import com.labajada.app.presentation.order.OrderViewModel
import com.labajada.app.presentation.restaurant.dashboard.components.DishRowItem
import com.labajada.app.presentation.restaurant.dashboard.components.MetricCard
import com.labajada.app.presentation.restaurant.dashboard.components.PedidoRowItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDashboardScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val viewModel = remember { RestaurantDashboardViewModel(context) }
    val orderViewModel = remember { OrderViewModel(context) }

    val platillosDelDia by viewModel.platillosDelDia.collectAsState()

    val pedidosActivosList by orderViewModel.pedidosActivos.collectAsState()

    val sheetState = rememberModalBottomSheetState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showFormSheet by remember { mutableStateOf(false) }

    // --- Estados de la Ficha Técnica Comercial ---
    var isEditingProfile by remember { mutableStateOf(false) }
    var resNameByOwner by remember { mutableStateOf("Mi Cocina Principal") }
    var resRucByOwner by remember { mutableStateOf("20123456789") }
    var resPhoneByOwner by remember { mutableStateOf("945612378") }
    var resAddressByOwner by remember { mutableStateOf("Av. Larco 123, Trujillo") }
    var resCategoryByOwner by remember { mutableStateOf("🍲 Criollo") }

    var resLatitude by remember { mutableDoubleStateOf(-8.1116) }
    var resLongitude by remember { mutableDoubleStateOf(-79.0287) }
    var showProfileMapDialog by remember { mutableStateOf(false) }
    var expandedProfileCategory by remember { mutableStateOf(false) }
    val categoriasDisponibles = listOf("🐟 Cevichería", "🍲 Criollo", "🍔 Fast Food / Bajadas", "🔥 Pollería", "🇨🇳 Chifa")

    val scrollProfileState = rememberScrollState()

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) viewModel.selectedImageUri.value = uri }
    Scaffold(
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.prepararNuevoPlatillo()
                        showFormSheet = true
                    },
                    containerColor = Color(0xFF263238),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nuevo Platillo", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Mi Cocina Dashboard",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF263238),
                modifier = Modifier.padding(top = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "Pedidos en Cola",
                    value = "${pedidosActivosList.size}",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(title = "Ganancias", value = "S/. 299", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent, contentColor = Color(0xFF263238)) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0; isEditingProfile = false }, text = { Text("Menú del Día", fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1; isEditingProfile = false }, text = { Text("Pedidos (${pedidosActivosList.size})", fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Mi Local", fontWeight = FontWeight.Bold) })
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    itemsIndexed(items = platillosDelDia, key = { _, platillo -> platillo.id }) { index, platillo ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.StartToEnd) {
                                    viewModel.itemIndexToAction.value = index
                                    viewModel.itemIdToEdit.value = platillo.id
                                    showDeleteDialog = true
                                } else if (value == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.prepararEdicionPlatillo(index, platillo)
                                    showFormSheet = true
                                }
                                false
                            }
                        )
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color by animateColorAsState(targetValue = when (dismissState.targetValue) { SwipeToDismissBoxValue.StartToEnd -> Color(0xFFD32F2F); SwipeToDismissBoxValue.EndToStart -> Color(0xFF1976D2); else -> Color.Transparent }, label = "")
                                Box(modifier = Modifier.fillMaxSize().background(color, RoundedCornerShape(12.dp)).padding(horizontal = 20.dp), contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd) {
                                    Icon(imageVector = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Icons.Default.Delete else Icons.Default.Edit, contentDescription = null, tint = Color.White)
                                }
                            },
                            content = { DishRowItem(name = platillo.name, price = platillo.price, imageUrl = platillo.imagePath) }
                        )
                    }
                }
            }  else if (selectedTab == 1) {
                if (pedidosActivosList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay pedidos pendientes. ¡Cocina limpia! 🎉", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        // 💡 SOLUCIÓN: Usamos "pedidosActivosList" en lugar de "viewModel.pedidosActivos"
                        items(pedidosActivosList, key = { it.id }) { pedido ->

                            // Formateamos una etiqueta de estado sutil para el cocinero
                            val tagEstado = if (pedido.status == OrderStatus.PENDING) "[Pendiente]" else "[Listo]"

                            PedidoRowItem(
                                cliente = "${pedido.buyerName} $tagEstado",
                                detalle = "${pedido.quantity}x ${pedido.dishName}",
                                total = "S/. ${String.format("%.2f", pedido.totalPrice)}",
                                onDespachado = {
                                    // Avanza el pedido de estado (Si llega a DISPATCHED, se suma a tus ganancias) 💰
                                    orderViewModel.avanzarEstadoDelPedido(pedido.id, pedido.status)
                                }
                            )
                        }
                    }
                }
            } else if (selectedTab == 2) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(scrollProfileState),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (!isEditingProfile) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Información Comercial", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF263238))
                                HorizontalDivider(color = Color(0xFFEEEEEE))
                                Text(" Huarique: $resNameByOwner", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(" RUC: $resRucByOwner", fontSize = 14.sp, color = Color.Gray)
                                Text(" Contacto: $resPhoneByOwner", fontSize = 14.sp, color = Color.Gray)
                                Text(" Rubro: $resCategoryByOwner", fontSize = 14.sp, color = Color.Gray)
                                Text(" Dirección: $resAddressByOwner", fontSize = 14.sp, color = Color.Gray)
                                Text(" Satélite: Lat: ${String.format("%.4f", resLatitude)} | Lon: ${String.format("%.4f", resLongitude)}", fontSize = 13.sp, color = Color(0xFF1976D2))
                            }
                        }

                        Button(
                            onClick = { isEditingProfile = true },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editar Datos del Local", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { isEditingProfile = false }.padding(vertical = 4.dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF263238))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cancelar Edición", color = Color(0xFF263238), fontWeight = FontWeight.Bold)
                        }

                        OutlinedTextField(value = resNameByOwner, onValueChange = { resNameByOwner = it }, label = { Text("Nombre del Restaurante") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = resRucByOwner, onValueChange = { if (it.length <= 11) resRucByOwner = it }, label = { Text("RUC o DNI") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = resPhoneByOwner, onValueChange = { resPhoneByOwner = it }, label = { Text("Teléfono de Contacto") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                        ExposedDropdownMenuBox(expanded = expandedProfileCategory, onExpandedChange = { expandedProfileCategory = !expandedProfileCategory }) {
                            OutlinedTextField(
                                value = resCategoryByOwner, onValueChange = {}, readOnly = true, label = { Text("Rubro") },
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(expanded = expandedProfileCategory, onDismissRequest = { expandedProfileCategory = false }) {
                                categoriasDisponibles.forEach { item ->
                                    DropdownMenuItem(text = { Text(item) }, onClick = { resCategoryByOwner = item; expandedProfileCategory = false })
                                }
                            }
                        }

                        Box(modifier = Modifier.fillMaxWidth().clickable { showProfileMapDialog = true }) {
                            OutlinedTextField(
                                value = "Ubicación Georreferenciada", onValueChange = {}, readOnly = true, enabled = false,
                                label = { Text("Ubicación Satelital") }, leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD32F2F)) },
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = Color(0xFF263238), disabledLabelColor = Color(0xFF263238), disabledTextColor = Color(0xFF212121))
                            )
                        }

                        OutlinedTextField(value = resAddressByOwner, onValueChange = { resAddressByOwner = it }, label = { Text("Dirección Escrita") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                        Button(
                            onClick = { isEditingProfile = false },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                            shape = RoundedCornerShape(12.dp),
                            enabled = resNameByOwner.isNotBlank() && resRucByOwner.isNotBlank() && resPhoneByOwner.isNotBlank() && resAddressByOwner.isNotBlank()
                        ) {
                            Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    Button(
                        onClick = onLogout, modifier = Modifier.fillMaxWidth().height(52.dp).padding(bottom = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cerrar Sesión del Local", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    }
                }
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar platillo?") },
            text = { Text("Esta acción quitará el platillo de tu menú del día.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.itemIndexToAction.value?.let { _ ->
                            viewModel.eliminarPlatillo(viewModel.itemIdToEdit.value)
                        }
                        showDeleteDialog = false
                    }
                ) { Text("Eliminar", color = Color(0xFFD32F2F)) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    if (showFormSheet) {
        ModalBottomSheet(onDismissRequest = { showFormSheet = false }, sheetState = sheetState, containerColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = if (viewModel.isEditing.value) "Editar Platillo" else "Nuevo Platillo Gastronómico", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0F0F0))
                        .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(12.dp))
                        .clickable { pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.selectedImageUri.value != null) {
                        AsyncImage(model = viewModel.selectedImageUri.value, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Toca para añadir foto", color = Color.Gray)
                        }
                    }
                }

                OutlinedTextField(value = viewModel.dishName.value, onValueChange = { viewModel.dishName.value = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = viewModel.dishPrice.value, onValueChange = { viewModel.dishPrice.value = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                Button(
                    onClick = {
                        viewModel.guardarPlatillo(context)
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showFormSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (viewModel.isEditing.value) "Guardar Cambios" else "Agregar al Menú", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showProfileMapDialog) {
        Dialog(onDismissRequest = { showProfileMapDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Card(modifier = Modifier.fillMaxWidth(0.92f).fillMaxHeight(0.75f), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("Reubica el marcador de tu local", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
                    }
                    val puntoPerfil = remember { LatLng(resLatitude, resLongitude) }
                    val markerProfileState = rememberMarkerState(position = puntoPerfil)
                    val cameraProfileState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(puntoPerfil, 15f) }

                    Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraProfileState, onMapClick = { latLng -> markerProfileState.position = latLng }) {
                            Marker(state = markerProfileState, title = "Mi Ubicación", draggable = true)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = { showProfileMapDialog = false }, modifier = Modifier.weight(1f)) { Text("Cancelar", color = Color.Gray) }
                        Button(
                            onClick = {
                                // CORRECCIÓN 2: Salto de línea estructurado y asignación matemática limpia
                                resLatitude = markerProfileState.position.latitude
                                resLongitude = markerProfileState.position.longitude
                                showProfileMapDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("Confirmar Coordenadas", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    }
                }
            }
        }
    }
}