package com.labajada.app.presentation.buyer.search

data class RadarHuarique(
    val id: String,
    val nombre: String,
    val category: String,
    val precioPromedio: Double,
    val distancia: String,
    val latitud: Double,
    val longitud: Double
)