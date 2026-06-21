package com.labajada.app.presentation.restaurant.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTabContent(
    viewModel: RestaurantDashboardViewModel,
    onLogout: () -> Unit
) {
    val scrollProfileState = rememberScrollState()
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollProfileState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- MODO LECTURA ---
        if (!state.isEditingProfile) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Información Comercial",
                        fontSize = 18.sp,
                        color = Color(0xFF263238),
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    Text("Huarique: ${state.resNameByOwner.ifBlank { "No registrado" }}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("RUC: ${state.resRucByOwner.ifBlank { "No registrado" }}", fontSize = 14.sp, color = Color.Gray)
                    Text("Contacto: ${state.resPhoneByOwner.ifBlank { "No registrado" }}", fontSize = 14.sp, color = Color.Gray)
                    Text("Rubro: ${state.resCategoryByOwner.ifBlank { "No registrado" }}", fontSize = 14.sp, color = Color.Gray)
                    Text("Dirección: ${state.resAddressByOwner.ifBlank { "No registrado" }}", fontSize = 14.sp, color = Color.Gray)

                    Text(
                        text = "Satélite: Lat: ${String.format(Locale.US, "%.4f", state.resLatitude)} | Lon: ${String.format(Locale.US, "%.4f", state.resLongitude)}",
                        fontSize = 13.sp,
                        color = Color(0xFF1976D2)
                    )
                }
            }

            Button(
                onClick = { viewModel.toggleProfileEdit() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar Datos del Local", fontWeight = FontWeight.Bold)
            }
        }
        // --- MODO EDICIÓN ---
        else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { viewModel.toggleProfileEdit() }
                    .padding(vertical = 4.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF263238))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cancelar Edición", color = Color(0xFF263238), fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = state.resNameByOwner,
                onValueChange = { viewModel.onProfileNameChange(it) },
                label = { Text("Nombre del Restaurante") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = state.resRucByOwner,
                onValueChange = { if (it.length <= 11) viewModel.onProfileRucChange(it) },
                label = { Text("RUC o DNI") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = state.resPhoneByOwner,
                onValueChange = { viewModel.onProfilePhoneChange(it) },
                label = { Text("Teléfono de Contacto") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = state.expandedProfileCategory,
                onExpandedChange = { viewModel.toggleProfileCategoryDropdown() }
            ) {
                OutlinedTextField(
                    value = state.resCategoryByOwner,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rubro") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = state.expandedProfileCategory,
                    onDismissRequest = { viewModel.toggleProfileCategoryDropdown() }
                ) {
                    viewModel.categoriesDisponibles.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                viewModel.onProfileCategorySelected(item)
                            }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleProfileMap(true) }
            ) {
                OutlinedTextField(
                    value = "Ubicación Georreferenciada",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Ubicación Satelital") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD32F2F)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color(0xFF263238),
                        disabledLabelColor = Color(0xFF263238),
                        disabledTextColor = Color(0xFF212121)
                    )
                )
            }

            OutlinedTextField(
                value = state.resAddressByOwner,
                onValueChange = { viewModel.onProfileAddressChange(it) },
                label = { Text("Dirección Escrita") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Button(
                onClick = { viewModel.guardarDatosDelLocal() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000000)),
                shape = RoundedCornerShape(12.dp),
                enabled = state.resNameByOwner.isNotBlank() && state.resRucByOwner.isNotBlank()
                        && state.resPhoneByOwner.isNotBlank() && state.resAddressByOwner.isNotBlank()
            ) {
                Text("Guardar Cambios", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(52.dp).padding(bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cerrar Sesión del Local", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        }
    }
}