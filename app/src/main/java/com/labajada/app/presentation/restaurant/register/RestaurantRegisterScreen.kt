package com.labajada.app.presentation.restaurant.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantRegisterScreen(
    onRegistrationComplete: () -> Unit
) {
    // --- ESTADOS COMPLETOS DEL HUARIQUE (Sincronizados con el Dashboard) ---
    var restaurantName by remember { mutableStateOf("") }
    var rucNumber by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var addressDetails by remember { mutableStateOf("") }

    // Credenciales para el futuro Login del Restaurante
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estado del selector de categoría (Menú Desplegable)
    var expandedCategory by remember { mutableStateOf(false) }
    val categoriasVisibles = listOf("🐟 Cevichería", "🍲 Criollo", "🍔 Fast Food / Bajadas", "🔥 Pollería", "🇨🇳 Chifa")
    var selectedCategory by remember { mutableStateOf("") }

    // Estados de Geolocalización para Room (Fijos en Trujillo por defecto)
    var latitude by remember { mutableDoubleStateOf(-8.1116) }
    var longitude by remember { mutableDoubleStateOf(-79.0287) }
    var isLocationSelected by remember { mutableStateOf(false) }
    var showMapDialog by remember { mutableStateOf(false) }

    // Scroll obligatorio para evitar bloqueos con el teclado
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Encabezado con estilo Gris Industrial / Oscuro para el rol negocio
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Registra tu Huarique", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF263238))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Configura tu cocina para empezar a recibir comensales.", fontSize = 14.sp, color = Color(0xFF757575))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bloque del Formulario Técnico
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = restaurantName, onValueChange = { restaurantName = it },
                label = { Text("Nombre del Restaurante") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF263238), focusedLabelColor = Color(0xFF263238))
            )

            OutlinedTextField(
                value = rucNumber, onValueChange = { if (it.length <= 11) rucNumber = it },
                label = { Text("RUC o DNI del Titular") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF263238), focusedLabelColor = Color(0xFF263238))
            )

            OutlinedTextField(
                value = phoneNumber, onValueChange = { phoneNumber = it },
                label = { Text("Teléfono / WhatsApp de Contacto") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF263238), focusedLabelColor = Color(0xFF263238))
            )

            // Selector Desplegable de Rubro
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                OutlinedTextField(
                    value = selectedCategory, onValueChange = {}, readOnly = true,
                    label = { Text("Tipo de comida / Rubro") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF263238), focusedLabelColor = Color(0xFF263238))
                )
                ExposedDropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }) {
                    categoriasVisibles.forEach { item ->
                        DropdownMenuItem(text = { Text(item) }, onClick = { selectedCategory = item; expandedCategory = false })
                    }
                }
            }
            // Selector Satelital Inmune a congelamientos de foco 📍
            Box(modifier = Modifier.fillMaxWidth().clickable { showMapDialog = true }) {
                OutlinedTextField(
                    value = if (isLocationSelected) "Ubicación fijada en el mapa" else "Toca para ubicar en el mapa",
                    onValueChange = {}, readOnly = true, enabled = false,
                    label = { Text("Ubicación Satelital") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = if (isLocationSelected) Color(0xFFD32F2F) else Color.Gray) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = if (isLocationSelected) Color(0xFF263238) else Color(0xFFCCCCCC),
                        disabledLabelColor = Color(0xFF263238), disabledTextColor = if (isLocationSelected) Color(0xFF212121) else Color.Gray
                    )
                )
            }

            OutlinedTextField(
                value = addressDetails, onValueChange = { addressDetails = it },
                label = { Text("Dirección escrita (Ej: Av. Larco 123 u Oficina)") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF263238), focusedLabelColor = Color(0xFF263238))
            )

            // 📧 Credenciales de acceso para el dueño del Local
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Correo de la Empresa (Gmail)") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF263238), focusedLabelColor = Color(0xFF263238))
            )

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Contraseña de Acceso") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF263238), focusedLabelColor = Color(0xFF263238))
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de Acción Final de color Gris Industrial Oscuro
        Button(
            onClick = onRegistrationComplete,
            // Bloquea el envío hasta que las 7 casillas de negocio y mapa estén completas 🔒
            enabled = restaurantName.isNotBlank() && rucNumber.isNotBlank() &&
                    phoneNumber.isNotBlank() && selectedCategory.isNotBlank() &&
                    addressDetails.isNotBlank() && email.isNotBlank() &&
                    password.isNotBlank() && isLocationSelected,
            modifier = Modifier.fillMaxWidth().height(58.dp).padding(bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238), disabledContainerColor = Color(0xFFE0E0E0)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Finalizar y Abrir Cocina", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }

    // Modal de Google Maps extractor de coordenadas decimales
    if (showMapDialog) {
        Dialog(onDismissRequest = { showMapDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Card(
                modifier = Modifier.fillMaxWidth(0.92f).fillMaxHeight(0.75f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Arrastra el marcador hasta tu local", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
                    }

                    val puntoInicial = remember { LatLng(latitude, longitude) }
                    val markerState = rememberMarkerState(position = puntoInicial)
                    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(puntoInicial, 15f) }

                    Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState, onMapClick = { latLng -> markerState.position = latLng }) {
                            Marker(state = markerState, title = "Mi Huarique", draggable = true)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = { showMapDialog = false }, modifier = Modifier.weight(1f)) { Text("Cancelar", color = Color.Gray) }
                        Button(
                            onClick = {
                                // Extrae las coordenadas exactas para tus columnas Double de Room 💾
                                latitude = markerState.position.latitude
                                longitude = markerState.position.longitude
                                isLocationSelected = true
                                showMapDialog = false
                            },
                            modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)), shape = RoundedCornerShape(8.dp)
                        ) { Text("Confirmar", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
}
