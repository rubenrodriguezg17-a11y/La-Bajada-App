package com.labajada.app.presentation.restaurant.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantRegisterScreen(
    viewModel: RestaurantRegisterViewModel,
    onRegistrationComplete: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Lista de categorías estática alineada con el negocio
    val rubrosGastronomicos = listOf("Menú clásico", "Cevichería", "Criollo", "Fast Food / Bajadas", "Pollería", "Chifa")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Registra tu Huarique",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF263238),
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Únete a La Bajada y gestiona tus pedidos al toque",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.restaurantName,
            onValueChange = { viewModel.onNameChange(it) },
            label = { Text("Nombre del Restaurante") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = state.rucNumber,
            onValueChange = { if (it.length <= 11) viewModel.onRucChange(it) },
            label = { Text("Número de RUC") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = state.phoneNumber,
            onValueChange = { viewModel.onPhoneChange(it) },
            label = { Text("Teléfono Celular") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        // --- Selector Desplegable de Rubro Comercial ---
        ExposedDropdownMenuBox(
            expanded = state.expandedCategory,
            onExpandedChange = { viewModel.toggleCategoryDropdown() }
        ) {
            OutlinedTextField(
                value = state.selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Rubro Gastronómico") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = state.expandedCategory,
                onDismissRequest = { viewModel.toggleCategoryDropdown() }
            ) {
                rubrosGastronomicos.forEach { rubro: String ->
                    DropdownMenuItem(
                        text = { Text(rubro) },
                        onClick = { viewModel.onCategorySelected(rubro) }
                    )
                }
            }
        }

        // --- Cuadro de Ubicación Satelital ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.toggleMapDialog(true) }
        ) {
            OutlinedTextField(
                value = if (state.isLocationSelected) "Ubicación Confirmada ✓" else "Seleccionar Ubicación en Mapa",
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Georreferenciación") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD32F2F)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = if (state.isLocationSelected) Color(0xFF4CAF50) else Color(0xFF263238),
                    disabledLabelColor = Color(0xFF263238),
                    disabledTextColor = if (state.isLocationSelected) Color(0xFF4CAF50) else Color(0xFF212121)
                )
            )
        }

        OutlinedTextField(
            value = state.addressDetails,
            onValueChange = { viewModel.onAddressChange(it) },
            label = { Text("Dirección de Local (Ej: Av. Larco 123)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Contraseña de Acceso") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // --- Botón de Registro Final ---
        Button(
            onClick = { viewModel.registerRestaurant(onRegistrationComplete) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
            shape = RoundedCornerShape(12.dp),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Registrar Comercio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        if (state.error != null) {
            Text(
                text = state.error ?: "",
                color = Color(0xFFD32F2F),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    if (state.showMapDialog) {
        com.labajada.app.presentation.restaurant.dashboard.components.modals.RestaurantMapDialog(
            initialLatitude = if (state.latitude != 0.0) state.latitude else -8.1116,
            initialLongitude = if (state.longitude != 0.0) state.longitude else -79.0287,
            onConfirm = { lat, lng ->
                viewModel.onLocationConfirmed(lat, lng)
            },
            onDismiss = {
                viewModel.toggleMapDialog(false)
            }
        )
    }
}
