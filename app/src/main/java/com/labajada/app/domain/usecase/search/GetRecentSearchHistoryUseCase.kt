package com.labajada.app.domain.usecase.search

import com.labajada.app.data.local.entity.SearchHistoryEntity
import com.labajada.app.domain.repository.LocalDishRepository
import kotlinx.coroutines.flow.Flow

class GetRecentSearchHistoryUseCase(private val repository: LocalDishRepository) {
    operator fun invoke(): Flow<List<SearchHistoryEntity>> {
        return repository.getSearchHistory()
    }
}
