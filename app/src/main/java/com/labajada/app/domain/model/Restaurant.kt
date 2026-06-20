package com.labajada.app.domain.model

data class Restaurant(
    val id: Int = 0,
    val email: String,
    val restaurantName: String,
    val rucNumber: String,
    val phoneNumber: String,
    val selectedCategory: String,
    val addressDetails: String,
    val latitude: Double,
    val longitude: Double,
    val password: String
)