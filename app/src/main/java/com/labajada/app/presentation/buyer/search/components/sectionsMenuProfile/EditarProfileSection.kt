package com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditarProfileSection(onSectionChange: (String) -> Unit) {
    var editNombre by remember { mutableStateOf("Ruben") }
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
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color(0xFFD32F2F)
            )
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