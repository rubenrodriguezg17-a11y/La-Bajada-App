package com.labajada.app.presentation.restaurant.dashboard

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.model.Dish
import com.labajada.app.domain.model.OrderStatus
import com.labajada.app.domain.model.Session
import com.labajada.app.domain.repository.AuthRepository
import com.labajada.app.domain.repository.LocalDishRepository
import com.labajada.app.domain.repository.OrderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantDashboardViewModel(
    private val dishRepository: LocalDishRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantDashboardState())
    val uiState: StateFlow<RestaurantDashboardState> = _uiState.asStateFlow()

    private var currentRestaurantId: String = ""

    private val activeSessionFlow = flow {
        val session = authRepository.getActiveSession()
        currentRestaurantId = session?.userId ?: ""
        emit(session)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val activeSession = activeSessionFlow.flatMapLatest { session ->
        if (session != null) {
            dishRepository.getRestaurantById(session.userId)
        } else {
            flow { emit(null) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val fallbackRestaurantName = activeSessionFlow.flatMapLatest { session ->
        flow { emit(session?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Huarique") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Huarique")
    val platillosDelDia = activeSessionFlow.flatMapLatest { session ->
        if (session != null) {
            dishRepository.getMenuOfTheDay(session.userId)
        } else {
            flow { emit(emptyList()) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pedidosActivos = orderRepository.getActiveOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pedidosDespachados = orderRepository.getDispatchedOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gananciasHoy = pedidosDespachados
        .combine(platillosDelDia) { despachados, _ ->
            despachados.sumOf { it.totalPrice }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        viewModelScope.launch {
            activeSession.collect { restaurant ->
                restaurant?.let { res ->
                    _uiState.update { it.copy(
                        resNameByOwner = res.restaurantName,
                        resRucByOwner = res.rucNumber,
                        resPhoneByOwner = res.phoneNumber,
                        resCategoryByOwner = res.selectedCategory,
                        resAddressByOwner = res.addressDetails,
                        resLatitude = res.latitude,
                        resLongitude = res.longitude
                    )}
                }
            }
        }
    }
    val categoriesDisponibles = listOf("Cevichería", "Criollo", "Fast Food / Bajadas", "Pollería", "Chifa")

    fun onTabSelected(index: Int) = _uiState.update { it.copy(selectedTab = index) }
    fun toggleDeleteDialog(show: Boolean) = _uiState.update { it.copy(showDeleteDialog = show) }
    fun toggleFormSheet(show: Boolean) = _uiState.update { it.copy(showFormSheet = show) }

    fun onDishNameChange(value: String) = _uiState.update { it.copy(dishName = value) }
    fun onDishPriceChange(value: String) = _uiState.update { it.copy(dishPrice = value) }
    fun onDishImageChange(uri: Uri?) = _uiState.update { it.copy(selectedImageUri = uri) }

    // --- Lógica del Perfil ---
    fun toggleProfileEdit() = _uiState.update { it.copy(isEditingProfile = !_uiState.value.isEditingProfile) }
    fun onProfileNameChange(v: String) = _uiState.update { it.copy(resNameByOwner = v) }
    fun onProfileRucChange(v: String) = _uiState.update { it.copy(resRucByOwner = v) }
    fun onProfilePhoneChange(v: String) = _uiState.update { it.copy(resPhoneByOwner = v) }
    fun onProfileAddressChange(v: String) = _uiState.update { it.copy(resAddressByOwner = v) }
    fun toggleProfileCategoryDropdown() = _uiState.update { it.copy(expandedProfileCategory = !it.expandedProfileCategory) }
    fun onProfileCategorySelected(v: String) = _uiState.update { it.copy(resCategoryByOwner = v, expandedProfileCategory = false) }
    fun toggleProfileMap(show: Boolean) = _uiState.update { it.copy(showProfileMapDialog = show) }
    fun onProfileLocationConfirmed(lat: Double, lng: Double) = _uiState.update {
        it.copy(resLatitude = lat, resLongitude = lng, showProfileMapDialog = false)
    }
    fun guardarDatosDelLocal() {
        val state = _uiState.value
        viewModelScope.launch {
            val restaurantActual = authRepository.getActiveSession()

            val restauranteActualizado = com.labajada.app.domain.model.Restaurant(
                id = currentRestaurantId.toIntOrNull() ?: 0,
                restaurantName = state.resNameByOwner,
                rucNumber = state.resRucByOwner,
                phoneNumber = state.resPhoneByOwner,
                selectedCategory = state.resCategoryByOwner,
                addressDetails = state.resAddressByOwner,
                latitude = state.resLatitude,
                longitude = state.resLongitude,
                email = restaurantActual?.email ?: "",
                password = ""
            )
            dishRepository.updateRestaurantProfile(restauranteActualizado)
            _uiState.update { it.copy(isEditingProfile = false) }
        }
    }
    fun cambiarEstadoPedido(orderId: String, actualStatus: OrderStatus) {
        viewModelScope.launch {
            val siguienteEstado = when (actualStatus) {
                OrderStatus.PENDING -> OrderStatus.READY
                OrderStatus.READY -> OrderStatus.DISPATCHED
                else -> OrderStatus.DISPATCHED
            }
            orderRepository.updateOrderStatus(orderId, siguienteEstado)
        }
    }

    fun prepararNuevoPlatillo() {
        _uiState.update {
            it.copy(isEditing = false, dishName = "", dishPrice = "", selectedImageUri = null)
        }
    }

    fun prepararEdicionPlatillo(index: Int, dish: Dish) {
        _uiState.update {
            it.copy(
                itemIndexToAction = index,
                itemIdToEdit = dish.id,
                isEditing = true,
                dishName = dish.name,
                dishPrice = dish.price.replace("S/. ", ""),
                selectedImageUri = if (dish.imagePath.isNotEmpty()) Uri.parse(dish.imagePath) else null
            )
        }
    }

    fun guardarPlatillo(context: Context) {
        val state = _uiState.value
        val formattedPrice = if (state.dishPrice.startsWith("S/. ")) state.dishPrice else "S/. ${state.dishPrice}"
        var finalImagePath = ""

        state.selectedImageUri?.let { uri ->
            if (uri.toString().startsWith("content://")) {
                finalImagePath = guardarImagenEnAlmacenamientoInterno(context, uri) ?: ""
            } else { finalImagePath = uri.toString() }
        }

        val dishModel = Dish(
            id = if (state.isEditing) state.itemIdToEdit else java.util.UUID.randomUUID().toString(),
            restaurantId = currentRestaurantId,
            name = state.dishName,
            price = formattedPrice,
            imagePath = finalImagePath
        )

        viewModelScope.launch {
            if (state.isEditing) dishRepository.updateDishInMenu(dishModel)
            else dishRepository.saveDishToMenu(dishModel)
        }
    }

    fun eliminarPlatillo(dishId: String) {
        viewModelScope.launch { dishRepository.deleteDishFromMenu(dishId) }
    }

    private fun guardarImagenEnAlmacenamientoInterno(context: Context, uri: Uri): String? {
        return try {
            val fileName = "platillo_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            context.contentResolver.openInputStream(uri).use { inputStream ->
                FileOutputStream(file).use { outputStream -> inputStream?.copyTo(outputStream) }
            }
            file.absolutePath
        } catch (e: Exception) { e.printStackTrace(); null }
    }

    fun prepararEliminacionPlatillo(index: Int, dishId: String) {
        _uiState.update {
            it.copy(
                itemIndexToAction = index,
                itemIdToEdit = dishId
            )
        }
    }

}