package com.jerry.ronaldo.siufascore.presentation.news

import com.jerry.ronaldo.siufascore.base.Intent
import com.jerry.ronaldo.siufascore.base.SingleEvent
import com.jerry.ronaldo.siufascore.base.ViewState
import com.jerry.ronaldo.siufascore.domain.model.Article
import com.jerry.ronaldo.siufascore.utils.ConceptNews

data class NewsUiState(
    val selectedConceptUri: String = ConceptNews.FOOTBALL.conceptUri,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val detailArticle: Article? = null,
    val selectedFilter: String = "Tất cả"
) : ViewState


sealed class NewsDetailIntent : Intent {
    data object LoadDetailNews : NewsDetailIntent()
}

sealed class NewsDetailEffect : SingleEvent {
    data class ShowError(val error: String) : NewsDetailEffect()
    data object NavigateBack : NewsDetailEffect()
}

sealed class NewsIntent : Intent {
    data class SetConcept(val conceptName: String) : NewsIntent()
    data object RefreshData : NewsIntent()
    data class FilterNews(val filter: String) : NewsIntent()
}

sealed class NewsEffect : SingleEvent {
    data class ShowError(val error: String) : NewsEffect()
    data class NavigateToDetailNews(val conceptUri: Int) : NewsEffect()
}