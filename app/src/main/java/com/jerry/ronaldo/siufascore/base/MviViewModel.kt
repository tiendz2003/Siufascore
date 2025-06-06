package com.jerry.ronaldo.siufascore.base

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MviViewModel<I:Intent,S:ViewState,E:SingleEvent> {
    val uiState:StateFlow<S>
    val singleEvent:Flow<E>
    suspend fun processIntent(intent:I)
}