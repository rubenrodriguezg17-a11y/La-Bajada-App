package com.labajada.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.labajada.app.presentation.buyer.map.BuyerMapScreen
import com.labajada.app.presentation.buyer.register.BuyerRegisterScreen
import com.labajada.app.presentation.buyer.register.BuyerRegisterViewModel
import com.labajada.app.presentation.buyer.search.BuyerSearchScreen
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel
import com.labajada.app.presentation.login.LoginScreen
import com.labajada.app.presentation.login.LoginViewModel
import com.labajada.app.presentation.login.forgot.ForgotPasswordScreen
import com.labajada.app.presentation.login.forgot.ForgotPasswordViewModel
import com.labajada.app.presentation.onboarding.OnboardingScreen
import com.labajada.app.presentation.order.OrderViewModel
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardScreen
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.register.RestaurantRegisterScreen
import com.labajada.app.presentation.restaurant.register.RestaurantRegisterViewModel
import kotlinx.coroutines.launch
// Interface donde están las rutas
sealed interface NavUiState {
    object Loading : NavUiState
    data class Success(val role: String) : NavUiState
}

@Composable
fun LaBajadaNavGraph() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val deps = remember { AppDependencies(context) }
    val factory = remember { AppViewModelFactory(deps) }

    var checkSessionTrigger by remember { mutableStateOf(0) }
    var uiState by remember { mutableStateOf<NavUiState>(NavUiState.Loading) }

    LaunchedEffect(checkSessionTrigger) {
        val activeSession = deps.authRepository.getActiveSession()
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

                composable(Screen.Login.route) {
                    val vm: LoginViewModel = viewModel(factory = factory)
                    LoginScreen(
                        viewModel = vm,
                        onLoginSuccess = { rol ->
                            scope.launch {
                                checkSessionTrigger++
                                val destino = if (rol == "BUYER") Screen.BuyerHome.route else Screen.RestaurantHome.route
                                navController.navigate(destino) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        },
                        onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) },
                        onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
                    )
                }

                composable(Screen.ForgotPassword.route) {
                    val vm: ForgotPasswordViewModel = viewModel(factory = factory)
                    ForgotPasswordScreen (
                        viewModel = vm,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onBuyerSelected = { navController.navigate(Screen.RegisterBuyer.route) },
                        onRestaurantSelected = { navController.navigate(Screen.RegisterRestaurant.route) }
                    )
                }

                composable(Screen.RegisterBuyer.route) {
                    val vm: BuyerRegisterViewModel = viewModel(factory = factory)
                    BuyerRegisterScreen(
                        viewModel = vm,
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

                composable(Screen.BuyerHome.route) {
                    val searchVm: BuyerSearchViewModel = viewModel(factory = factory)
                    val orderVm: OrderViewModel = viewModel(factory = factory)
                    BuyerSearchScreen(
                        searchViewModel = searchVm,
                        orderViewModel = orderVm,
                        onLogout = {
                            scope.launch {
                                deps.authRepository.logout()
                                checkSessionTrigger++
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable("buyer_map_buyer") {
                    BuyerMapScreen(onNavigateBack = { navController.popBackStack() })
                }

                composable(Screen.RegisterRestaurant.route) {
                    val vm: RestaurantRegisterViewModel = viewModel(factory = factory)
                    RestaurantRegisterScreen(
                        viewModel = vm,
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

                composable(Screen.RestaurantHome.route) {
                    val vm: RestaurantDashboardViewModel = viewModel(factory = factory)
                    RestaurantDashboardScreen(
                        viewModel = vm,
                        onLogout = {
                            scope.launch {
                                deps.authRepository.logout()
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