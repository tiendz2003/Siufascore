package com.jerry.ronaldo.siufascore.domain.usecase.auth

// domain/usecase/SignInWithEmailUseCase.kt
import android.content.Context
import com.jerry.ronaldo.siufascore.data.remote.AuthRepository
import com.jerry.ronaldo.siufascore.domain.model.User
import com.jerry.ronaldo.siufascore.utils.AuthException
import com.jerry.ronaldo.siufascore.utils.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            // Input validation
            if (email.isBlank() || !isValidEmail(email)) {
                return@withContext AuthResult.Error(AuthException.InvalidCredentials)
            }

            if (password.isBlank() || password.length < 6) {
                return@withContext AuthResult.Error(AuthException.WeakPassword)
            }

            authRepository.signInWithEmail(email, password)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

// domain/usecase/SignUpWithEmailUseCase.kt
class SignUpWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            // Input validation
            if (email.isBlank() || !isValidEmail(email)) {
                return@withContext AuthResult.Error(AuthException.InvalidCredentials)
            }

            if (password.isBlank() || password.length < 6) {
                return@withContext AuthResult.Error(AuthException.WeakPassword)
            }

            authRepository.signUpWithEmail(email, password)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

// domain/usecase/SignInWithGoogleUseCase.kt
class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(context:Context): AuthResult {
        return withContext(Dispatchers.IO) {
            authRepository.signInWithGoogle(context)
        }
    }
}

// domain/usecase/SignInWithFacebookUseCase.kt
class SignInWithFacebookUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(accessToken: String): AuthResult {
        return withContext(Dispatchers.IO) {
            if (accessToken.isBlank()) {
                return@withContext AuthResult.Error(AuthException.InvalidCredentials)
            }

            authRepository.signInWithFacebook(accessToken)
        }
    }
}

// domain/usecase/SignOutUseCase.kt
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            authRepository.signOut()
        }
    }
}

// domain/usecase/GetCurrentUserUseCase.kt
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? {
        return withContext(Dispatchers.IO) {
            authRepository.getCurrentUser()
        }
    }
}

// domain/usecase/ObserveAuthStateUseCase.kt
class ObserveAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.currentUser
    }
}
class SendPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            if (email.isBlank() || !isValidEmail(email)) {
                return@withContext Result.failure(AuthException.InvalidCredentials)
            }

            authRepository.sendPasswordResetEmail(email)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

// domain/usecase/SendEmailVerificationUseCase.kt
class SendEmailVerificationUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            authRepository.sendEmailVerification()
        }
    }
}

data class AuthUseCases @Inject constructor(
    val signInWithEmail: SignInWithEmailUseCase,
    val signUpWithEmail: SignUpWithEmailUseCase,
    val signInWithGoogle: SignInWithGoogleUseCase,
    val signInWithFacebook: SignInWithFacebookUseCase,
    val signOut: SignOutUseCase,
    val getCurrentUser: GetCurrentUserUseCase,
    val observeAuthState: ObserveAuthStateUseCase,
    val sendPasswordReset: SendPasswordResetUseCase,
    val sendEmailVerification: SendEmailVerificationUseCase
)