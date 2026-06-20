package com.labajada.app.presentation.buyer.register

data class BuyerRegisterState(
    val name: String = "",
    val phone: String = "",
    val departamento: String = "",
    val provincia: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
