package com.labajada.app.presentation.buyer.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.google.maps.android.compose.*
import com.labajada.app.presentation.order.OrderViewModel // ◄ Asegura esta importación
import com.labajada.app.presentation.buyer.search.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerSearchScreen(
    searchViewModel: BuyerSearchViewModel,
    orderViewModel: OrderViewModel,
    onNavigateToMap: () -> Unit,
    onLogout: () -> Unit
) {
    // Estados de control de la UI de las Hojas Desplegables
    var showProfileSheet by remember { mutableStateOf(false) }
    var showCartSheet by remember { mutableStateOf(false) }
    var profileCurrentSection by remember { mutableStateOf("MENU") }

    // Estados de transferencia para el Carrito Flotante
    var selectedHuariqueForCart by remember { mutableStateOf<RadarHuarique?>(null) }
    var cantidadSeleccionada by remember { mutableIntStateOf(1) }
    var filtroSeleccionado by remember { mutableStateOf("Populares") }

    val filtrosYCategorias = listOf("Populares", "Cerca (<2km)", "Económicos", "Ceviches", "Bajadas")

    val huariquesRadar = remember {
        listOf(
            RadarHuarique("1", "El Cevichazo Fino", "Ceviches", 15.00, "0.8 km", -8.1116, -79.0287),
            RadarHuarique("2", "La Bajada Criolla", "Criollo", 18.00, "1.4 km", -8.1150, -79.0320),
            RadarHuarique("3", "Burgers El Tío Toque", "Bajadas", 12.00, "2.1 km", -8.1090, -79.0250)
        )
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val ubicacionClienteInicial = remember { LatLng(-8.1116, -79.0287) } // Trujillo por defecto

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ubicacionClienteInicial, 17f)
    }
    // Disparador automático al montar la pantalla
    LaunchedEffect(Unit) {
        try {
            val fusedLocationClient = com.google.android.gms.location.LocationServices
                .getFusedLocationProviderClient(context)

            @android.annotation.SuppressLint("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    // Desliza suavemente la cámara del cliente a su posición real
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val queryText by searchViewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(16.dp)
    ) {
        // Fila Superior: Título y Acceso Directo al Perfil
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "¡Encuentra tu bitoque!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF212121)
                )
                Text(
                    text = "Huariques listos al toque en tu zona",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
            }
            IconButton(
                onClick = {
                    profileCurrentSection = "MENU"
                    showProfileSheet = true
                },
                modifier = Modifier
                    .background(Color(0xFFFFEB3B), shape = RoundedCornerShape(50.dp))
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = Color(0xFF212121)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Barra de Búsqueda Conectada Reactivamente
        OutlinedTextField(
            value = queryText,
            onValueChange = { searchViewModel.onSearchQueryChange(it) },
            placeholder = { Text("¿Qué deseas comer hoy?") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.clickable { searchViewModel.ejecutarBusqueda() }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD32F2F),
                focusedLabelColor = Color(0xFFD32F2F),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fila Horizontal de Filtros
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
                        selectedContainerColor = Color(0xFFD32F2F),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFEEEEEE),
                        labelColor = Color(0xFF616161)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Huariques en tu radar",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Card del mapa interactivo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    // ◄ CONFIGURACIÓN MODERNA: Vincula el punto azul de ubicación real
                    properties = MapProperties(
                        isMyLocationEnabled = true
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = true,
                        rotationGesturesEnabled = false, // ◄ BLOQUEADO: El mapa ya no se girará de lado con los dedos
                        tiltGesturesEnabled = false,     // Bloquea la inclinación en 3D para evitar distorsiones
                        scrollGesturesEnabled = true,    // ◄ PERMITIDO: El usuario sí puede desplazarse y correr el mapa
                        zoomGesturesEnabled = true   // Permite recalibrar si el cliente camina
                    )
                ) {
                    // conservar tus marcadores de huariques:
                    huariquesRadar.forEach { huarique ->
                        Marker(
                            state = rememberMarkerState(
                                position = LatLng(huarique.latitud, huarique.longitud)
                            ),
                            title = huarique.nombre,
                            snippet = "${huarique.category} • ${huarique.distancia}"
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Carrusel Inferior de Huariques
        HuariquesRadarCarousel(
            huariquesRadar = huariquesRadar,
            cameraPositionState = cameraPositionState,
            searchViewModel = searchViewModel,
            onOpenCart = { huarique ->
                selectedHuariqueForCart = huarique
                cantidadSeleccionada = 1
                showCartSheet = true
            }
        )
    }


    BuyerSearchSheets(
        showCartSheet = showCartSheet,
        onDismissCart = { showCartSheet = false },
        selectedHuariqueForCart = selectedHuariqueForCart,
        cantidadSeleccionada = cantidadSeleccionada,
        onCantidadChange = { cantidadSeleccionada = it },
        orderViewModel = orderViewModel,
        showProfileSheet = showProfileSheet,
        onDismissProfile = { showProfileSheet = false },
        profileCurrentSection = profileCurrentSection,
        onSectionChange = { profileCurrentSection = it },
        searchViewModel = searchViewModel,
        onLogout = onLogout
    )
}
