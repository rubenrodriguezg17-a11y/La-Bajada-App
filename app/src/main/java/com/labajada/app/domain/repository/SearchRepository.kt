package com.labajada.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun saveSearchQuery(query: String)
    fun getSearchHistory(): Flow<List<String>>
    suspend fun clearHistory()
}