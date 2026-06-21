package com.labajada.app.presentation.buyer.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
    viewModel: BuyerSearchViewModel,
    onSectionChange: (String) -> Unit,
    onDismissProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val userName by viewModel.currentBuyerName.collectAsState()
    val userEmail by viewModel.currentBuyerEmail.collectAsState()

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
            text = "¡Hola, $userName!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF212121)
        )
        Text(
            text = userEmail,
            fontSize = 13.sp,
            color = Color(0xFF757575)
        )

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(8.dp))

        // Opción: Historial
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
            Text("Historial de busqueda", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121))
        }

        // Opción: Favoritos
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
            Text("Restaurantes favoritos", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121))
        }

        // Opción: Editar Perfil
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