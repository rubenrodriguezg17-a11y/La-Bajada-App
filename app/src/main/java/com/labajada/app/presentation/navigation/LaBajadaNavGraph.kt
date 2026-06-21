package com.labajada.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.labajada.app.data.local.AppDatabase
import com.labajada.app.data.local.dao.AuthDao
import com.labajada.app.data.repository.AuthRepositoryImpl
import com.labajada.app.data.repository.LocalRestaurantRepositoryImpl
import com.labajada.app.data.repository.OrderRepositoryImpl
import com.labajada.app.domain.repository.AuthRepository
import com.labajada.app.domain.usecase.auth.LoginUseCase
import com.labajada.app.domain.usecase.auth.RegisterBuyerUseCase
import com.labajada.app.domain.usecase.auth.RegisterRestaurantUseCase
import com.labajada.app.presentation.buyer.map.BuyerMapScreen
import com.labajada.app.presentation.buyer.register.BuyerRegisterScreen
import com.labajada.app.presentation.buyer.register.BuyerRegisterViewModel
import com.labajada.app.presentation.buyer.search.BuyerSearchScreen
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel
import com.labajada.app.presentation.onboarding.OnboardingScreen
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardScreen
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.register.RestaurantRegisterScreen
import com.labajada.app.presentation.restaurant.register.RestaurantRegisterViewModel
import com.labajada.app.presentation.login.LoginScreen
import com.labajada.app.presentation.login.LoginViewModel
import com.labajada.app.presentation.order.OrderViewModel
import kotlinx.coroutines.launch

sealed interface NavUiState {
    object Loading : NavUiState
    data class Success(val role: String) : NavUiState
}

@Composable
fun LaBajadaNavGraph(
    authDao: AuthDao
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authRepository: AuthRepository = remember { AuthRepositoryImpl(authDao) }
    val loginUseCase = remember { LoginUseCase(authRepository) }
    val registerBuyerUseCase = remember { RegisterBuyerUseCase(authRepository) }
    val registerRestaurantUseCase = remember { RegisterRestaurantUseCase(authRepository) }

    val viewModelFactory = remember {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getDatabase(context)
                return when (modelClass.name) {
                    LoginViewModel::class.java.name -> {
                        LoginViewModel(loginUseCase) as T
                    }
                    BuyerRegisterViewModel::class.java.name -> {
                        BuyerRegisterViewModel(registerBuyerUseCase) as T
                    }
                    RestaurantRegisterViewModel::class.java.name -> {
                        RestaurantRegisterViewModel(registerRestaurantUseCase) as T
                    }
                    RestaurantDashboardViewModel::class.java.name -> {
                        val dishRepo = LocalRestaurantRepositoryImpl(db.dishDao())
                        val orderRepo = OrderRepositoryImpl(db.orderDao())
                        RestaurantDashboardViewModel(
                            dishRepository = dishRepo,
                            orderRepository = orderRepo,
                            authRepository = authRepository
                        ) as T
                    }
                    BuyerSearchViewModel::class.java.name -> {
                        val db = AppDatabase.getDatabase(context)
                        val dishRepo = LocalRestaurantRepositoryImpl(db.dishDao())
                        val saveSearchQueryUseCase = com.labajada.app.domain.usecase.search.SaveSearchQueryUseCase(dishRepo)
                        val getRecentSearchHistoryUseCase = com.labajada.app.domain.usecase.search.GetRecentSearchHistoryUseCase(dishRepo)
                        val manageFavoriteRestaurantUseCase = com.labajada.app.domain.usecase.search.ManageFavoriteRestaurantUseCase(dishRepo)

                        BuyerSearchViewModel(
                            saveSearchQueryUseCase = saveSearchQueryUseCase,
                            getRecentSearchHistoryUseCase = getRecentSearchHistoryUseCase,
                            manageFavoriteRestaurantUseCase = manageFavoriteRestaurantUseCase, // ◄ CORREGIDO
                            dishDao = db.dishDao(),
                            authRepository = authRepository
                        ) as T
                    }

                    OrderViewModel::class.java.name -> {
                        val orderRepo = OrderRepositoryImpl(db.orderDao())
                        OrderViewModel(orderRepository = orderRepo) as T
                    }
                    else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
                }
            }
        }
    }

    //Variable  para forzar la lectura de Room al iniciar, loguearse o salir
    var checkSessionTrigger by remember { mutableStateOf(0) }
    var uiState by remember { mutableStateOf<NavUiState>(NavUiState.Loading) }

    LaunchedEffect(checkSessionTrigger) {
        val activeSession = authRepository.getActiveSession()
        uiState = NavUiState.Success(activeSession?.role ?: "")
    }
    when (val state = uiState) {
        is NavUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFD32F2F))
            }
        }
        is NavUiState.Success -> {
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
                // --- INICIO DE SESIÓN ---
                composable(route = Screen.Login.route) {
                    val loginVm: LoginViewModel = viewModel(factory = viewModelFactory)
                    LoginScreen(
                        viewModel = loginVm,
                        onLoginSuccess = { rol ->
                            scope.launch {
                                // ◄ CORREGIDO: Notifica al NavGraph que hay una nueva sesión activa
                                checkSessionTrigger++
                                val destinoFinal = if (rol == "BUYER") Screen.BuyerHome.route else Screen.RestaurantHome.route
                                navController.navigate(destinoFinal) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
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

                // --- FLUJO 2: RUTA COMPRADOR CON REGISTRO REAL ---
                composable(route = Screen.RegisterBuyer.route) {
                    val buyerVm: BuyerRegisterViewModel = viewModel(factory = viewModelFactory)
                    BuyerRegisterScreen(
                        viewModel = buyerVm,
                        onRegistrationComplete = {
                            scope.launch {
                                checkSessionTrigger++
                                navController.navigate(Screen.BuyerHome.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(route = Screen.BuyerHome.route) {
                    val searchVm: BuyerSearchViewModel = viewModel(factory = viewModelFactory)
                    val orderVm: OrderViewModel = viewModel(factory = viewModelFactory)
                    BuyerSearchScreen(
                        searchViewModel = searchVm,
                        orderViewModel = orderVm,
                        onNavigateToMap = { navController.navigate("buyer_map_buyer") },
                        onLogout = {
                            scope.launch {
                                authDao.logout()
                                checkSessionTrigger++
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(route = "buyer_map_buyer") {
                    BuyerMapScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                // --- FLUJO 3: RUTA RESTAURANTE CON REGISTRO REAL ---
                composable(route = Screen.RegisterRestaurant.route) {
                    val restaurantVm: RestaurantRegisterViewModel = viewModel(factory = viewModelFactory)
                    RestaurantRegisterScreen(
                        viewModel = restaurantVm,
                        onRegistrationComplete = {
                            scope.launch {
                                // ◄ CORREGIDO: Fuerza la actualización al registrarse
                                checkSessionTrigger++
                                navController.navigate(Screen.RestaurantHome.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(route = Screen.RestaurantHome.route) {
                    val dashboardVm: RestaurantDashboardViewModel = viewModel(factory = viewModelFactory)
                    RestaurantDashboardScreen(
                        viewModel = dashboardVm,
                        onLogout = {
                            scope.launch {
                                authDao.logout()
                                checkSessionTrigger++
                                navController.navigate(Screen.Login.route) {
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
