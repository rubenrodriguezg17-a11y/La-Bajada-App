package com.labajada.app.data.mapper

import com.labajada.app.data.local.entity.FavoriteRestaurantEntity
import com.labajada.app.domain.model.FavoriteRestaurant

fun FavoriteRestaurantEntity.toDomain() = FavoriteRestaurant(
    restaurantId = restaurantId,
    restaurantName = restaurantName,
    category = category,
    address = address,
    timestamp = timestamp
)

fun FavoriteRestaurant.toEntity() = FavoriteRestaurantEntity(
    restaurantId = restaurantId,
    restaurantName = restaurantName,
    category = category,
    address = address,
    timestamp = timestamp
)