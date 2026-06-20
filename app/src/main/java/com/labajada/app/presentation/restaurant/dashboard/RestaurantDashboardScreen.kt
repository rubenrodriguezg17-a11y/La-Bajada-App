package com.labajada.app.presentation.restaurant.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.restaurant.dashboard.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDashboardScreen(
    viewModel: RestaurantDashboardViewModel, // ◄ 1. RECIBE EL VIEWMODEL EXTERNO (Soluciona el error)
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 2. OBSERVAMOS EL UI STATE ÚNICO Y LOS REPOSITORIOS REACTIVOS
    val uiState by viewModel.uiState.collectAsState()
    val platillosDelDia by viewModel.platillosDelDia.collectAsState()
    val pedidosActivosList by viewModel.pedidosActivos.collectAsState()
    val gananciasHoyCalculadas by viewModel.gananciasHoy.collectAsState() // Ahora es un StateFlow reactivo

    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        floatingActionButton = {
            // Evaluamos la pestaña usando el estado centralizado
            if (uiState.selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.prepararNuevoPlatillo()
                        viewModel.toggleFormSheet(true)
                    },
                    containerColor = Color(0xFF263238),
                    contentColor = Color.White,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nuevo Platillo", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Mi Cocina Dashboard",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF263238),
                modifier = Modifier.padding(top = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Pedidos en Cola",
                    value = "${pedidosActivosList.size}",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Ganancias de Hoy",
                    value = "S/. ${String.format("%.2f", gananciasHoyCalculadas)}",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF263238)
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = {
                        viewModel.onTabSelected(0)
                        if (uiState.isEditingProfile) viewModel.toggleProfileEdit()
                    },
                    text = { Text("Menú del Día", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = {
                        viewModel.onTabSelected(1)
                        if (uiState.isEditingProfile) viewModel.toggleProfileEdit()
                    },
                    text = { Text("Pedidos (${pedidosActivosList.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.onTabSelected(2) },
                    text = { Text("Mi Local", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Renderizado de las pestañas modulares según el estado inmutable
            when (uiState.selectedTab) {
                0 -> MenuTabContent(viewModel = viewModel, platillosDelDia = platillosDelDia)
                1 -> OrdersTabContent(viewModel = viewModel, pedidosActivosList = pedidosActivosList)
                2 -> ProfileTabContent(viewModel = viewModel, onLogout = onLogout)
            }
        }
    }

    DashboardModals(
        viewModel = viewModel
    )
}
