package com.jerry.ronaldo.siufascore.data.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.jerry.ronaldo.siufascore.data.remote.AuthRepository
import com.jerry.ronaldo.siufascore.data.source.FacebookAuthDataSource
import com.jerry.ronaldo.siufascore.data.source.FirebaseAuthDataSource
import com.jerry.ronaldo.siufascore.data.source.GoogleAuthDataSource
import com.jerry.ronaldo.siufascore.domain.model.User
import com.jerry.ronaldo.siufascore.utils.AuthException
import com.jerry.ronaldo.siufascore.utils.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val googleAuthDataSource: GoogleAuthDataSource,
    private val facebookAuthDataSource: FacebookAuthDataSource,
) : AuthRepository {
    override val currentUser: Flow<User?>
        get() = firebaseAuthDataSource.currentUser
    override val isLoggedIn: Flow<Boolean>
        get() = currentUser.map { it != null }
    override fun observeAuthState ():Flow<AuthResult> = currentUser.map { user ->
        if (user != null) {
            AuthResult.AuthenticatedSuccess(user)
        } else {
            AuthResult.Error(AuthException.UserNotFound)
        }
    }
    override suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return firebaseAuthDataSource.signInWithEmail(email, password)
    }

    override suspend fun signUpWithEmail(email: String, password: String): AuthResult {
        return firebaseAuthDataSource.signUpWithEmail(email, password)
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return firebaseAuthDataSource.sendPasswordResetEmail(email)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun signInWithGoogle(context:Context): AuthResult {
        return firebaseAuthDataSource.signInWithGoogle(context)
    }

    override suspend fun signInWithFacebook(accessToken: String): AuthResult {
        return firebaseAuthDataSource.signInWithFacebook(accessToken)
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            // Sign out from all providers
            firebaseAuthDataSource.signOut()
            googleAuthDataSource.signOut()
            facebookAuthDataSource.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return firebaseAuthDataSource.deleteAccount()
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return firebaseAuthDataSource.sendEmailVerification()
    }

    override suspend fun getCurrentUser(): User? {
        return firebaseAuthDataSource.getCurrentUser()
    }

    override suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit> {
        return firebaseAuthDataSource.updateProfile(displayName, photoUrl)
    }

    override suspend fun updateEmail(newEmail: String): Result<Unit> {
        return firebaseAuthDataSource.updateEmail(newEmail)
    }

    override suspend fun updatePassword(newPassword: String): Result<Unit> {
        return firebaseAuthDataSource.updatePassword(newPassword)
    }

}