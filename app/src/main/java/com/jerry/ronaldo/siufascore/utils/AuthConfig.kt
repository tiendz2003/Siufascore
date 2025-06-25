package com.jerry.ronaldo.siufascore.utils

object AuthConfig {
    const val PASSWORD_MIN_LENGTH = 6
    const val EMAIL_VERIFICATION_REQUIRED = true

    // These should be loaded from BuildConfig or secure storage
    const val GOOGLE_WEB_CLIENT_ID = "YOUR_GOOGLE_WEB_CLIENT_ID"
    const val FACEBOOK_APP_ID = "YOUR_FACEBOOK_APP_ID"
}

// domain/validation/AuthValidator.kt
object AuthValidator {

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error("Email cannot be empty")
            !isValidEmailFormat(email) -> ValidationResult.Error("Invalid email format")
            else -> ValidationResult.Success
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error("Password cannot be empty")
            password.length < 6 -> ValidationResult.Error("Password must be at least 6 characters")
            !password.any { it.isDigit() } -> ValidationResult.Error("Password must contain at least one digit")
            !password.any { it.isLetter() } -> ValidationResult.Error("Password must contain at least one letter")
            else -> ValidationResult.Success
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isBlank() -> ValidationResult.Error("Please confirm your password")
            password != confirmPassword -> ValidationResult.Error("Passwords do not match")
            else -> ValidationResult.Success
        }
    }

    private fun isValidEmailFormat(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

// domain/validation/ValidationResult.kt
sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()

    val isValid: Boolean
        get() = this is Success

    val errorMessage: String?
        get() = if (this is Error) message else null
}