package com.jerry.ronaldo.siufascore.domain.usecase

import com.jerry.ronaldo.siufascore.domain.repository.NewsRepository
import javax.inject.Inject

class GetDetailNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(newsUri: String) = newsRepository.getDetailNews(newsUri)
}