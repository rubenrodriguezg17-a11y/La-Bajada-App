package com.labajada.app.presentation.restaurant.dashboard.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.labajada.app.domain.model.Dish
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTabContent(
    viewModel: RestaurantDashboardViewModel,
    platillosDelDia: List<Dish>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(items = platillosDelDia, key = { _, platillo -> platillo.id }) { index, platillo ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.StartToEnd) {
                        // ◄ CORREGIDO: Llamamos a la función preparada para la eliminación del plato
                        viewModel.prepararEliminacionPlatillo(index, platillo.id)
                        viewModel.toggleDeleteDialog(true)
                    } else if (value == SwipeToDismissBoxValue.EndToStart) {
                        // ◄ CORREGIDO: Llama a la lógica centralizada del ViewModel
                        viewModel.prepararEdicionPlatillo(index, platillo)
                        viewModel.toggleFormSheet(true)
                    }
                    false
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    val color by animateColorAsState(
                        targetValue = when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.StartToEnd -> Color(0xFFD32F2F)
                            SwipeToDismissBoxValue.EndToStart -> Color(0xFF1976D2)
                            else -> Color.Transparent
                        },
                        label = ""
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color, RoundedCornerShape(12.dp))
                            .padding(horizontal = 20.dp),
                        contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
                            Alignment.CenterStart else Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
                                Icons.Default.Delete else Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                content = {
                    DishRowItem(name = platillo.name, price = platillo.price, imageUrl = platillo.imagePath)
                }
            )
        }
    }
}
