package com.labajada.app.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(
    onBuyerSelected: () -> Unit,
    onRestaurantSelected: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bloque Superior: Identidad de Marca Tipográfica y Agresiva
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 72.dp)
        ) {
            Text(
                text = "La Bajada",
                fontSize = 54.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFD32F2F),
                letterSpacing = (-2).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .background(Color(0xFFFFEB3B), shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "LET'S GOO'",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF212121),
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "El mapa definitivo de los huariques y bajadas.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF757575),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¡Te damos la bienvenida a la comunidad gastronómica más picante! Descubre sabores auténticos a la vuelta de la esquina o pon a rugir los motores de tu negocio.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color(0xFF9E9E9E), // Un tono gris sutil para no saturar la vista
                modifier = Modifier.padding(horizontal = 12.dp),
                lineHeight = 18.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "¿Cuál es tu rol hoy?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF757575),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clickable { onBuyerSelected() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Quiero Buscar Comida ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Encuentra tu bajada al toque cerca de ti.",
                        fontSize = 13.sp,
                        color = Color(0xFFFFCDD2)
                    )
                }
            }

            // Tarjeta 2: Modo Negocio (Gris Industrial Sofisticado)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clickable { onRestaurantSelected() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Soy un Restaurante ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gestiona tu cocina y recibe comensales.",
                        fontSize = 13.sp,
                        color = Color(0xFFB0BEC5)
                    )
                }
            }
        }
    }
}
