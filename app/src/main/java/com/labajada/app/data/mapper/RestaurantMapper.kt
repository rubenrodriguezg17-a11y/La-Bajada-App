package com.labajada.app.data.mapper

import com.labajada.app.data.local.entity.RestaurantEntity
import com.labajada.app.domain.model.Restaurant

fun Restaurant.toEntity() = RestaurantEntity(
    id = id,
    email = email,
    password = password,
    restaurantName = restaurantName,
    rucNumber = rucNumber,
    phoneNumber = phoneNumber,
    selectedCategory = selectedCategory,
    addressDetails = addressDetails,
    latitude = latitude,
    longitude = longitude,
    offersDelivery = offersDelivery,
    maxDeliveryDistanceKm = maxDeliveryDistanceKm,
    imageUrl = imageUrl,
    isOpen = isOpen,
    businessHours = businessHours
)

fun RestaurantEntity.toDomain() = Restaurant(
    id = id,
    email = email,
    password = password,
    restaurantName = restaurantName,
    rucNumber = rucNumber,
    phoneNumber = phoneNumber,
    selectedCategory = selectedCategory,
    addressDetails = addressDetails,
    latitude = latitude,
    longitude = longitude,
    offersDelivery = offersDelivery,
    maxDeliveryDistanceKm = maxDeliveryDistanceKm,
    imageUrl = imageUrl,
    isOpen = isOpen,
    businessHours = businessHours
)