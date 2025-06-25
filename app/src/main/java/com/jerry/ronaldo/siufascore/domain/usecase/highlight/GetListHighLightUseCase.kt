package com.jerry.ronaldo.siufascore.domain.usecase.highlight

import com.jerry.ronaldo.siufascore.domain.repository.HighlightRepository
import javax.inject.Inject

class GetListHighLightUseCase @Inject constructor(
    private val highlightRepository: HighlightRepository
) {
    operator fun invoke(query: String) = highlightRepository.getPlayListVideo(query)
}