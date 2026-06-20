package com.labajada.app.presentation.restaurant.register.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun RestaurantRegisterMapDialog(
    initialLatitude: Double,
    initialLongitude: Double,
    onDismiss: () -> Unit,
    onConfirmLocation: (Double, Double) -> Unit
) {
    // Dialog de pantalla completa adaptable
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.80f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Cabecera informativa del modal
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Mantén presionado el marcador para moverlo 📍",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )
                }

                // Estados internos del mapa de Google
                val puntoInicial = remember { LatLng(initialLatitude, initialLongitude) }
                val markerState = rememberMarkerState(position = puntoInicial)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(puntoInicial, 15f) // Zoom a nivel de calles
                }

                // Contenedor del Mapa Satelital
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { latLng ->
                            // Permite cambiar el marcador haciendo un toque simple en cualquier lado
                            markerState.position = latLng
                        }
                    ) {
                        Marker(
                            state = markerState,
                            title = "Ubicación de mi Huarique",
                            snippet = "Arrástrame hasta tu puerta",
                            draggable = true // ◄ Activa el arrastre con el dedo
                        )
                    }
                }

                // Fila inferior de acciones (Botones)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar", color = Color(0xFF757575), fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = {
                            // Devuelve los decimales exactos donde el usuario soltó el pin rojo
                            onConfirmLocation(
                                markerState.position.latitude,
                                markerState.position.longitude
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirmar", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
