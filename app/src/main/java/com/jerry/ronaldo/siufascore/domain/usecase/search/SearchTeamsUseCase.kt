package com.jerry.ronaldo.siufascore.domain.usecase.search

import com.jerry.ronaldo.siufascore.domain.repository.SearchRepository
import javax.inject.Inject

class SearchTeamsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String) = repository.searchTeams(query)
}