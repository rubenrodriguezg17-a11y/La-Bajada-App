package com.labajada.app.presentation.buyer.search

data class BuyerSearchState(
    val showProfileSheet: Boolean = false,
    val showCartSheet: Boolean = false,
    val showMenuSheet: Boolean = false,
    val profileCurrentSection: String = "MENU",
    val selectedHuariqueForCart: RadarHuarique? = null,
    val selectedDishForCart: com.labajada.app.domain.model.Dish? = null,
    val huariqueParaMenu: RadarHuarique? = null,
    val cantidadSeleccionada: Int = 1,
    val hasLocationPermission: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val locationTrigger: Int = 0
)