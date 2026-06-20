package com.labajada.app.presentation.restaurant.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.model.Restaurant
import com.labajada.app.domain.usecase.auth.RegisterRestaurantUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RestaurantRegisterViewModel(
    private val registerRestaurantUseCase: RegisterRestaurantUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantRegisterState())
    val uiState: StateFlow<RestaurantRegisterState> = _uiState.asStateFlow()

    // Funciones de actualización de campos (igual que antes)
    fun onNameChange(value: String) = _uiState.update { it.copy(restaurantName = value) }
    fun onRucChange(value: String) = _uiState.update { it.copy(rucNumber = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phoneNumber = value) }
    fun onCategorySelected(category: String) = _uiState.update {
        it.copy(selectedCategory = category, expandedCategory = false)
    }
    fun toggleCategoryDropdown() = _uiState.update {
        it.copy(expandedCategory = !it.expandedCategory)
    }
    fun onAddressChange(value: String) = _uiState.update { it.copy(addressDetails = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }
    fun toggleMapDialog(show: Boolean) = _uiState.update { it.copy(showMapDialog = show) }
    fun onLocationConfirmed(lat: Double, lng: Double) = _uiState.update {
        it.copy(latitude = lat, longitude = lng, isLocationSelected = true, showMapDialog = false)
    }

    fun registerRestaurant(onComplete: () -> Unit) {
        val state = _uiState.value
        // Validaciones igual que antes
        if (state.restaurantName.isBlank() || state.rucNumber.isBlank() ||
            state.phoneNumber.isBlank() || state.selectedCategory.isBlank() ||
            state.addressDetails.isBlank() || state.email.isBlank() ||
            state.password.isBlank() || !state.isLocationSelected) {
            _uiState.update { it.copy(error = "Completa todos los campos y selecciona ubicación.") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val newRestaurant = Restaurant(
                    email = state.email.trim(),
                    restaurantName = state.restaurantName.trim(),
                    rucNumber = state.rucNumber.trim(),
                    phoneNumber = state.phoneNumber.trim(),
                    selectedCategory = state.selectedCategory,
                    addressDetails = state.addressDetails.trim(),
                    latitude = state.latitude,
                    longitude = state.longitude,
                    password = state.password
                )

                val result = registerRestaurantUseCase.execute(newRestaurant)
                _uiState.update { it.copy(isLoading = false) }

                result.onSuccess {
                    onComplete()
                }.onFailure { exception ->
                    _uiState.update { it.copy(error = "Fallo en la persistencia: ${exception.localizedMessage}") }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Error inesperado: ${e.localizedMessage}")
                }
            }
        }
    }
}