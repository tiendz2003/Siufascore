package com.jerry.ronaldo.siufascore.presentation.auth

// presentation/viewmodel/SignInViewModel.kt
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.domain.usecase.auth.AuthUseCases
import com.jerry.ronaldo.siufascore.utils.AuthException
import com.jerry.ronaldo.siufascore.utils.AuthResult
import com.jerry.ronaldo.siufascore.utils.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : BaseViewModel<AuthIntent, SignInUiState, AuthUiEvent>() {
    private val _uiState = MutableStateFlow(SignInUiState())
    override val uiState: StateFlow<SignInUiState>
        get() = _uiState.asStateFlow()

    private fun updateState(update: (SignInUiState) -> SignInUiState) {
        _uiState.value = update(_uiState.value)
    }

    override suspend fun processIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UpdateEmail -> updateEmail(intent.email)
            is AuthIntent.UpdatePassword -> updatePassword(intent.password)
            is AuthIntent.TogglePasswordVisibility -> togglePasswordVisibility()
            is AuthIntent.SignInWithEmail -> signInWithEmail()
            is AuthIntent.SignInWithGoogle -> signInWithGoogle(
                intent.context
            )
            is AuthIntent.SignInWithFacebook -> signInWithFacebook()
            is AuthIntent.SendPasswordReset -> sendPasswordReset(intent.email)
            is AuthIntent.ClearError -> clearError()
            is AuthIntent.NavigateToSignUp -> sendEvent(AuthUiEvent.NavigateToSignUp)
            else -> {}
        }
    }

    private fun updateEmail(email: String) {
        val validation = validateEmail(email)
        updateState { currentState ->
            currentState.copy(
                email = email,
                emailValidation = validation,
                isFormValid = validation.isValid && currentState.passwordValidation.isValid
            )
        }
    }

    private fun updatePassword(password: String) {
        val validation = validatePassword(password)
        updateState { currentState ->
            currentState.copy(
                password = password,
                passwordValidation = validation,
                isFormValid = currentState.emailValidation.isValid && validation.isValid
            )
        }
    }

    private fun togglePasswordVisibility() {
        updateState {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    private fun signInWithEmail() {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (!currentState.isFormValid) {
                sendEvent(AuthUiEvent.ShowError("Please fill in all fields correctly"))
                return@launch
            }

            updateState { it.copy(isLoading = true, error = null) }

            val result = authUseCases.signInWithEmail(
                currentState.email,
                currentState.password
            )

            updateState { it.copy(isLoading = false) }

            when (result) {
                is AuthResult.AuthenticatedSuccess -> {
                    if (!result.user.isEmailVerified) {
                        sendEvent(AuthUiEvent.ShowEmailVerificationDialog)
                    } else {
                        sendEvent(AuthUiEvent.NavigateToHome)
                    }
                }

                is AuthResult.Error -> {
                    val errorMessage = mapAuthExceptionToMessage(result.exception)
                    updateState { it.copy(error = errorMessage , isLoading = false) }
                    sendEvent(AuthUiEvent.ShowError(errorMessage))
                }

                AuthResult.Loading -> {
                    updateState { it.copy(isLoading = true) }
                }

                AuthResult.Unauthenticated -> {
                    sendEvent(AuthUiEvent.NavigateToSignUp)
                }
            }
        }
    }


    private fun signInWithGoogle(context:Context) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }

            val result = authUseCases.signInWithGoogle(context)

            updateState { it.copy(isLoading = false) }

            when (result) {
                is AuthResult.AuthenticatedSuccess -> {
                    sendEvent(AuthUiEvent.NavigateToHome)
                }

                is AuthResult.Error -> {
                    val errorMessage = mapAuthExceptionToMessage(result.exception)
                    updateState { it.copy(error = errorMessage, isLoading = false) }
                    Timber.tag("SignInViewModel").e("Google Sign-In Error: $errorMessage")
                    sendEvent(AuthUiEvent.ShowError(errorMessage))
                }

                AuthResult.Loading -> {
                    updateState { it.copy(isLoading = true) }
                }

                AuthResult.Unauthenticated -> {
                    sendEvent(AuthUiEvent.NavigateToSignUp)
                }
            }
        }
    }

    private fun signInWithFacebook() {
        // This will be called after getting the access token from Facebook
        // The actual Facebook Sign-In flow is handled in the UI layer
    }

    /*fun handleFacebookSignInResult(accessToken: String) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }

            val result = authUseCases.signInWithFacebook(accessToken)

            updateState { it.copy(isLoading = false) }

            when (result) {
                is AuthResult.AuthenticatedSuccess -> {
                    sendEvent(AuthUiEvent.NavigateToHome)
                }

                is AuthResult.Error -> {
                    val errorMessage = mapAuthExceptionToMessage(result.exception)
                    updateState { it.copy(error = errorMessage) }
                    sendEvent(AuthUiEvent.ShowError(errorMessage))
                }

                AuthResult.Loading -> {
                    updateState { it.copy(isLoading = true) }
                }
            }
        }
    }*/

    private fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }

            val result = authUseCases.sendPasswordReset(email)

            updateState { it.copy(isLoading = false) }

            if (result.isSuccess) {
                sendEvent(AuthUiEvent.ShowSuccess("Password reset email sent"))
            } else {
                val error = result.exceptionOrNull()?.message ?: "Failed to send reset email"
                sendEvent(AuthUiEvent.ShowError(error))
            }
        }
    }

    private fun clearError() {
        updateState { it.copy(error = null) }
    }

    private fun validateEmail(email: String): ValidationState {
        val result = AuthValidator.validateEmail(email)
        return if (result.isValid) {
            ValidationState.success()
        } else {
            ValidationState.error(result.errorMessage ?: "Invalid email")
        }
    }

    private fun validatePassword(password: String): ValidationState {
        return if (password.isNotEmpty()) {
            ValidationState.success()
        } else {
            ValidationState.error("Password is required")
        }
    }

    private fun mapAuthExceptionToMessage(exception: AuthException): String {
        return when (exception) {
            is AuthException.InvalidCredentials -> "Invalid email or password"
            is AuthException.UserNotFound -> "No account found with this email"
            is AuthException.NetworkError -> "Network error. Please check your connection"
            is AuthException.CancelledByUser -> "Sign in was cancelled"
            is AuthException.Unknown -> exception.message
            else -> "An error occurred. Please try again"
        }
    }

}