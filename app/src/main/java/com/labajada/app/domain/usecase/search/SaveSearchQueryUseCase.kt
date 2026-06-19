package com.labajada.app.domain.usecase.search

import com.labajada.app.data.local.entity.SearchHistoryEntity
import com.labajada.app.domain.repository.LocalDishRepository

class SaveSearchQueryUseCase(private val repository: LocalDishRepository) {

    suspend operator fun invoke(queryText: String) {
        val cleanQuery = queryText.trim()

        if (cleanQuery.isNotBlank() && cleanQuery.length >= 2) {
            val entity = SearchHistoryEntity(searchQuery = cleanQuery)
            repository.saveSearchQuery(entity)
        }
    }
}
