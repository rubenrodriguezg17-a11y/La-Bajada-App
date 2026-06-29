package com.labajada.app.presentation.buyer.search

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.labajada.app.domain.model.Dish
import com.labajada.app.domain.model.FavoriteRestaurant
import com.labajada.app.domain.repository.AuthRepository
import com.labajada.app.domain.repository.DishRepository
import com.labajada.app.domain.repository.RestaurantRepository
import com.labajada.app.domain.usecase.auth.GetActiveBuyerUseCase
import com.labajada.app.domain.usecase.search.ClearSearchHistoryUseCase
import com.labajada.app.domain.usecase.search.GetAllDishesUseCase
import com.labajada.app.domain.usecase.search.GetRecentSearchHistoryUseCase
import com.labajada.app.domain.usecase.search.ManageFavoriteRestaurantUseCase
import com.labajada.app.domain.usecase.search.SaveSearchQueryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class BuyerSearchViewModel(
    private val saveSearchQueryUseCase: SaveSearchQueryUseCase,
    private val getRecentSearchHistoryUseCase: GetRecentSearchHistoryUseCase,
    private val manageFavoriteRestaurantUseCase: ManageFavoriteRestaurantUseCase,
    private val getActiveBuyerUseCase: GetActiveBuyerUseCase,       // ← nuevo
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase, // ← nuevo
    private val getAllDishesUseCase: GetAllDishesUseCase,             // ← nuevo
    private val dishRepository: DishRepository,
    private val restaurantRepository: RestaurantRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _platosEncontrados = MutableStateFlow<List<Dish>>(emptyList())
    val platosEncontrados: StateFlow<List<Dish>> = _platosEncontrados.asStateFlow()

    private val _userLocation = MutableStateFlow(LatLng(-8.1116, -79.0287))
    val userLocation: StateFlow<LatLng> = _userLocation.asStateFlow()

    private val _currentBuyerName = MutableStateFlow("")
    val currentBuyerName: StateFlow<String> = _currentBuyerName.asStateFlow()

    private val _currentBuyerEmail = MutableStateFlow("")
    val currentBuyerEmail: StateFlow<String> = _currentBuyerEmail.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _restaurantIdParaMenu = MutableStateFlow<String?>(null)

    val menuDelHuariqueSeleccionado: StateFlow<List<Dish>> = _restaurantIdParaMenu
        .flatMapLatest { id ->
            if (id != null) dishRepository.getMenuOfTheDay(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun abrirMenuDeHuarique(restaurantId: String) {
        _restaurantIdParaMenu.value = restaurantId
    }

    fun cerrarMenuDeHuarique() {
        _restaurantIdParaMenu.value = null
    }

    val searchHistory = getRecentSearchHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val restaurantesFavoritosRoom: StateFlow<List<FavoriteRestaurant>> =
        manageFavoriteRestaurantUseCase.getAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val huariquesDesdeBaseDeDatos = restaurantRepository.getAllRestaurants()
        .combine(_userLocation) { listaRestaurants, ubicacionActual ->
            listaRestaurants.map { restaurant ->
                RadarHuarique(
                    id = restaurant.id.toString(),
                    nombre = restaurant.restaurantName,
                    category = restaurant.selectedCategory,
                    precioPromedio = 15.0,
                    distancia = calcularDistanciaReal(
                        ubicacionActual.latitude,
                        ubicacionActual.longitude,
                        restaurant.latitude,
                        restaurant.longitude
                    ),
                    latitud = restaurant.latitude,
                    longitud = restaurant.longitude
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            val buyer = getActiveBuyerUseCase()  // ← resuelve sesión + busca buyer en un paso
            if (buyer != null) {
                _currentBuyerName.value = buyer.name
                _currentBuyerEmail.value = buyer.email
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun rastrearUbicacionActual(context: Context) {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    _userLocation.value = LatLng(location.latitude, location.longitude)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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

    fun agregarRestauranteAFavoritos(id: String, nombre: String, categoria: String, direccion: String) {
        viewModelScope.launch {
            manageFavoriteRestaurantUseCase.add(id, nombre, categoria, direccion)
        }
    }

    fun quitarRestauranteDeFavoritos(id: String) {
        viewModelScope.launch {
            manageFavoriteRestaurantUseCase.remove(id)
        }
    }

    fun borrarTodoElHistorial() {
        viewModelScope.launch {
            clearSearchHistoryUseCase()  // ← resuelto
        }
    }

    fun ejecutarBusquedaInteligente() {
        val query = _searchQuery.value.trim().lowercase(java.util.Locale.ROOT)
        if (query.isBlank()) return

        viewModelScope.launch {
            try {
                saveSearchQueryUseCase(query)
                val todosLosPlatos = getAllDishesUseCase()  // ← resuelto
                val platosFiltrados = todosLosPlatos.filter { plato ->
                    val nombrePlato = plato.name.lowercase(java.util.Locale.ROOT)
                    nombrePlato.contains(query) || calcularSimilitudTexto(nombrePlato, query) >= 0.6
                }
                _platosEncontrados.value = platosFiltrados
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun calcularDistanciaReal(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
        val radioTierra = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distanciaEnKm = radioTierra * c
        return if (distanciaEnKm < 1) "${(distanciaEnKm * 1000).toInt()} metros"
        else String.format(java.util.Locale.US, "%.1f km", distanciaEnKm)
    }

    private fun calcularSimilitudTexto(s1: String, s2: String): Double {
        val longitudMaxima = maxOf(s1.length, s2.length)
        if (longitudMaxima == 0) return 1.0
        val costo = IntArray(s2.length + 1) { it }
        for (i in 1..s1.length) {
            var anterior = costo[0]
            costo[0] = i
            for (j in 1..s2.length) {
                val temp = costo[j]
                val match = if (s1[i - 1] == s2[j - 1]) 0 else 1
                costo[j] = minOf(costo[j] + 1, costo[j - 1] + 1, anterior + match)
                anterior = temp
            }
        }
        return (longitudMaxima - costo[s2.length]).toDouble() / longitudMaxima
    }
}