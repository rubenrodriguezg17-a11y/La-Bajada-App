package com.labajada.app.data.repository

import com.labajada.app.data.local.dao.RestaurantDao
import com.labajada.app.data.local.entity.FavoriteRestaurantEntity
import com.labajada.app.data.mapper.toDomain
import com.labajada.app.data.mapper.toEntity
import com.labajada.app.domain.model.FavoriteRestaurant
import com.labajada.app.domain.model.Restaurant
import com.labajada.app.domain.repository.RestaurantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RestaurantRepositoryImpl(
    private val restaurantDao: RestaurantDao
) : RestaurantRepository {

    override suspend fun saveFavoriteRestaurant(
        restaurantId: String,
        restaurantName: String,
        category: String,
        address: String,
        timestamp: Long
    ) {
        restaurantDao.insertFavoriteRestaurant(
            FavoriteRestaurantEntity(
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                category = category,
                address = address,
                timestamp = timestamp
            )
        )
    }

    override fun getFavoriteRestaurants(): Flow<List<FavoriteRestaurant>> {
        return restaurantDao.getAllFavoriteRestaurants().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun removeFavoriteRestaurant(restaurantId: String) {
        restaurantDao.deleteFavoriteRestaurantById(restaurantId)
    }

    override fun getRestaurantById(restaurantId: String): Flow<Restaurant?> {
        return restaurantDao.getRestaurantById(restaurantId).map { it?.toDomain() }
    }

    override suspend fun updateRestaurantProfile(restaurant: Restaurant) {
        restaurantDao.updateRestaurantProfile(restaurant.toEntity())
    }

    override fun getAllRestaurants(): Flow<List<Restaurant>> {
        return restaurantDao.getAllRestaurants().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}