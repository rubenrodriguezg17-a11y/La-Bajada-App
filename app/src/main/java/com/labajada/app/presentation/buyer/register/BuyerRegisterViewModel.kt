package com.labajada.app.presentation.buyer.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.model.Buyer
import com.labajada.app.domain.usecase.auth.RegisterBuyerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BuyerRegisterViewModel(
    private val registerBuyerUseCase: RegisterBuyerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuyerRegisterState())
    val uiState: StateFlow<BuyerRegisterState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }
    fun onDepartamentoChange(value: String) = _uiState.update { it.copy(departamento = value) }
    fun onProvinciaChange(value: String) = _uiState.update { it.copy(provincia = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }

    fun registerBuyer(onComplete: () -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank() || state.phone.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Por favor, completa todos los campos obligatorios.") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val newBuyer = Buyer(
                    email = state.email.trim(),
                    name = state.name.trim(),
                    phone = state.phone.trim(),
                    departamento = state.departamento.trim(),
                    provincia = state.provincia.trim(),
                    password = state.password
                )

                val result = registerBuyerUseCase.execute(newBuyer)
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