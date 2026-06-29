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
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val platosEncontrados by searchViewModel.platosEncontrados.collectAsState()
    val huariquesRadar by searchViewModel.huariquesDesdeBaseDeDatos.collectAsState()
    val ultimasBusquedas by searchViewModel.searchHistory.collectAsState()
    val queryText by searchViewModel.searchQuery.collectAsState()
    val menuDelHuarique by searchViewModel.menuDelHuariqueSeleccionado.collectAsState()

    var state by remember {
        mutableStateOf(
            BuyerSearchState(
                hasLocationPermission =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            )
        )
    }

    val ubicacionClienteInicial = remember { LatLng(-8.1116, -79.0287) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ubicacionClienteInicial, 17f)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        state = state.copy(
            hasLocationPermission =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        )
    }

    val gpsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            state = state.copy(locationTrigger = state.locationTrigger + 1)
        } else {
            state = state.copy(isLoadingLocation = false)
        }
    }

    LaunchedEffect(state.hasLocationPermission, state.locationTrigger) {
        if (!state.hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            state = state.copy(isLoadingLocation = true)
            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 5000
            ).build()
            val builder = com.google.android.gms.location.LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
            val client = com.google.android.gms.location.LocationServices.getSettingsClient(context)
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                try {
                    searchViewModel.rastrearUbicacionActual(context)
                    val fusedLocationClient = com.google.android.gms.location.LocationServices
                        .getFusedLocationProviderClient(context)

                    @android.annotation.SuppressLint("MissingPermission")
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(LatLng(location.latitude, location.longitude), 16.5f)
                            state = state.copy(isLoadingLocation = false)
                        } else {
                            @android.annotation.SuppressLint("MissingPermission")
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                object : com.google.android.gms.location.LocationCallback() {
                                    override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                                        val lastLoc = result.lastLocation
                                        if (lastLoc != null) {
                                            cameraPositionState.position =
                                                CameraPosition.fromLatLngZoom(LatLng(lastLoc.latitude, lastLoc.longitude), 16.5f)
                                            state = state.copy(isLoadingLocation = false)
                                            fusedLocationClient.removeLocationUpdates(this)
                                        }
                                    }
                                },
                                context.mainLooper
                            )
                        }
                    }.addOnFailureListener {
                        state = state.copy(isLoadingLocation = false)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    state = state.copy(isLoadingLocation = false)
                }
            }

            task.addOnFailureListener { exception ->
                if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                    try {
                        gpsLauncher.launch(
                            androidx.activity.result.IntentSenderRequest
                                .Builder(exception.resolution.intentSender)
                                .build()
                        )
                    } catch (e: android.content.IntentSender.SendIntentException) {
                        e.printStackTrace()
                        state = state.copy(isLoadingLocation = false)
                    }
                } else {
                    state = state.copy(isLoadingLocation = false)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "¡Encuentra tu bitoque!", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color(0xFF212121))
                    Text(text = "Huariques listos al toque en tu zona", fontSize = 14.sp, color = Color(0xFF757575))
                }
                IconButton(
                    onClick = { state = state.copy(profileCurrentSection = "MENU", showProfileSheet = true) },
                    modifier = Modifier
                        .background(Color(0xFFFFEB3B), shape = RoundedCornerShape(50.dp))
                        .size(44.dp)
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Perfil", tint = Color(0xFF212121))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

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
                    items(ultimasBusquedas, key = { it }) { historialItem ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                searchViewModel.onSearchQueryChange(historialItem)
                                searchViewModel.ejecutarBusquedaInteligente()
                            },
                            label = {
                                Text(historialItem, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                        properties = MapProperties(isMyLocationEnabled = state.hasLocationPermission),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = state.hasLocationPermission,
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

                    if (state.isLoadingLocation) {
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
                                    Text(text = "Un momento, te estamos buscando...", fontSize = 13.sp, color = Color(0xFF212121))
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
                    state = state.copy(
                        huariqueParaMenu = huarique,
                        showMenuSheet = true
                    )
                    searchViewModel.abrirMenuDeHuarique(huarique.id)
                }
            )
        }

        if (queryText.isNotEmpty() && platosEncontrados.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 155.dp)
                    .background(Color.White)
            ) {
                SearchResultsList(
                    platosEncontrados = platosEncontrados,
                    onSelectDish = { huariquePuente, dish ->
                        state = state.copy(
                            selectedHuariqueForCart = huariquePuente,
                            selectedDishForCart = dish,
                            cantidadSeleccionada = 1,
                            showCartSheet = true
                        )
                    }
                )
            }
        }
    }

    if (state.showMenuSheet && state.huariqueParaMenu != null) {
        RestaurantMenuSheet(
            huarique = state.huariqueParaMenu!!,
            menu = menuDelHuarique,
            onDismiss = {
                state = state.copy(showMenuSheet = false, huariqueParaMenu = null)
                searchViewModel.cerrarMenuDeHuarique()
            },
            onDishSelected = { dish ->
                state = state.copy(
                    selectedHuariqueForCart = state.huariqueParaMenu,
                    selectedDishForCart = dish,
                    cantidadSeleccionada = 1,
                    showMenuSheet = false,
                    huariqueParaMenu = null
                )
                searchViewModel.cerrarMenuDeHuarique()
                state = state.copy(showCartSheet = true)
            }
        )
    }

    BuyerSearchSheets(
        showCartSheet = state.showCartSheet,
        onDismissCart = { state = state.copy(showCartSheet = false) },
        selectedHuariqueForCart = state.selectedHuariqueForCart,
        selectedDish = state.selectedDishForCart,
        cantidadSeleccionada = state.cantidadSeleccionada,
        onCantidadChange = { state = state.copy(cantidadSeleccionada = it) },
        orderViewModel = orderViewModel,
        showProfileSheet = state.showProfileSheet,
        onDismissProfile = { state = state.copy(showProfileSheet = false) },
        profileCurrentSection = state.profileCurrentSection,
        onSectionChange = { state = state.copy(profileCurrentSection = it) },
        searchViewModel = searchViewModel,
        onLogout = onLogout
    )
}