package com.labajada.app.data.mapper

import com.labajada.app.data.local.entity.BuyerEntity
import com.labajada.app.domain.model.Buyer

fun Buyer.toEntity() = BuyerEntity(
    id = id,
    email = email,
    name = name,
    phone = phone,
    password = password
)

fun BuyerEntity.toDomain() = Buyer(
    id = id,
    email = email,
    name = name,
    phone = phone,
    password = password
)