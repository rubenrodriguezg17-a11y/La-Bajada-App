package com.labajada.app.presentation.restaurant.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.core.validation.PasswordValidator
import com.labajada.app.core.validation.PeruValidators
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

    fun onNameChange(value: String) = _uiState.update { it.copy(restaurantName = value, error = null) }
    fun onRucChange(value: String) = _uiState.update { it.copy(rucNumber = value, error = null) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phoneNumber = value, error = null) }
    fun onCategorySelected(category: String) = _uiState.update {
        it.copy(selectedCategory = category, expandedCategory = false)
    }
    fun toggleCategoryDropdown() = _uiState.update {
        it.copy(expandedCategory = !it.expandedCategory)
    }
    fun onAddressChange(value: String) = _uiState.update { it.copy(addressDetails = value, error = null) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, error = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, error = null) }
    fun toggleMapDialog(show: Boolean) = _uiState.update { it.copy(showMapDialog = show) }
    fun onLocationConfirmed(lat: Double, lng: Double) = _uiState.update {
        it.copy(latitude = lat, longitude = lng, isLocationSelected = true, showMapDialog = false)
    }

    fun registerRestaurant(onComplete: () -> Unit) {
        val state = _uiState.value

        if (state.restaurantName.isBlank() || state.rucNumber.isBlank() ||
            state.phoneNumber.isBlank() || state.selectedCategory.isBlank() ||
            state.addressDetails.isBlank() || state.email.isBlank() ||
            state.password.isBlank() || !state.isLocationSelected) {
            _uiState.update { it.copy(error = "Completa todos los campos y selecciona ubicación.") }
            return
        }

        if (!PeruValidators.isValidDocumento(state.rucNumber)) {
            _uiState.value = _uiState.value.copy(error = "Documento inválido. Debe ser DNI (8 dígitos) o RUC (11 dígitos).")
            return
        }

        if (!PeruValidators.isValidPhone(state.phoneNumber)) {
            _uiState.update { it.copy(error = "Ingresa un celular válido (9 dígitos, empieza con 9).") }
            return
        }

        if (!PasswordValidator.isValidEmail(state.email)) {
            _uiState.update { it.copy(error = "Ingresa un correo electrónico válido.") }
            return
        }

        val passwordCheck = PasswordValidator.validate(state.password)
        if (!passwordCheck.isValid) {
            _uiState.update { it.copy(error = "La contraseña no cumple con los requisitos de seguridad.") }
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