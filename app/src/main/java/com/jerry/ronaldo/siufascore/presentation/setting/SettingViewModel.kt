package com.jerry.ronaldo.siufascore.presentation.setting

import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.domain.usecase.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authUseCase: AuthUseCases,
) : BaseViewModel<SettingIntent, SettingUiState, SettingEvent>() {
    private val _uiState = MutableStateFlow(SettingUiState())
    override val uiState: StateFlow<SettingUiState>
        get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadCurrentUser()
        }
    }

    private fun updateState(newState: (SettingUiState) -> SettingUiState) {
        _uiState.update(newState)
    }

    override suspend fun processIntent(intent: SettingIntent) {
        when (intent) {
            is SettingIntent.LoadUserInfo -> loadCurrentUser()
            is SettingIntent.Signout -> signOut()
            is SettingIntent.ToggleNotification -> {}
            is SettingIntent.UpdateUserInfo -> {

            }
        }
    }

    private suspend fun loadCurrentUser() {
        try {
            updateState { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
            val user = authUseCase.getCurrentUser()
            updateState { currentState ->
                currentState.copy(
                    userInfo = user,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            updateState { currentState ->
                currentState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Lỗi không rõ"
                )
            }
        }

    }

    private suspend fun signOut() {
        try {
            authUseCase.signOut.invoke().onSuccess {
                updateState { currentState ->
                    currentState.copy(
                        isLoading = false,
                        userInfo = null,
                        errorMessage = null
                    )
                }
                sendEvent(SettingEvent.SignoutSuccess)
            }.onFailure { e ->
                updateState { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Lỗi không rõ"
                    )
                }
            }

        } catch (e: Exception) {
            updateState { currentState ->
                currentState.copy(
                    errorMessage = e.message ?: "Lỗi không rõ"
                )
            }
        }
    }

    fun clearError() {
        updateState { currentState ->
            currentState.copy(
                errorMessage = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        clearError()
    }
}