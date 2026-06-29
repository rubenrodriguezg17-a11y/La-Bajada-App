package com.labajada.app.data.repository

import com.labajada.app.data.local.dao.SearchDao
import com.labajada.app.data.local.entity.SearchHistoryEntity
import com.labajada.app.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchRepositoryImpl(
    private val searchDao: SearchDao
) : SearchRepository {

    override suspend fun saveSearchQuery(query: String) {
        searchDao.insertSearchQuery(
            SearchHistoryEntity(
                searchQuery = query,  // ← corregido
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override fun getSearchHistory(): Flow<List<String>> {
        return searchDao.getRecentSearchHistory().map { entities ->
            entities.map { it.searchQuery }  // ← corregido
        }
    }

    override suspend fun clearHistory() {
        searchDao.clearAllSearchHistory()
    }
}