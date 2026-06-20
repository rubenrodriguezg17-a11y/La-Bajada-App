package com.labajada.app.presentation.buyer.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.usecase.search.GetRecentSearchHistoryUseCase
import com.labajada.app.domain.usecase.search.ManageFavoriteDishUseCase
import com.labajada.app.domain.usecase.search.SaveSearchQueryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BuyerSearchViewModel(
    private val saveSearchQueryUseCase: SaveSearchQueryUseCase,
    private val getRecentSearchHistoryUseCase: GetRecentSearchHistoryUseCase,
    private val manageFavoriteDishUseCase: ManageFavoriteDishUseCase
) : ViewModel() {

    // Cambiado a MutableStateFlow para mantener la consistencia arquitectónica
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchHistory = getRecentSearchHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val platosFavoritosRoom = manageFavoriteDishUseCase.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(value: String) {
        _searchQuery.update { value }
    }

    fun ejecutarBusqueda() {
        viewModelScope.launch {
            if (_searchQuery.value.isNotBlank()) {
                saveSearchQueryUseCase(_searchQuery.value)
            }
        }
    }

    fun borrarTodoElHistorial() {
        viewModelScope.launch {
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
