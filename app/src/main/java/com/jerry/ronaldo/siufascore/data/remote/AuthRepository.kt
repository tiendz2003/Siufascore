package com.jerry.ronaldo.siufascore.data.remote

import android.content.Context
import com.jerry.ronaldo.siufascore.domain.model.User
import com.jerry.ronaldo.siufascore.utils.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository  {
    // Authentication state
    val currentUser: Flow<User?>
    val isLoggedIn: Flow<Boolean>

    // Email authentication
    suspend fun signInWithEmail(email: String, password: String): AuthResult
    suspend fun signUpWithEmail(email: String, password: String): AuthResult
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun observeAuthState(): Flow<AuthResult>
    // Google authentication
    suspend fun signInWithGoogle(context:Context): AuthResult

    // Facebook authentication
    suspend fun signInWithFacebook(accessToken: String): AuthResult

    // Common operations
    suspend fun signOut(): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun getCurrentUser(): User?

    // Profile updates
    suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit>
    suspend fun updateEmail(newEmail: String): Result<Unit>
    suspend fun updatePassword(newPassword: String): Result<Unit>
}

/*
interface AuthLocalDataSource {
    suspend fun saveUser(user: UserDto): Unit
    suspend fun getUser(): UserDto?
    suspend fun clearUser(): Unit
    suspend fun saveTokens(accessToken: String, refreshToken: String): Unit
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens(): Unit
    suspend fun isUserLoggedIn(): Boolean
}
*/
