package com.labajada.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
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
import com.labajada.app.data.repository.AuthRepositoryImpl
import com.labajada.app.data.repository.BuyerRepositoryImpl
import com.labajada.app.data.repository.DishRepositoryImpl
import com.labajada.app.data.repository.OrderRepositoryImpl
import com.labajada.app.data.repository.RestaurantRepositoryImpl
import com.labajada.app.data.repository.SearchRepositoryImpl
import com.labajada.app.domain.repository.AuthRepository
import com.labajada.app.domain.usecase.auth.GetActiveBuyerUseCase
import com.labajada.app.domain.usecase.auth.LoginUseCase
import com.labajada.app.domain.usecase.auth.RegisterBuyerUseCase
import com.labajada.app.domain.usecase.auth.RegisterRestaurantUseCase
import com.labajada.app.domain.usecase.search.ClearSearchHistoryUseCase
import com.labajada.app.domain.usecase.search.GetAllDishesUseCase
import com.labajada.app.domain.usecase.search.GetRecentSearchHistoryUseCase
import com.labajada.app.domain.usecase.search.ManageFavoriteRestaurantUseCase
import com.labajada.app.domain.usecase.search.SaveSearchQueryUseCase
import com.labajada.app.presentation.buyer.map.BuyerMapScreen
import com.labajada.app.presentation.buyer.register.BuyerRegisterScreen
import com.labajada.app.presentation.buyer.register.BuyerRegisterViewModel
import com.labajada.app.presentation.buyer.search.BuyerSearchScreen
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel
import com.labajada.app.presentation.login.LoginScreen
import com.labajada.app.presentation.login.LoginViewModel
import com.labajada.app.presentation.onboarding.OnboardingScreen
import com.labajada.app.presentation.order.OrderViewModel
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardScreen
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.register.RestaurantRegisterScreen
import com.labajada.app.presentation.restaurant.register.RestaurantRegisterViewModel
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

    val db = remember { AppDatabase.getDatabase(context) }

    // Repositorios
    val authRepository: AuthRepository = remember {
        AuthRepositoryImpl(
            authDao = db.authDao(),
            buyerDao = db.buyerDao(),
            restaurantDao = db.restaurantDao()
        )
    }
    val buyerRepository = remember { BuyerRepositoryImpl(db.buyerDao()) }
    val restaurantRepository = remember { RestaurantRepositoryImpl(db.restaurantDao()) }
    val dishRepository = remember { DishRepositoryImpl(db.dishDao()) }
    val searchRepository = remember { SearchRepositoryImpl(db.searchDao()) }
    val orderRepository = remember { OrderRepositoryImpl(db.orderDao()) }

    // UseCases auth
    val loginUseCase = remember { LoginUseCase(authRepository) }
    val registerBuyerUseCase = remember { RegisterBuyerUseCase(authRepository) }
    val registerRestaurantUseCase = remember { RegisterRestaurantUseCase(authRepository) }
    val getActiveBuyerUseCase = remember { GetActiveBuyerUseCase(authRepository, buyerRepository) }

    // UseCases search
    val saveSearchQueryUseCase = remember { SaveSearchQueryUseCase(searchRepository) }
    val getRecentSearchHistoryUseCase = remember { GetRecentSearchHistoryUseCase(searchRepository) }
    val clearSearchHistoryUseCase = remember { ClearSearchHistoryUseCase(searchRepository) }
    val manageFavoriteRestaurantUseCase = remember { ManageFavoriteRestaurantUseCase(restaurantRepository) }
    val getAllDishesUseCase = remember { GetAllDishesUseCase(dishRepository) }

    val viewModelFactory = remember {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when (modelClass.name) {
                    LoginViewModel::class.java.name ->
                        LoginViewModel(loginUseCase) as T

                    BuyerRegisterViewModel::class.java.name ->
                        BuyerRegisterViewModel(registerBuyerUseCase) as T

                    RestaurantRegisterViewModel::class.java.name ->
                        RestaurantRegisterViewModel(registerRestaurantUseCase) as T

                    RestaurantDashboardViewModel::class.java.name ->
                        RestaurantDashboardViewModel(
                            restaurantRepository = restaurantRepository,
                            dishRepository = dishRepository,
                            orderRepository = orderRepository,
                            authRepository = authRepository
                        ) as T

                    BuyerSearchViewModel::class.java.name ->
                        BuyerSearchViewModel(
                            saveSearchQueryUseCase = saveSearchQueryUseCase,
                            getRecentSearchHistoryUseCase = getRecentSearchHistoryUseCase,
                            manageFavoriteRestaurantUseCase = manageFavoriteRestaurantUseCase,
                            getActiveBuyerUseCase = getActiveBuyerUseCase,
                            clearSearchHistoryUseCase = clearSearchHistoryUseCase,
                            getAllDishesUseCase = getAllDishesUseCase,
                            dishRepository = dishRepository,
                            restaurantRepository = restaurantRepository,
                            authRepository = authRepository
                        ) as T

                    OrderViewModel::class.java.name ->
                        OrderViewModel(orderRepository = orderRepository) as T

                    else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
                }
            }
        }
    }

    var checkSessionTrigger by remember { mutableStateOf(0) }
    var uiState by remember { mutableStateOf<NavUiState>(NavUiState.Loading) }

    LaunchedEffect(checkSessionTrigger) {
        val activeSession = authRepository.getActiveSession()
        uiState = NavUiState.Success(activeSession?.role ?: "")
    }

    when (val state = uiState) {
        is NavUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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

            NavHost(navController = navController, startDestination = startDestination) {

                composable(route = Screen.Login.route) {
                    val loginVm: LoginViewModel = viewModel(factory = viewModelFactory)
                    LoginScreen(
                        viewModel = loginVm,
                        onLoginSuccess = { rol ->
                            scope.launch {
                                checkSessionTrigger++
                                val destino = if (rol == "BUYER") Screen.BuyerHome.route else Screen.RestaurantHome.route
                                navController.navigate(destino) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        },
                        onNavigateToOnboarding = {
                            navController.navigate(Screen.Onboarding.route)
                        }
                    )
                }

                composable(route = Screen.Onboarding.route) {
                    OnboardingScreen(
                        onBuyerSelected = { navController.navigate(Screen.RegisterBuyer.route) },
                        onRestaurantSelected = { navController.navigate(Screen.RegisterRestaurant.route) }
                    )
                }

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
                        onLogout = {
                            scope.launch {
                                authRepository.logout()
                                checkSessionTrigger++
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(route = "buyer_map_buyer") {
                    BuyerMapScreen(onNavigateBack = { navController.popBackStack() })
                }

                composable(route = Screen.RegisterRestaurant.route) {
                    val restaurantVm: RestaurantRegisterViewModel = viewModel(factory = viewModelFactory)
                    RestaurantRegisterScreen(
                        viewModel = restaurantVm,
                        onRegistrationComplete = {
                            scope.launch {
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
                                authRepository.logout()
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