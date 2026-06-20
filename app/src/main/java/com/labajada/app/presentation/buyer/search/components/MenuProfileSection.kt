package com.labajada.app.presentation.buyer.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel

@Composable
fun MenuProfileSection(
    onSectionChange: (String) -> Unit,
    onDismissProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFFFFEB3B), shape = RoundedCornerShape(36.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(38.dp),
                tint = Color(0xFF212121)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "¡Hola, Bitoquero!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF212121)
        )
        Text(text = "cliente.bajada@email.com", fontSize = 13.sp, color = Color(0xFF757575))

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onSectionChange("HISTORIAL") }
                .padding(vertical = 14.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF616161))
            Spacer(modifier = Modifier.width(14.dp))
            Text("Historial de antojos", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onSectionChange("FAVORITOS") }
                .padding(vertical = 14.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null, tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.width(14.dp))
            Text("Mis platos favoritos", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onSectionChange("EDITAR") }
                .padding(vertical = 14.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = Color(0xFF616161))
            Spacer(modifier = Modifier.width(14.dp))
            Text("Editar perfil", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onDismissProfile()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FavoritosProfileSection(
    viewModel: BuyerSearchViewModel,
    onSectionChange: (String) -> Unit
) {
    val favoritosReal by viewModel.platosFavoritosRoom.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onSectionChange("MENU") }
                .padding(bottom = 16.dp)
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
        }

        Text(
            text = "Mis Platos Favoritos",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (favoritosReal.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no has guardado platos favoritos.", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // ◄ CORREGIDO: Manejo de clave segura usando una propiedad garantizada o string
                items(favoritosReal, key = { it.name + it.price }) { plato ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = plato.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                                Text(text = plato.price, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                            }
                            IconButton(onClick = { viewModel.quitarPlatoDeFavoritos(plato.name) }) { // Ajustado según tu mapper
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun HistorialProfileSection(
    viewModel: BuyerSearchViewModel,
    onSectionChange: (String) -> Unit
) {
    val historialReal by viewModel.searchHistory.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onSectionChange("MENU") }
                .padding(bottom = 16.dp)
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Historial de antojos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF212121)
            )
            if (historialReal.isNotEmpty()) {
                Text(
                    text = "Limpiar todo",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.clickable {
                        viewModel.borrarTodoElHistorial()
                        onSectionChange("MENU") // ◄ CORREGIDO: Redirige para evitar contenedores vacíos con botones huérfanos
                    }
                )
            }
        }

        if (historialReal.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no tienes antojos registrados.", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // ◄ CORREGIDO: Clave calculada dinámicamente mediante el texto buscado para asegurar consistencia
                items(historialReal, key = { it.searchQuery }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Búsqueda reciente",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.searchQuery,
                                fontSize = 15.sp,
                                color = Color(0xFF212121),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditarProfileSection(onSectionChange: (String) -> Unit) {
    var editNombre by remember { mutableStateOf("Ruben") }
    var editApellido by remember { mutableStateOf("Cib") }
    var editDepartamento by remember { mutableStateOf("La Libertad") }
    var editProvincia by remember { mutableStateOf("Trujillo") }
    var editTelefono by remember { mutableStateOf("987654321") }

    val scrollForm = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 450.dp)
            .verticalScroll(scrollForm)
    ) {
        Row(
            modifier = Modifier
                .clickable { onSectionChange("MENU") }
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
        }

        Text(
            text = "Información Personal",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = editNombre,
                onValueChange = { editNombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = editApellido,
                onValueChange = { editApellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = editDepartamento,
                onValueChange = { editDepartamento = it },
                label = { Text("Departamento") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = editProvincia,
                onValueChange = { editProvincia = it },
                label = { Text("Provincia") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = editTelefono,
                onValueChange = { editTelefono = it },
                label = { Text("Número de Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onSectionChange("MENU") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Guardar Cambios", fontWeight = FontWeight.Bold)
            }
        }
    }
}
