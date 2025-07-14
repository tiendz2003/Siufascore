package com.jerry.ronaldo.siufascore.domain.usecase

import com.jerry.ronaldo.siufascore.domain.repository.HighlightRepository
import javax.inject.Inject

class GetVideoDetailsInfoUseCase @Inject constructor(
    private val highlightRepository: HighlightRepository
) {
    operator fun invoke(videoId:String) = highlightRepository.getVideoDetailsInfo(videoId)
}