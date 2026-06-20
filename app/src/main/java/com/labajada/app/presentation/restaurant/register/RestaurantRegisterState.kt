package com.labajada.app.presentation.restaurant.register

data class RestaurantRegisterState(
    val restaurantName: String = "",
    val rucNumber: String = "",
    val phoneNumber: String = "",
    val addressDetails: String = "",
    val email: String = "",
    val password: String = "",
    val selectedCategory: String = "",
    val expandedCategory: Boolean = false,
    val latitude: Double = -8.1116,
    val longitude: Double = -79.0287,
    val isLocationSelected: Boolean = false,
    val showMapDialog: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isFormValid: Boolean = restaurantName.isNotBlank() &&
            rucNumber.isNotBlank() &&
            phoneNumber.isNotBlank() &&
            selectedCategory.isNotBlank() &&
            addressDetails.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            isLocationSelected
}
