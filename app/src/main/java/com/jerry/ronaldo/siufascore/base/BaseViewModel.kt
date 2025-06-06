package com.jerry.ronaldo.siufascore.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel<I : Intent, S : ViewState, E : SingleEvent> : ViewModel(),
    MviViewModel<I, S, E> {
    private val intentFlow = MutableSharedFlow<I>(extraBufferCapacity = 64)
    private val eventChannel = Channel<E>(Channel.UNLIMITED)

    override val singleEvent: Flow<E>
        get() = eventChannel.receiveAsFlow()

    abstract override val uiState: StateFlow<S>

    override suspend fun processIntent(intent: I) {
        intentFlow.emit(intent)
    }
    fun sendIntent(intent:I){
        viewModelScope.launch {
            processIntent(intent)
        }
    }
    protected suspend fun sendEvent(event:E){
        eventChannel.trySend(event)
            .onFailure {
                Timber.e("Không thể gửi event:$event")
            }
    }
    protected val intent:SharedFlow<I> = intentFlow
    override fun onCleared() {
        super.onCleared()
        eventChannel.close()
    }
}
