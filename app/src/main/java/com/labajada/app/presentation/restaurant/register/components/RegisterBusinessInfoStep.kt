package com.labajada.app.presentation.restaurant.register.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.core.validation.PeruValidators

private val rubrosGastronomicos = listOf(
    "Menú clásico", "Cevichería", "Criollo", "Fast Food / Bajadas", "Pollería", "Chifa"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterBusinessInfoStep(
    restaurantName: String,
    rucNumber: String,
    phoneNumber: String,
    selectedCategory: String,
    expandedCategory: Boolean,
    onNameChange: (String) -> Unit,
    onRucChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onToggleCategoryDropdown: () -> Unit
) {
    val isDocumentoValid = rucNumber.isEmpty() || PeruValidators.isValidDocumento(rucNumber)
    val isPhoneValid = phoneNumber.isEmpty() || PeruValidators.isValidPhone(phoneNumber)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Identidad de tu negocio",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF263238)
        )
        Text(
            text = "Así te verán los clientes en la búsqueda.",
            fontSize = 13.sp,
            color = Color.Gray
        )

        OutlinedTextField(
            value = restaurantName,
            onValueChange = onNameChange,
            label = { Text("Nombre del Restaurante") },
            placeholder = { Text("Ej: Pollería El Chasky") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = rucNumber,
            onValueChange = { if (it.length <= 11 && it.all(Char::isDigit)) onRucChange(it) },
            label = {
                Text(if (rucNumber.length in 0..8) "DNI o RUC del Titular" else "Número de RUC")
            },
            placeholder = { Text("Ingresa tu DNI o RUC") },
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
            value = phoneNumber,
            onValueChange = { if (it.length <= 9 && it.all(Char::isDigit)) onPhoneChange(it) },
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
            expanded = expandedCategory,
            onExpandedChange = { onToggleCategoryDropdown() }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Rubro Gastronómico") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { onToggleCategoryDropdown() }
            ) {
                rubrosGastronomicos.forEach { rubro ->
                    DropdownMenuItem(
                        text = { Text(rubro) },
                        onClick = { onCategorySelected(rubro) }
                    )
                }
            }
        }
    }
}