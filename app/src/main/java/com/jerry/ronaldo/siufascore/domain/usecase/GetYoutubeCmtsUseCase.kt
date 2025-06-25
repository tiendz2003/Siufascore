package com.jerry.ronaldo.siufascore.domain.usecase

import com.jerry.ronaldo.siufascore.domain.repository.HighlightRepository
import javax.inject.Inject

class GetYoutubeCmtsUseCase @Inject constructor(
    private val highLightsRepository: HighlightRepository
) {
    operator fun invoke(videoId: String) = highLightsRepository.getVideoComments(videoId = videoId)
}