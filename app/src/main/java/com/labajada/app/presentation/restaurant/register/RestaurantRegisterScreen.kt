package com.labajada.app.presentation.restaurant.register

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.core.validation.PasswordValidator
import com.labajada.app.core.validation.PeruValidators
import com.labajada.app.core.validation.PasswordRuleRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantRegisterScreen(
    viewModel: RestaurantRegisterViewModel,
    onRegistrationComplete: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val rubrosGastronomicos = listOf("Menú clásico", "Cevichería", "Criollo", "Fast Food / Bajadas", "Pollería", "Chifa")

    val isDocumentoValid = remember(state.rucNumber) {
        state.rucNumber.isEmpty() || PeruValidators.isValidDocumento(state.rucNumber)
    }
    val isPhoneValid = remember(state.phoneNumber) {
        state.phoneNumber.isEmpty() || PeruValidators.isValidPhone(state.phoneNumber)
    }
    val isEmailValid = remember(state.email) {
        state.email.isEmpty() || PasswordValidator.isValidEmail(state.email)
    }
    val passwordCheck = remember(state.password) {
        PasswordValidator.validate(state.password)
    }
    val showPasswordChecklist = state.password.isNotEmpty()

    val isFormReadyToSubmit = state.restaurantName.isNotBlank() &&
            PeruValidators.isValidDocumento(state.rucNumber) &&
            PeruValidators.isValidPhone(state.phoneNumber) &&
            state.selectedCategory.isNotBlank() &&
            state.addressDetails.isNotBlank() &&
            PasswordValidator.isValidEmail(state.email) &&
            passwordCheck.isValid &&
            state.isLocationSelected &&
            !state.isLoading

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
            onValueChange = {
                if (it.length <= 11 && it.all(Char::isDigit)) {
                    viewModel.onRucChange(it)
                }
            },
            label = {
                Text(
                    text = when (state.rucNumber.length) {
                        in 0..8 -> "DNI o RUC del Titular"
                        else -> "Número de RUC"
                    }
                )
            },
            placeholder = { Text("Ej: 8 dígitos para DNI o 11 para RUC") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = !isDocumentoValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (!isDocumentoValid) {
        Text(
            text = "Documento inválido. Debe ser DNI (8 dígitos) o RUC (11 dígitos).",
            color = Color.Red,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
    }


        OutlinedTextField(
            value = state.phoneNumber,
            onValueChange = {
                if (it.length <= 9 && it.all(Char::isDigit)) {
                    viewModel.onPhoneChange(it)
                }
            },
            label = { Text("Teléfono Celular") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = !isPhoneValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        if (!isPhoneValid) {
            Text(
                text = "Celular inválido (9 dígitos, empieza con 9)",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

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
            singleLine = true,
            isError = !isEmailValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        if (!isEmailValid) {
            Text(
                text = "Ingresa un formato de correo válido",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Contraseña de Acceso") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = showPasswordChecklist && !passwordCheck.isValid,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        if (showPasswordChecklist) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                PasswordRuleRow("Mínimo 8 caracteres", passwordCheck.hasMinLength)
                PasswordRuleRow("Una letra mayúscula", passwordCheck.hasUppercase)
                PasswordRuleRow("Un número", passwordCheck.hasNumber)
                PasswordRuleRow("Un carácter especial (!@#\$...)", passwordCheck.hasSpecialChar)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { viewModel.registerRestaurant(onRegistrationComplete) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF263238),
                disabledContainerColor = Color(0xFFCFD8DC)
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = isFormReadyToSubmit
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