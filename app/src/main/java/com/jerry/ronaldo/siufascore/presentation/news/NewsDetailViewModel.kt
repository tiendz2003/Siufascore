package com.jerry.ronaldo.siufascore.presentation.news

import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.domain.usecase.news.GetDetailNewsUseCase
import com.jerry.ronaldo.siufascore.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = NewsDetailViewModel.Factory::class)
class NewsDetailViewModel @AssistedInject constructor(
    private val getDetailNewsUseCase: GetDetailNewsUseCase,
    @Assisted val conceptUri: String
) : BaseViewModel<NewsDetailIntent, NewsUiState, NewsDetailEffect>() {
    private val _uiState = MutableStateFlow(NewsUiState())
    override val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()
    override suspend fun processIntent(intent: NewsDetailIntent) {
        when (intent) {
            NewsDetailIntent.LoadDetailNews -> {
                loadDetailNews()
            }
        }
    }

    private fun loadDetailNews() {
        viewModelScope.launch {
            when (val detailArticle = getDetailNewsUseCase(conceptUri)) {
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = detailArticle.exception.message,
                        isLoading = false
                    )
                }

                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                }

                is Resource.Success -> {
                    _uiState.value =
                        _uiState.value.copy(detailArticle = detailArticle.data, isLoading = false)
                }
            }
        }
    }


    @AssistedFactory
    interface Factory {
        fun create(conceptUri: String): NewsDetailViewModel
    }
}