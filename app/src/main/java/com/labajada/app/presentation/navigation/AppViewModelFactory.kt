package com.labajada.app.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.labajada.app.presentation.buyer.register.BuyerRegisterViewModel
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel
import com.labajada.app.presentation.login.LoginViewModel
import com.labajada.app.presentation.login.forgot.ForgotPasswordViewModel
import com.labajada.app.presentation.order.OrderViewModel
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.register.RestaurantRegisterViewModel

//Clase Para aregar los ViewModels
class AppViewModelFactory(
    private val deps: AppDependencies
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass.name) {
            LoginViewModel::class.java.name ->
                LoginViewModel(deps.loginUseCase) as T

            ForgotPasswordViewModel::class.java.name ->
                ForgotPasswordViewModel(deps.sendPasswordResetEmailUseCase) as T

            BuyerRegisterViewModel::class.java.name ->
                BuyerRegisterViewModel(deps.registerBuyerUseCase) as T

            RestaurantRegisterViewModel::class.java.name ->
                RestaurantRegisterViewModel(deps.registerRestaurantUseCase) as T

            RestaurantDashboardViewModel::class.java.name ->
                RestaurantDashboardViewModel(
                    restaurantRepository = deps.restaurantRepository,
                    dishRepository = deps.dishRepository,
                    orderRepository = deps.orderRepository,
                    authRepository = deps.authRepository
                ) as T

            BuyerSearchViewModel::class.java.name ->
                BuyerSearchViewModel(
                    saveSearchQueryUseCase = deps.saveSearchQueryUseCase,
                    getRecentSearchHistoryUseCase = deps.getRecentSearchHistoryUseCase,
                    manageFavoriteRestaurantUseCase = deps.manageFavoriteRestaurantUseCase,
                    getActiveBuyerUseCase = deps.getActiveBuyerUseCase,
                    clearSearchHistoryUseCase = deps.clearSearchHistoryUseCase,
                    getAllDishesUseCase = deps.getAllDishesUseCase,
                    dishRepository = deps.dishRepository,
                    restaurantRepository = deps.restaurantRepository,
                    authRepository = deps.authRepository
                ) as T

            OrderViewModel::class.java.name ->
                OrderViewModel(orderRepository = deps.orderRepository) as T

            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}