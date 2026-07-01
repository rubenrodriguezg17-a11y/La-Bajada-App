package com.labajada.app.presentation.navigation

import android.content.Context
import com.labajada.app.data.local.AppDatabase
import com.labajada.app.data.repository.*
import com.labajada.app.domain.repository.AuthRepository
import com.labajada.app.domain.usecase.auth.*
import com.labajada.app.domain.usecase.search.*
//Clase para agregar los UseCases
class AppDependencies(context: Context) {

    private val db = AppDatabase.getDatabase(context)

    // Repositorios
    val authRepository: AuthRepository = AuthRepositoryImpl(
        authDao = db.authDao(),
        buyerDao = db.buyerDao(),
        restaurantDao = db.restaurantDao()
    )
    val buyerRepository = BuyerRepositoryImpl(db.buyerDao())
    val restaurantRepository = RestaurantRepositoryImpl(db.restaurantDao())
    val dishRepository = DishRepositoryImpl(db.dishDao())
    val searchRepository = SearchRepositoryImpl(db.searchDao())
    val orderRepository = OrderRepositoryImpl(db.orderDao())

    // UseCases auth
    val loginUseCase = LoginUseCase(authRepository)
    val registerBuyerUseCase = RegisterBuyerUseCase(authRepository)
    val registerRestaurantUseCase = RegisterRestaurantUseCase(authRepository)
    val getActiveBuyerUseCase = GetActiveBuyerUseCase(authRepository, buyerRepository)
    val sendPasswordResetEmailUseCase = SendPasswordResetEmailUseCase(authRepository)

    // UseCases search
    val saveSearchQueryUseCase = SaveSearchQueryUseCase(searchRepository)
    val getRecentSearchHistoryUseCase = GetRecentSearchHistoryUseCase(searchRepository)
    val clearSearchHistoryUseCase = ClearSearchHistoryUseCase(searchRepository)
    val manageFavoriteRestaurantUseCase = ManageFavoriteRestaurantUseCase(restaurantRepository)
    val getAllDishesUseCase = GetAllDishesUseCase(dishRepository)
}