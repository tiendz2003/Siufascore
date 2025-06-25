package com.jerry.ronaldo.siufascore.presentation.news

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.domain.model.Article
import com.jerry.ronaldo.siufascore.domain.usecase.news.GetNewsUseCase
import com.jerry.ronaldo.siufascore.utils.ConceptNews
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
) : BaseViewModel<NewsIntent, NewsUiState, NewsEffect>() {
    private val _uiState = MutableStateFlow(NewsUiState())
    override val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()
    private val _refreshTrigger = MutableStateFlow(Unit)
    private val _conceptUri = MutableStateFlow(ConceptNews.FOOTBALL.conceptUri)
    private val _selectedFilter = MutableStateFlow("Tất cả")

    private val filterToConceptMap = mapOf(
        "Tất cả" to ConceptNews.FOOTBALL.conceptUri,
        "Bóng đá" to ConceptNews.FOOTBALL.conceptUri,
        "Premier League" to ConceptNews.PREMIER_LEAGUE.conceptUri,
        "La Liga" to ConceptNews.LA_LIGA.conceptUri,
        "Serie A" to ConceptNews.SERIE_A.conceptUri,
        "Champions League" to ConceptNews.CHAMPIONS_LEAGUE.conceptUri
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val newsPagingData: Flow<PagingData<Article>> =
        combine(
            _conceptUri,
            _refreshTrigger,
            _selectedFilter
        ) { selectedConcept, _, selectedFilter ->
            Pair(selectedConcept, selectedFilter)
        }
            .distinctUntilChanged()
            .flatMapLatest { (selectedConcept, _) ->
                getNewsUseCase(selectedConcept)
            }.cachedIn(viewModelScope)

    override suspend fun processIntent(intent: NewsIntent) {
        when (intent) {
            is NewsIntent.RefreshData -> {
                refreshNews()
            }

            is NewsIntent.SetConcept -> {
                setConcept(intent.conceptName)
            }

            is NewsIntent.FilterNews -> {
                filterNews(intent.filter)
            }
        }
    }


    private suspend fun refreshNews() {
        _refreshTrigger.emit(Unit)
    }

    private suspend fun setConcept(conceptName: String) {
        _conceptUri.emit(conceptName)
    }
    private suspend fun filterNews(filterName: String) {
        _selectedFilter.emit(filterName)

        val conceptUri = filterToConceptMap[filterName] ?: ConceptNews.FOOTBALL.conceptUri
        _conceptUri.emit(conceptUri)

        _uiState.update {
            it.copy(
                selectedFilter = filterName,
                selectedConceptUri = conceptUri
            )
        }
    }
    fun getAvailableFilters(): List<String> {
        return filterToConceptMap.keys.toList()
    }
}