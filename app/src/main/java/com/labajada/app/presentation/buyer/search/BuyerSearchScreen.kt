package com.labajada.app.presentation.buyer.search

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.labajada.app.presentation.order.OrderViewModel
import com.labajada.app.presentation.buyer.search.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerSearchScreen(
    searchViewModel: BuyerSearchViewModel,
    orderViewModel: OrderViewModel,
    onNavigateToMap: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val platosEncontrados by searchViewModel.platosEncontrados.collectAsState(initial = emptyList())

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Trigger que se incrementa cuando el usuario acepta encender el GPS desde el diálogo nativo
    var locationTrigger by remember { mutableStateOf(0) }

    // Indicador de carga mientras se obtiene el fix de ubicación
    var isLoadingLocation by remember { mutableStateOf(false) }

    val gpsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // El usuario aceptó encender el GPS -> reintentamos obtener ubicación
            locationTrigger++
        } else {
            // El usuario rechazó encender el GPS
            isLoadingLocation = false
        }
    }

    var showProfileSheet by remember { mutableStateOf(false) }
    var showCartSheet by remember { mutableStateOf(false) }
    var profileCurrentSection by remember { mutableStateOf("MENU") }

    var selectedHuariqueForCart by remember { mutableStateOf<RadarHuarique?>(null) }

    var selectedDishForCart by remember { mutableStateOf<com.labajada.app.domain.model.Dish?>(null) }
    var showMenuSheet by remember { mutableStateOf(false) }
    var huariqueParaMenu by remember { mutableStateOf<RadarHuarique?>(null) }
    val menuDelHuarique by searchViewModel.menuDelHuariqueSeleccionado.collectAsState()

    var cantidadSeleccionada by remember { mutableIntStateOf(1) }

    val huariquesRadar by searchViewModel.huariquesDesdeBaseDeDatos.collectAsState()
    val ubicacionClienteInicial = remember { LatLng(-8.1116, -79.0287) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ubicacionClienteInicial, 17f)
    }
    val ultimasBusquedas by searchViewModel.searchHistory.collectAsState(initial = emptyList())
    val queryText by searchViewModel.searchQuery.collectAsState(initial = "")

    // Disparador que gestiona permisos, exige el GPS encendido y mueve la cámara a la ubicación actual
    LaunchedEffect(hasLocationPermission, locationTrigger) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            isLoadingLocation = true
            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 5000
            ).build()
            val builder = com.google.android.gms.location.LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val client: com.google.android.gms.location.SettingsClient =
                com.google.android.gms.location.LocationServices.getSettingsClient(context)

            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                // El GPS está encendido y tenemos permisos. Procedemos a rastrear la ubicación real:
                try {
                    searchViewModel.rastrearUbicacionActual(context)
                    val fusedLocationClient = com.google.android.gms.location.LocationServices
                        .getFusedLocationProviderClient(context)

                    @android.annotation.SuppressLint("MissingPermission")
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val currentLatLng = LatLng(location.latitude, location.longitude)
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(currentLatLng, 16.5f)
                            isLoadingLocation = false
                        } else {
                            // Respaldo si lastLocation devuelve null (caché vacía):
                            // pedimos un fix nuevo en tiempo real
                            @android.annotation.SuppressLint("MissingPermission")
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                object : com.google.android.gms.location.LocationCallback() {
                                    override fun onLocationResult(
                                        locationResult: com.google.android.gms.location.LocationResult
                                    ) {
                                        val lastLoc = locationResult.lastLocation
                                        if (lastLoc != null) {
                                            val fallbackLatLng =
                                                LatLng(lastLoc.latitude, lastLoc.longitude)
                                            cameraPositionState.position =
                                                CameraPosition.fromLatLngZoom(fallbackLatLng, 16.5f)
                                            isLoadingLocation = false
                                            fusedLocationClient.removeLocationUpdates(this)
                                        }
                                    }
                                },
                                context.mainLooper
                            )
                        }
                    }.addOnFailureListener {
                        isLoadingLocation = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    isLoadingLocation = false
                }
            }

            task.addOnFailureListener { exception ->
                // El GPS está apagado. Mostramos el cuadro de diálogo nativo de Android para activarlo
                if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                    try {
                        val intentSenderRequest = androidx.activity.result.IntentSenderRequest
                            .Builder(exception.resolution.intentSender)
                            .build()
                        gpsLauncher.launch(intentSenderRequest)
                    } catch (sendEx: android.content.IntentSender.SendIntentException) {
                        sendEx.printStackTrace()
                        isLoadingLocation = false
                    }
                } else {
                    isLoadingLocation = false
                }
            }
        }
    }

    // DISEÑO INTERFAZ DE USUARIO
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .padding(16.dp)
        ) {
            // Encabezado
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
                    modifier = Modifier
                        .background(Color(0xFFFFEB3B), shape = RoundedCornerShape(50.dp))
                        .size(44.dp)
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Perfil", tint = Color(0xFF212121))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Buscador
            OutlinedTextField(
                value = queryText,
                onValueChange = { searchViewModel.onSearchQueryChange(it) },
                placeholder = { Text("¿Qué deseas comer hoy?") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.clickable { searchViewModel.ejecutarBusquedaInteligente() }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Search
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSearch = { searchViewModel.ejecutarBusquedaInteligente() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD32F2F),
                    focusedLabelColor = Color(0xFFD32F2F),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (ultimasBusquedas.isEmpty()) {
                    item {
                        Text(
                            text = "Aún no tienes búsquedas recientes",
                            fontSize = 13.sp,
                            color = Color(0xFF9E9E9E),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                } else {
                    items(ultimasBusquedas, key = { it.id }) { historialItem ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                searchViewModel.onSearchQueryChange(historialItem.searchQuery)
                                searchViewModel.ejecutarBusquedaInteligente()
                            },
                            label = {
                                Text(
                                    historialItem.searchQuery,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFFEEEEEE),
                                labelColor = Color(0xFF616161)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Huariques en tu radar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121), modifier = Modifier.padding(bottom = 10.dp))

            // Contenedor Mapa Google Maps
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = hasLocationPermission,
                            rotationGesturesEnabled = false,
                            tiltGesturesEnabled = false,
                            scrollGesturesEnabled = true,
                            zoomGesturesEnabled = true
                        )
                    ) {
                        huariquesRadar.forEach { huarique ->
                            Marker(
                                state = rememberMarkerState(position = LatLng(huarique.latitud, huarique.longitud)),
                                title = huarique.nombre,
                                snippet = "${huarique.category} • ${huarique.distancia}"
                            )
                        }
                    }

                    // Overlay de carga mientras se obtiene la ubicación actual
                    if (isLoadingLocation) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFFD32F2F)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Un momento, te estamos buscando...",
                                        fontSize = 13.sp,
                                        color = Color(0xFF212121)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            HuariquesRadarCarousel(
                huariquesRadar = huariquesRadar,
                cameraPositionState = cameraPositionState,
                searchViewModel = searchViewModel,
                onVerMenu = { huarique ->
                    huariqueParaMenu = huarique
                    searchViewModel.abrirMenuDeHuarique(huarique.id)
                    showMenuSheet = true
                }
            )
        }
        // Una sola lista de búsqueda superpuesta condicionalmente
        if (queryText.isNotEmpty() && platosEncontrados.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 155.dp) // Ajusta para dejar visible el buscador superior
                    .background(Color.White)
            ) {
                SearchResultsList(
                    platosEncontrados = platosEncontrados,
                    onSelectDish = { huariquePuente, dish ->
                        selectedHuariqueForCart = huariquePuente
                        selectedDishForCart = dish
                        cantidadSeleccionada = 1
                        showCartSheet = true
                    }
                )
            }
        }
    }
    // sheet de menú
    if (showMenuSheet && huariqueParaMenu != null) {
        RestaurantMenuSheet(
            huarique = huariqueParaMenu!!,
            menu = menuDelHuarique,
            onDismiss = {
                showMenuSheet = false
                searchViewModel.cerrarMenuDeHuarique()
            },
            onDishSelected = { dish ->
                selectedHuariqueForCart = huariqueParaMenu
                selectedDishForCart = dish
                cantidadSeleccionada = 1
                showMenuSheet = false
                searchViewModel.cerrarMenuDeHuarique()
                showCartSheet = true
            }
        )
    }
    BuyerSearchSheets(
        showCartSheet = showCartSheet,
        onDismissCart = { showCartSheet = false },
        selectedHuariqueForCart = selectedHuariqueForCart,
        selectedDish = selectedDishForCart,
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