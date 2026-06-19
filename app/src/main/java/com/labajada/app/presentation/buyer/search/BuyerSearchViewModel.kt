package com.labajada.app.presentation.buyer.search

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.data.local.AppDatabase
import com.labajada.app.data.repository.LocalDishRepositoryImpl
import com.labajada.app.domain.usecase.search.GetRecentSearchHistoryUseCase
import com.labajada.app.domain.usecase.search.ManageFavoriteDishUseCase
import com.labajada.app.domain.usecase.search.SaveSearchQueryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BuyerSearchViewModel(context: Context) : ViewModel() {

    private val database = AppDatabase.getDatabase(context)
    private val repository = LocalDishRepositoryImpl(database.dishDao())

    private val saveSearchQueryUseCase = SaveSearchQueryUseCase(repository)
    private val getRecentSearchHistoryUseCase = GetRecentSearchHistoryUseCase(repository)
    private val manageFavoriteDishUseCase = ManageFavoriteDishUseCase(repository)

    var searchQuery = mutableStateOf("")

    val searchHistory = getRecentSearchHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val platosFavoritosRoom = manageFavoriteDishUseCase.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun ejecutarBusqueda() {
        viewModelScope.launch {
            saveSearchQueryUseCase(searchQuery.value)
        }
    }

    fun borrarTodoElHistorial() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun agregarPlatoAFavoritos(nombre: String, precio: String) {
        viewModelScope.launch {
            manageFavoriteDishUseCase.add(nombre, precio)
        }
    }

    fun quitarPlatoDeFavoritos(id: String) {
        viewModelScope.launch {
            manageFavoriteDishUseCase.remove(id)
        }
    }
}
