package com.labajada.app.presentation.restaurant.register

data class RestaurantRegisterState(
    val currentStep: Int = 1,

    // Paso 1: Identidad del negocio
    val restaurantName: String = "",
    val rucNumber: String = "",
    val phoneNumber: String = "",
    val selectedCategory: String = "",
    val expandedCategory: Boolean = false,

    // Paso 2: Ubicación y delivery
    val addressDetails: String = "",
    val latitude: Double = -8.1116,
    val longitude: Double = -79.0287,
    val isLocationSelected: Boolean = false,
    val showMapDialog: Boolean = false,
    val offersDelivery: Boolean = false,
    val maxDeliveryDistanceKm: Double = 0.1,
    val imageUrl: String? = null,
    val businessHours: String? = null,

    // Paso 3: Credenciales
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // Estado general
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isStep1Valid: Boolean
        get() = restaurantName.isNotBlank() && rucNumber.isNotBlank() &&
                phoneNumber.isNotBlank() && selectedCategory.isNotBlank()

    val isStep2Valid: Boolean
        get() = addressDetails.isNotBlank() && isLocationSelected &&
                (!offersDelivery || maxDeliveryDistanceKm > 0)

    val isStep3Valid: Boolean
        get() = email.isNotBlank() && password.isNotBlank() &&
                confirmPassword.isNotBlank() && password == confirmPassword
}