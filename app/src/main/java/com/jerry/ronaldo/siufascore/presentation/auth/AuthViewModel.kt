package com.jerry.ronaldo.siufascore.presentation.auth

import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.domain.usecase.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) :
    BaseViewModel<AuthIntent, AuthUiState, AuthUiEvent>() {
    private val _uiState = MutableStateFlow(AuthUiState())
    override val uiState: StateFlow<AuthUiState>
        get() = _uiState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun updateState(update: (AuthUiState) -> AuthUiState) {
        _uiState.value = update(_uiState.value)
    }

    private fun observeAuthState() {
        updateState { it.copy(isLoading = true) }
        viewModelScope.launch {
            authUseCases.observeAuthState().collect { user ->
                updateState { currentState ->
                    currentState.copy(
                        isAuthenticated = user != null,
                        user = user,
                        isLoading = false
                    )
                }

              /*  // Navigate based on auth state
                if (user != null) {
                    sendEvent(AuthUiEvent.NavigateToHome)
                } else {
                    sendEvent(AuthUiEvent.NavigateToSignIn)
                }*/
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }

            val result = authUseCases.signOut()

            if (result.isFailure) {
                updateState {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message
                    )
                }
                sendEvent(AuthUiEvent.ShowError("Failed to sign out"))
            } else {
                updateState {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        user = null
                    )
                }
                sendEvent(AuthUiEvent.NavigateToSignIn)
            }
        }
    }

    fun sendEmailVerification() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }

            val result = authUseCases.sendEmailVerification()

            updateState { it.copy(isLoading = false) }

            if (result.isSuccess) {
                sendEvent(AuthUiEvent.ShowSuccess("Verification email sent"))
            } else {
                val error = result.exceptionOrNull()?.message ?: "Failed to send verification email"
                sendEvent(AuthUiEvent.ShowError(error))
            }
        }
    }

    fun clearError() {
        updateState { it.copy(error = null) }
    }

}