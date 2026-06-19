package com.labajada.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.labajada.app.data.local.preferences.UserPreferencesRepositoryImpl
import com.labajada.app.domain.repository.UserPreferencesRepository
import com.labajada.app.presentation.buyer.map.BuyerMapScreen
import com.labajada.app.presentation.buyer.register.BuyerRegisterScreen
import com.labajada.app.presentation.buyer.search.BuyerSearchScreen
import com.labajada.app.presentation.onboarding.OnboardingScreen
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardScreen
import com.labajada.app.presentation.restaurant.register.RestaurantRegisterScreen
import com.labajada.app.presentation.login.LoginScreen // 💡 Importación agregada
import kotlinx.coroutines.launch

sealed interface NavUiState {
    object Loading : NavUiState
    data class Success(val role: String) : NavUiState
}

@Composable
fun LaBajadaNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val userPreferencesRepository: UserPreferencesRepository = remember {
        UserPreferencesRepositoryImpl(context)
    }

    // 2. Leemos el DataStore y transformamos el resultado en un estado seguro de UI
    val userRoleState by userPreferencesRepository.userRole.collectAsState(initial = "CARGANDO_INICIAL")

    val uiState = remember(userRoleState) {
        when (userRoleState) {
            "CARGANDO_INICIAL" -> NavUiState.Loading
            else -> NavUiState.Success(userRoleState ?: "")
        }
    }

    // 3. Renderizado según el estado de la arquitectura
    when (val state = uiState) {
        is NavUiState.Loading -> {
            // Pantalla de carga limpia mientras se lee el disco, dura milisegundos
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFD32F2F))
            }
        }
        is NavUiState.Success -> {
            // 💡 CALCULO DE DESTINO: Si no hay rol, arranca directamente en el Login
            val startDestination = remember {
                when (state.role) {
                    "BUYER" -> Screen.BuyerHome.route
                    "RESTAURANT" -> Screen.RestaurantHome.route
                    else -> Screen.Login.route
                }
            }
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                // --- 🔐 NUEVA RUTA INTEGRADA: INICIO DE SESIÓN ---
                composable(route = Screen.Login.route) {
                    LoginScreen(
                        onLoginSuccess = { rol ->
                            scope.launch {
                                // Guardamos el rol de forma asíncrona en el DataStore
                                userPreferencesRepository.saveUserRole(rol)
                                val destinoFinal = if (rol == "BUYER") Screen.BuyerHome.route else Screen.RestaurantHome.route

                                navController.navigate(destinoFinal) {
                                    popUpTo(Screen.Login.route) { inclusive = true } // Sacamos el login del historial
                                }
                            }
                        },
                        onNavigateToOnboarding = {
                            navController.navigate(Screen.Onboarding.route)
                        }
                    )
                }

                // --- FLUJO 1: BIENVENIDA ---
                composable(route = Screen.Onboarding.route) {
                    OnboardingScreen(
                        onBuyerSelected = { navController.navigate(Screen.RegisterBuyer.route) },
                        onRestaurantSelected = { navController.navigate(Screen.RegisterRestaurant.route) }
                    )
                }

                // --- FLUJO 2: RUTA COMPRADOR ---
                composable(route = Screen.RegisterBuyer.route) {
                    BuyerRegisterScreen(
                        onRegistrationComplete = {
                            scope.launch {
                                userPreferencesRepository.saveUserRole("BUYER")
                                navController.navigate(Screen.BuyerHome.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(route = Screen.BuyerHome.route) {
                    BuyerSearchScreen(
                        onNavigateToMap = {
                            navController.navigate("buyer_map_buyer")
                        },
                        onLogout = {
                            scope.launch {
                                userPreferencesRepository.saveUserRole("") // Limpiamos DataStore
                                navController.navigate(Screen.Login.route) { // Te devuelve al Login 💡
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                // Implementación del mapa interactivo
                composable(route = "buyer_map_buyer") {
                    BuyerMapScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                // --- FLUJO 3: RUTA RESTAURANTE ---
                composable(route = Screen.RegisterRestaurant.route) {
                    RestaurantRegisterScreen(
                        onRegistrationComplete = {
                            scope.launch {
                                userPreferencesRepository.saveUserRole("RESTAURANT")
                                navController.navigate(Screen.RestaurantHome.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(route = Screen.RestaurantHome.route) {
                    RestaurantDashboardScreen(
                        onLogout = {
                            scope.launch {
                                userPreferencesRepository.saveUserRole("") // Limpiamos DataStore
                                navController.navigate(Screen.Login.route) { // Te devuelve al Login 💡
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}