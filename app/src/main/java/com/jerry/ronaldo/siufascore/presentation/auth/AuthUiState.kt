package com.jerry.ronaldo.siufascore.presentation.auth

import android.content.Context
import com.jerry.ronaldo.siufascore.base.Intent
import com.jerry.ronaldo.siufascore.base.SingleEvent
import com.jerry.ronaldo.siufascore.base.ViewState
import com.jerry.ronaldo.siufascore.domain.model.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val email: String = "",
    val password: String = "",
    val emailValidation: ValidationState = ValidationState(),
    val passwordValidation: ValidationState = ValidationState(),
    val isFormValid: Boolean = false,
    val isPasswordVisible: Boolean = false
) : ViewState

sealed class AuthIntent : Intent {
    // Sign In Actions
    data class UpdateEmail(val email: String) : AuthIntent()
    data class UpdatePassword(val password: String) : AuthIntent()
    data class UpdateConfirmPassword(val confirmPassword: String) : AuthIntent()
    data object TogglePasswordVisibility : AuthIntent()
    data object ToggleConfirmPasswordVisibility : AuthIntent()

    // Authentication Actions
    data object SignInWithEmail : AuthIntent()
    data object SignUpWithEmail : AuthIntent()
    data class SignInWithGoogle(val context:Context) : AuthIntent()
    data object SignInWithFacebook : AuthIntent()
    data class SendPasswordReset(val email: String) : AuthIntent()

    // Navigation Actions
    data object NavigateToSignUp : AuthIntent()
    data object NavigateToSignIn : AuthIntent()
    data object NavigateToForgotPassword : AuthIntent()

    // Common Actions
    data object SignOut : AuthIntent()
    data object ClearError : AuthIntent()
    data object SendEmailVerification : AuthIntent()
}

sealed class AuthUiEvent : SingleEvent {
    data object NavigateToHome : AuthUiEvent()
    data object NavigateToSignIn : AuthUiEvent()
    data object NavigateToSignUp : AuthUiEvent()
    data object ShowEmailVerificationDialog : AuthUiEvent()
    data class ShowSnackbar(val message: String) : AuthUiEvent()
    data class ShowError(val message: String) : AuthUiEvent()
    data class ShowSuccess(val message: String) : AuthUiEvent()
}
data class ValidationState(
    val isValid: Boolean = false,
    val errorMessage: String? = null
) {
    companion object {
        fun success() = ValidationState(isValid = true)
        fun error(message: String) = ValidationState(isValid = false, errorMessage = message)
    }
}
data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailValidation: ValidationState = ValidationState(),
    val passwordValidation: ValidationState = ValidationState(),
    val isFormValid: Boolean = false,
    val error: String? = null,
    val isPasswordVisible: Boolean = false
):ViewState

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val emailValidation: ValidationState = ValidationState(),
    val passwordValidation: ValidationState = ValidationState(),
    val confirmPasswordValidation: ValidationState = ValidationState(),
    val isFormValid: Boolean = false,
    val error: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isSignUpSuccess: Boolean = false
):ViewState