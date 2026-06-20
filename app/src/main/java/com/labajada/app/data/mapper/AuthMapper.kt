package com.labajada.app.data.mapper

import com.labajada.app.data.local.entity.BuyerEntity
import com.labajada.app.data.local.entity.DishEntity
import com.labajada.app.data.local.entity.RestaurantEntity
import com.labajada.app.data.local.entity.SessionEntity
import com.labajada.app.domain.model.Buyer
import com.labajada.app.domain.model.Dish
import com.labajada.app.domain.model.Restaurant
import com.labajada.app.domain.model.Session

// Buyer
fun Buyer.toEntity() = BuyerEntity(
    id = id,
    email = email,
    name = name,
    phone = phone,
    departamento = departamento,
    provincia = provincia,
    password = password
)

fun BuyerEntity.toDomain() = Buyer(
    id = id,
    email = email,
    name = name,
    phone = phone,
    departamento = departamento,
    provincia = provincia,
    password = password
)

// Restaurant
fun Restaurant.toEntity() = RestaurantEntity(
    id = id,
    email = email,
    restaurantName = restaurantName,
    rucNumber = rucNumber,
    phoneNumber = phoneNumber,
    selectedCategory = selectedCategory,
    addressDetails = addressDetails,
    latitude = latitude,
    longitude = longitude,
    password = password
)

fun RestaurantEntity.toDomain() = Restaurant(
    id = id,
    email = email,
    restaurantName = restaurantName,
    rucNumber = rucNumber,
    phoneNumber = phoneNumber,
    selectedCategory = selectedCategory,
    addressDetails = addressDetails,
    latitude = latitude,
    longitude = longitude,
    password = password
)

// Session
fun Session.toEntity() = SessionEntity(
    id = id,
    userId = userId,
    email = email,
    role = role
)

fun SessionEntity.toDomain() = Session(
    id = id,
    userId = userId,
    email = email,
    role = role
)

fun Dish.toEntity() = DishEntity(
    id = id,
    restaurantId = restaurantId,
    name = name,
    price = price,
    imagePath = imagePath
)

fun DishEntity.toDomain() = Dish(
    id = id,
    restaurantId = restaurantId,
    name = name,
    price = price,
    imagePath = imagePath
)