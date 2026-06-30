package com.labajada.app.presentation.restaurant.dashboard

import android.net.Uri

data class RestaurantDashboardState(
    // Estados de Control de Navegación y Modales de UI
    val selectedTab: Int = 0,
    val showDeleteDialog: Boolean = false,
    val showFormSheet: Boolean = false,

    // Estados del Formulario de Platillos
    val dishName: String = "",
    val dishPrice: String = "",
    val selectedImageUri: Uri? = null,
    val isEditing: Boolean = false,
    val itemIndexToAction: Int? = null,
    val itemIdToEdit: String = "",

    // Estados de la Ficha del Local (Perfil)
    val isEditingProfile: Boolean = false,
    val resNameByOwner: String = "Mi Cocina Principal",
    val resRucByOwner: String = "20123456789",
    val resPhoneByOwner: String = "945612378",
    val resAddressByOwner: String = "Av. Larco 123, Trujillo",
    val resCategoryByOwner: String = "Criollo",
    val resLatitude: Double = -8.1116,
    val resLongitude: Double = -79.0287,
    val showProfileMapDialog: Boolean = false,
    val expandedProfileCategory: Boolean = false,

    // Delivery e imagen
    val resOffersDelivery: Boolean = false,
    val resMaxDeliveryDistanceKm: Double = 3.0,
    val resImageUrl: String? = null
)