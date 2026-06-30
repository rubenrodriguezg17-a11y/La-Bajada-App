package com.labajada.app.presentation.restaurant.register.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterLocationStep(
    addressDetails: String,
    isLocationSelected: Boolean,
    offersDelivery: Boolean,
    maxDeliveryDistanceKm: Double,
    imageUrl: String?,
    onAddressChange: (String) -> Unit,
    onOpenMapDialog: () -> Unit,
    onImageSelected: (String?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Ubicación y delivery",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF263238)
        )
        Text(
            text = "Define dónde estás y hasta dónde llega tu reparto.",
            fontSize = 13.sp,
            color = Color.Gray
        )

        OutlinedTextField(
            value = addressDetails,
            onValueChange = onAddressChange,
            label = { Text("Dirección de Local") },
            placeholder = { Text("Ej: Av. Larco 123, a media cuadra del óvalo") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Box(modifier = Modifier.fillMaxWidth().clickable { onOpenMapDialog() }) {
            OutlinedTextField(
                value = when {
                    !isLocationSelected -> "Seleccionar Ubicación en Mapa"
                    offersDelivery -> "Ubicación confirmada • Delivery hasta ${maxDeliveryDistanceKm.toInt()} km ✓"
                    else -> "Ubicación confirmada • Solo recojo en local ✓"
                },
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Georreferenciación") },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD32F2F))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = if (isLocationSelected) Color(0xFF4CAF50) else Color(0xFF263238),
                    disabledLabelColor = Color(0xFF263238),
                    disabledTextColor = if (isLocationSelected) Color(0xFF4CAF50) else Color(0xFF212121)
                )
            )
        }

        RestaurantImagePicker(
            imageUrl = imageUrl,
            onImageSelected = onImageSelected
        )
    }
}