package com.labajada.app.presentation.restaurant.dashboard

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.data.local.AppDatabase
import com.labajada.app.data.repository.LocalDishRepositoryImpl
import com.labajada.app.data.repository.OrderRepositoryImpl
import com.labajada.app.domain.model.Dish
import com.labajada.app.domain.model.OrderStatus
import com.labajada.app.domain.repository.LocalDishRepository
import com.labajada.app.domain.repository.OrderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class RestaurantDashboardViewModel(context: Context) : ViewModel() {

    private val database = AppDatabase.getDatabase(context)
    private val dishRepository: LocalDishRepository = LocalDishRepositoryImpl(database.dishDao())

    private val orderRepository: OrderRepository = OrderRepositoryImpl(database.orderDao())

    val platillosDelDia = dishRepository.getMenuOfTheDay()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pedidosActivos = orderRepository.getActiveOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pedidosDespachados = orderRepository.getDispatchedOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gananciasHoy = derivedStateOf {
        pedidosDespachados.value.sumOf { it.totalPrice }
    }

    var dishName = mutableStateOf("")
    var dishPrice = mutableStateOf("")
    var selectedImageUri = mutableStateOf<Uri?>(null)
    var isEditing = mutableStateOf(false)
    var itemIndexToAction = mutableStateOf<Int?>(null)
    var itemIdToEdit = mutableStateOf("")

    // --- MÉTODOS DE ACCIÓN ---

    fun cambiarEstadoPedido(orderId: String, actualStatus: OrderStatus) {
        viewModelScope.launch {
            val siguienteEstado = when (actualStatus) {
                OrderStatus.PENDING -> OrderStatus.READY        // Pasa de Pendiente a Listo
                OrderStatus.READY -> OrderStatus.DISPATCHED     // Pasa de Listo a Despachado y suma caja 💰
                else -> OrderStatus.DISPATCHED
            }
            orderRepository.updateOrderStatus(orderId, siguienteEstado)
        }
    }

    fun prepararNuevoPlatillo() {
        isEditing.value = false
        dishName.value = ""
        dishPrice.value = ""
        selectedImageUri.value = null
    }

    fun prepararEdicionPlatillo(index: Int, dish: Dish) {
        itemIndexToAction.value = index
        itemIdToEdit.value = dish.id
        isEditing.value = true
        dishName.value = dish.name
        dishPrice.value = dish.price.replace("S/. ", "")
        selectedImageUri.value = if (dish.imagePath.isNotEmpty()) Uri.parse(dish.imagePath) else null
    }

    fun guardarPlatillo(context: Context) {
        val formattedPrice = if (dishPrice.value.startsWith("S/. ")) dishPrice.value else "S/. ${dishPrice.value}"
        var finalImagePath = ""
        selectedImageUri.value?.let { uri ->
            if (uri.toString().startsWith("content://")) {
                finalImagePath = guardarImagenEnAlmacenamientoInterno(context, uri) ?: ""
            } else { finalImagePath = uri.toString() }
        }
        val dishModel = Dish(
            id = if (isEditing.value) itemIdToEdit.value else java.util.UUID.randomUUID().toString(),
            name = dishName.value, price = formattedPrice, imagePath = finalImagePath
        )
        viewModelScope.launch {
            if (isEditing.value) dishRepository.updateDishInMenu(dishModel)
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
}
