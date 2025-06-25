package com.jerry.ronaldo.siufascore.domain.usecase.news

import com.jerry.ronaldo.siufascore.domain.repository.NewsRepository
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    operator fun invoke(
        conceptUri: String,
    ) = newsRepository.getNews(conceptUri)
}