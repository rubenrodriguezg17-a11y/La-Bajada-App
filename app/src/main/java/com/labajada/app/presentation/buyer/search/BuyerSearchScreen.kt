package com.labajada.app.presentation.buyer.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.labajada.app.presentation.order.OrderViewModel // 💡 El nuevo cerebro de transacciones

// Estructura de negocio tipada con valores decimales para la caja registradora 💰
data class RadarHuarique(
    val id: String,
    val nombre: String,
    val category: String,
    val precioPromedio: Double,
    val distancia: String,
    val latitud: Double,
    val longitud: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerSearchScreen(
    onNavigateToMap: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    val searchViewModel = remember { BuyerSearchViewModel(context) }
    val orderViewModel = remember { OrderViewModel(context) }

    val viewModel = remember { BuyerSearchViewModel(context) }

    var showProfileSheet by remember { mutableStateOf(false) }
    var showCartSheet by remember { mutableStateOf(false) }
    var profileCurrentSection by remember { mutableStateOf("MENU") }

    // Estados para transferir datos al Carrito flotante
    var selectedHuariqueForCart by remember { mutableStateOf<RadarHuarique?>(null) }
    var cantidadSeleccionada by remember { mutableIntStateOf(1) }

    var filtroSeleccionado by remember { mutableStateOf("🔥 Populares") }
    val filtrosYCategorias = listOf("🔥 Populares", "📍 Cerca (< 2km)", "💰 Económicos", "🐟 Ceviches", "🍔 Bajadas")

    // Lista de restaurantes cargados en el mapa
    val huariquesRadar = remember {
        listOf(
            RadarHuarique("1", "El Cevichazo Fino", "🐟 Ceviches", 15.00, "0.8 km", -8.1116, -79.0287),
            RadarHuarique("2", "La Bajada Criolla", "🍲 Criollo", 18.00, "1.4 km", -8.1150, -79.0320),
            RadarHuarique("3", "Burgers El Tío Toque", "🍔 Bajadas", 12.00, "2.1 km", -8.1090, -79.0250)
        )
    }

    // Ubicación inicial de la cámara en Trujillo por defecto
    val ubicacionCliente = LatLng(-8.1116, -79.0287)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ubicacionCliente, 14.5f)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(16.dp)
    ) {
        // Fila Superior: Título y Acceso Directo al Perfil
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "¡Encuentra tu bitoque!", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color(0xFF212121))
                Text(text = "Huariques listos al toque en tu zona", fontSize = 14.sp, color = Color(0xFF757575))
            }

            IconButton(
                onClick = {
                    profileCurrentSection = "MENU"
                    showProfileSheet = true
                },
                modifier = Modifier.background(Color(0xFFFFEB3B), shape = RoundedCornerShape(50.dp)).size(44.dp)
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Perfil", tint = Color(0xFF212121))
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Barra de Búsqueda Conectada a tu Persistencia Local de Room 🔍
        OutlinedTextField(
            value = searchViewModel.searchQuery.value,
            onValueChange = { searchViewModel.searchQuery.value = it },
            placeholder = { Text("¿Qué tienes ganas de comer hoy?") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.clickable { searchViewModel.ejecutarBusqueda() } // Inyecta en la tabla search_history 💾
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD32F2F), focusedLabelColor = Color(0xFFD32F2F),
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fila Horizontal de Filtros Inteligentes
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filtrosYCategorias) { filtro ->
                val esSeleccionado = filtro == filtroSeleccionado
                FilterChip(
                    selected = esSeleccionado,
                    onClick = { filtroSeleccionado = filtro },
                    label = { Text(filtro, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFD32F2F), selectedLabelColor = Color.White,
                        containerColor = Color(0xFFEEEEEE), labelColor = Color(0xFF616161)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Huariques en tu radar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121), modifier = Modifier.padding(bottom = 10.dp))

        // 🗺️ Tarjeta que encierra el mapa de Google nativo e interactivo
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
                ) {
                    Marker(
                        state = rememberMarkerState(position = ubicacionCliente),
                        title = "Tu ubicación",
                        icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE)
                    )
                    huariquesRadar.forEach { huarique ->
                        Marker(state = rememberMarkerState(position = LatLng(huarique.latitud, huarique.longitud)), title = huarique.nombre, snippet = "${huarique.category} • ${huarique.distancia}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // 🛞 Carrusel Inferior con tu lógica de Corazones Booleanos + Botón de Carrito de Compras 🛍Header
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            items(huariquesRadar) { huarique ->
                Card(
                    modifier = Modifier.width(240.dp).clickable {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(huarique.latitud, huarique.longitud), 16f)
                    },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = huarique.nombre, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121), maxLines = 1)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "📍 ${huarique.distancia} • ${huarique.category}", fontSize = 12.sp, color = Color(0xFF757575))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "S/. ${String.format("%.2f", huarique.precioPromedio)}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFD32F2F))

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                // 💡 Tu lógica del corazón dinámico que cambia de estado y guarda en Room
                                var isFavorite by remember { mutableStateOf(false) }
                                Icon(
                                    imageVector = if (isFavorite) androidx.compose.material.icons.Icons.Default.Favorite else androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (isFavorite) Color(0xFFD32F2F) else Color.Gray,
                                    modifier = Modifier.size(22.dp).clickable {
                                        isFavorite = !isFavorite
                                        if (isFavorite) searchViewModel.agregarPlatoAFavoritos(huarique.nombre, "S/. ${huarique.precioPromedio}")
                                        else searchViewModel.quitarPlatoDeFavoritos(huarique.id)
                                    }
                                )

                                // 🛍️ NUEVO BOTÓN PRO: Abre el carrito de compras inferior para este huarique
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Pedir",
                                    tint = Color(0xFF263238),
                                    modifier = Modifier.size(22.dp).clickable {
                                        selectedHuariqueForCart = huarique
                                        cantidadSeleccionada = 1 // Resetea el sumador a 1
                                        showCartSheet = true // Levanta la hoja de compras
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    // 🛍️ HOJA FLOTANTE DEL CARRITO DE COMPRAS CON SUMADOR (+ y -) Y CÁLCULO DE TOTAL
    if (showCartSheet && selectedHuariqueForCart != null) {
        val huarique = selectedHuariqueForCart!!
        val platoSimulado = when(huarique.nombre) {
            "El Cevichazo Fino" -> "Ceviche de Tollo"
            "La Bajada Criolla" -> "Lomo Saltado"
            else -> "Hamburguesa Extrema"
        }
        val precioPlato = huarique.precioPromedio
        val montoTotalCalculado = precioPlato * cantidadSeleccionada // Multiplicación en vivo 💰

        ModalBottomSheet(onDismissRequest = { showCartSheet = false }, containerColor = Color.White) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp).navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Detalle de tu pedido 🛒", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF212121))
                HorizontalDivider(color = Color(0xFFEEEEEE))

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Text(text = huarique.nombre, fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(text = platoSimulado, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF212121))
                    Text(text = "Precio unitario: S/. ${String.format("%.2f", precioPlato)}", fontSize = 13.sp, color = Color(0xFF757575))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Selecciona la cantidad", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))

                // ➕ / ➖ CONTROLES DEL SUMADOR MATEMÁTICO EN COMPOSICIÓN
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    FilledIconButton(
                        onClick = { if (cantidadSeleccionada > 1) cantidadSeleccionada-- },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFEEEEEE))
                    ) { Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121)) }

                    Text(text = "$cantidadSeleccionada", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF212121))

                    FilledIconButton(
                        onClick = { cantidadSeleccionada++ },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFEEEEEE))
                    ) { Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121)) }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))

                // Fila de resumen de cuenta
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Total a pagar:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF757575))
                    Text("S/. ${String.format("%.2f", montoTotalCalculado)}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFFD32F2F))
                }

                // 🚀 BOTÓN DE ENVÍO FINAL: Registra la transacción física en la tabla orders_table de Room
                Button(
                    onClick = {
                        orderViewModel.enviarPedidoAlHuarique(
                            cliente = "Ruben (Comensal)", // Simula el perfil autenticado
                            plato = platoSimulado,
                            precio = precioPlato,
                            cantidad = cantidadSeleccionada
                        )
                        showCartSheet = false // Cierra el carrito
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirmar y Enviar Pedido al Toque", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }


    // PANEL DE GESTIÓN DE PERFIL (Múltiples vistas dinámicas)
    if (showProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProfileSheet = false },
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
                    "MENU" -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(modifier = Modifier.size(72.dp).background(Color(0xFFFFEB3B), shape = RoundedCornerShape(36.dp)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(38.dp), tint = Color(0xFF212121))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "¡Hola, Bitoquero!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF212121))
                            Text(text = "cliente.bajada@email.com", fontSize = 13.sp, color = Color(0xFF757575))
                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                            Spacer(modifier = Modifier.height(8.dp))

                            // Botón 1: Historial de Antojos (Muestra tus búsquedas recientes)
                            Row(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { profileCurrentSection = "HISTORIAL" }.padding(vertical = 14.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF616161)) // Cambiado a icono de reloj/historial
                                Spacer(modifier = Modifier.width(14.dp))
                                Text("Historial de antojos", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121))
                            }

                            //  BOTÓN 2: NUEVA OPCIÓN DE FAVORITOS AGREGADA
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { profileCurrentSection = "FAVORITOS" }
                                    .padding(vertical = 14.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.FavoriteBorder, // 💡 Ruta absoluta completa
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text("Mis platos favoritos", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121))
                            }

                            // Botón 3: Editar Perfil
                            Row(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { profileCurrentSection = "EDITAR" }.padding(vertical = 14.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF616161))
                                Spacer(modifier = Modifier.width(14.dp))
                                Text("Editar perfil", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121))
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showProfileSheet = false; onLogout() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    "FAVORITOS" -> {
                        val favoritosReal by viewModel.platosFavoritosRoom.collectAsState()

                        Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { profileCurrentSection = "MENU" }.padding(bottom = 16.dp)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFFD32F2F))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Volver", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                            }

                            Text("Mis Platos Favoritos", fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 12.dp))

                            if (favoritosReal.isEmpty()) {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                                    Text("Aún no has guardado platos favoritos.", color = Color.Gray, fontSize = 14.sp)
                                }
                            } else {
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    items(favoritosReal, key = { it.id }) { plato ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
                                        ) {
                                            Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Column {
                                                    Text(text = plato.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                                                    Text(text = plato.price, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                                                }
                                                // Icono de eliminación rápida si el comensal se arrepiente
                                                IconButton(onClick = {
                                                    viewModel.quitarPlatoDeFavoritos(plato.id)
                                                }) {
                                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Gray)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "HISTORIAL" -> {
                        val historialReal by viewModel.searchHistory.collectAsState()

                        Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                            // Botón de Volver
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { profileCurrentSection = "MENU" }.padding(bottom = 16.dp)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFFD32F2F))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Volver", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Historial de antojos",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF212121)
                                )

                                // 🚨 BOTÓN DE LIMPIEZA TOTAL EN ROOM
                                if (historialReal.isNotEmpty()) {
                                    Text(
                                        text = "Limpiar todo",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F), // Rojo Bajada sutil
                                        modifier = Modifier.clickable { viewModel.borrarTodoElHistorial() }
                                    )
                                }
                            }

                            if (historialReal.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Aún no tienes antojos registrados.", color = Color.Gray, fontSize = 14.sp)
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(historialReal, key = { it.id }) { item ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    text = "Búsqueda reciente",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFD32F2F)
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = item.searchQuery,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFF212121),
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "EDITAR" -> {
                        // Campos de texto reactivos para la información personal
                        var editNombre by remember { mutableStateOf("Ruben") }
                        var editApellido by remember { mutableStateOf("Cib") }
                        var editDepartamento by remember { mutableStateOf("La Libertad") }
                        var editProvincia by remember { mutableStateOf("Trujillo") }
                        var editTelefono by remember { mutableStateOf("987654321") }
                        val scrollForm = rememberScrollState()

                        Column(modifier = Modifier.fillMaxWidth().heightIn(max = 450.dp).verticalScroll(scrollForm)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { profileCurrentSection = "MENU" }.padding(bottom = 16.dp)) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFFD32F2F))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Volver", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                            }
                            Text("Información Personal", fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 16.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(value = editNombre, onValueChange = { editNombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                                OutlinedTextField(value = editApellido, onValueChange = { editApellido = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                                OutlinedTextField(value = editDepartamento, onValueChange = { editDepartamento = it }, label = { Text("Departamento") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                                OutlinedTextField(value = editProvincia, onValueChange = { editProvincia = it }, label = { Text("Provincia") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                                OutlinedTextField(value = editTelefono, onValueChange = { editTelefono = it }, label = { Text("Número de Teléfono") },
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { profileCurrentSection = "MENU" /* TODO: Persistir cambios en Room / Firestore */ },
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
