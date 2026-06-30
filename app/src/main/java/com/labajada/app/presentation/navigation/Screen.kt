package com.labajada.app.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Onboarding : Screen("onboarding")
    object RegisterBuyer : Screen("register_buyer")
    object RegisterRestaurant : Screen("register_restaurant")
    object BuyerHome : Screen("buyer_home")
    object RestaurantHome : Screen("restaurant_home")
    object ForgotPassword : Screen("forgot_password")
}
