package com.jerry.ronaldo.siufascore.presentation.auth

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
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : BaseViewModel<AuthIntent, SignUpUiState, AuthUiEvent>() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    override val uiState: StateFlow<SignUpUiState>
        get() = _uiState.asStateFlow()

    private fun getCurrentState() = _uiState.value
    private fun updateState(update: (SignUpUiState) -> SignUpUiState) {
        _uiState.value = update(_uiState.value)
    }

    override suspend fun processIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UpdateEmail -> updateEmail(intent.email)
            is AuthIntent.UpdatePassword -> updatePassword(intent.password)
            is AuthIntent.UpdateConfirmPassword -> updateConfirmPassword(intent.confirmPassword)
            is AuthIntent.TogglePasswordVisibility -> togglePasswordVisibility()
            is AuthIntent.ToggleConfirmPasswordVisibility -> toggleConfirmPasswordVisibility()
            is AuthIntent.SignUpWithEmail -> signUpWithEmail()
            is AuthIntent.ClearError -> clearError()
            else -> {}
        }
    }

    private fun updateEmail(email: String) {
        val validation = validateEmail(email)
        updateState { currentState ->
            currentState.copy(
                email = email,
                emailValidation = validation,
                isFormValid = isFormValid(
                    emailValid = validation.isValid,
                    passwordValid = currentState.passwordValidation.isValid,
                    confirmPasswordValid = currentState.confirmPasswordValidation.isValid
                )
            )
        }
    }

    private fun updatePassword(password: String) {
        val validation = validatePassword(password)
        val confirmPasswordValidation = if (getCurrentState().confirmPassword.isNotEmpty()) {
            validateConfirmPassword(password, getCurrentState().confirmPassword)
        } else {
            getCurrentState().confirmPasswordValidation
        }

        updateState { currentState ->
            currentState.copy(
                password = password,
                passwordValidation = validation,
                confirmPasswordValidation = confirmPasswordValidation,
                isFormValid = isFormValid(
                    emailValid = currentState.emailValidation.isValid,
                    passwordValid = validation.isValid,
                    confirmPasswordValid = confirmPasswordValidation.isValid
                )
            )
        }
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        val validation = validateConfirmPassword(getCurrentState().password, confirmPassword)
        updateState { currentState ->
            currentState.copy(
                confirmPassword = confirmPassword,
                confirmPasswordValidation = validation,
                isFormValid = isFormValid(
                    emailValid = currentState.emailValidation.isValid,
                    passwordValid = currentState.passwordValidation.isValid,
                    confirmPasswordValid = validation.isValid
                )
            )
        }
    }

    private fun togglePasswordVisibility() {
        updateState {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    private fun toggleConfirmPasswordVisibility() {
        updateState {
            it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible)
        }
    }

    private fun signUpWithEmail() {
        viewModelScope.launch {
            val currentState = getCurrentState()

            if (!currentState.isFormValid) {
                sendEvent(AuthUiEvent.ShowError("Please fill in all fields correctly"))
                return@launch
            }

            updateState { it.copy(isLoading = true, error = null) }

            val result = authUseCases.signUpWithEmail(
                currentState.email,
                currentState.password
            )

            updateState { it.copy(isLoading = false) }

            when (result) {
                is AuthResult.AuthenticatedSuccess -> {
                    updateState { it.copy(isSignUpSuccess = true) }
                    sendEvent(AuthUiEvent.ShowSuccess("Account created successfully! Please verify your email."))
                    sendEvent(AuthUiEvent.ShowEmailVerificationDialog)
                }

                is AuthResult.Error -> {
                    val errorMessage = mapAuthExceptionToMessage(result.exception)
                    updateState { it.copy(error = errorMessage) }
                    sendEvent(AuthUiEvent.ShowError(errorMessage))
                }

                AuthResult.Loading -> {
                    updateState { it.copy(isLoading = true) }
                }
                AuthResult.Unauthenticated -> {

                }
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
        val result = AuthValidator.validatePassword(password)
        return if (result.isValid) {
            ValidationState.success()
        } else {
            ValidationState.error(result.errorMessage ?: "Password requirements not met")
        }
    }

    private fun validateConfirmPassword(
        password: String,
        confirmPassword: String
    ): ValidationState {
        val result = AuthValidator.validateConfirmPassword(password, confirmPassword)
        return if (result.isValid) {
            ValidationState.success()
        } else {
            ValidationState.error(result.errorMessage ?: "Passwords do not match")
        }
    }

    private fun isFormValid(
        emailValid: Boolean,
        passwordValid: Boolean,
        confirmPasswordValid: Boolean
    ): Boolean {
        return emailValid && passwordValid && confirmPasswordValid
    }

    private fun mapAuthExceptionToMessage(exception: AuthException): String {
        return when (exception) {
            is AuthException.EmailAlreadyInUse -> "An account with this email already exists"
            is AuthException.WeakPassword -> "Password is too weak. Please choose a stronger password"
            is AuthException.InvalidCredentials -> "Invalid email address"
            is AuthException.NetworkError -> "Network error. Please check your connection"
            is AuthException.Unknown -> exception.message
            else -> "An error occurred. Please try again"
        }
    }

}