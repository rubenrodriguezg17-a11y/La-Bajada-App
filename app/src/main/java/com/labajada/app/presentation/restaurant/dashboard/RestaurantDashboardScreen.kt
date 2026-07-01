package com.labajada.app.presentation.restaurant.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.labajada.app.presentation.restaurant.dashboard.components.DashboardHeader
import com.labajada.app.presentation.restaurant.dashboard.components.DashboardModals
import com.labajada.app.presentation.restaurant.dashboard.components.menu.MenuScreen
import com.labajada.app.presentation.restaurant.dashboard.components.pedidos.PedidosScreen
import com.labajada.app.presentation.restaurant.dashboard.components.perfil.PerfilScreen

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val index: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDashboardScreen(
    viewModel: RestaurantDashboardViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val platillosDelDia by viewModel.platillosDelDia.collectAsState()
    val pedidosActivosList by viewModel.pedidosActivos.collectAsState()
    val gananciasHoyCalculadas by viewModel.gananciasHoy.collectAsState()
    val session by viewModel.activeSession.collectAsState()
    val backupName by viewModel.fallbackRestaurantName.collectAsState()
    val nameRestaurant = session?.restaurantName ?: uiState.resNameByOwner.ifBlank { backupName }

    val bottomNavItems = listOf(
        BottomNavItem("Pedidos", Icons.Default.ShoppingCart, 0),
        BottomNavItem("Mi Menú", Icons.Default.Menu, 1),
        BottomNavItem("Perfil", Icons.Default.Person, 2)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = uiState.selectedTab == item.index,
                        onClick = { viewModel.onTabSelected(item.index) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (item.index == 0 && pedidosActivosList.isNotEmpty()) {
                                        Badge { Text("${pedidosActivosList.size}") }
                                    }
                                }
                            ) {
                                Icon(item.icon, contentDescription = item.label)
                            }
                        },
                        label = {
                            Text(item.label, fontWeight = FontWeight.Bold)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF263238),
                            selectedTextColor = Color(0xFF263238),
                            indicatorColor = Color(0xFFECEFF1),
                            unselectedIconColor = Color(0xFF9E9E9E),
                            unselectedTextColor = Color(0xFF9E9E9E)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (uiState.selectedTab == 1) {
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
            Spacer(modifier = Modifier.height(16.dp))

            DashboardHeader(
                restaurantName = nameRestaurant,
                pedidosEnCola = pedidosActivosList.size,
                gananciasHoy = gananciasHoyCalculadas,
                isGananciasVisible = uiState.isGananciasVisible,
                isOpen = uiState.resIsOpen,
                onToggleGanancias = { viewModel.toggleGananciasVisibility() },
                onToggleIsOpen = { viewModel.toggleIsOpen() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (uiState.selectedTab) {
                0 -> PedidosScreen(
                    viewModel = viewModel,
                    pedidosActivosList = pedidosActivosList
                )
                1 -> MenuScreen(
                    viewModel = viewModel,
                    platillosDelDia = platillosDelDia
                )
                2 -> PerfilScreen(
                    viewModel = viewModel,
                    onLogout = onLogout
                )
            }
        }
    }

    DashboardModals(viewModel = viewModel)
}