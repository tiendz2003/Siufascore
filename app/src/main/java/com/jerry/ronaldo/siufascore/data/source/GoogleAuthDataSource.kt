package com.jerry.ronaldo.siufascore.data.source

// data/source/GoogleAuthDataSource.kt
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.jerry.ronaldo.siufascore.data.mapper.GoogleAuthMapper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var googleSignInClient: GoogleSignInClient? = null

    fun initialize(webClientId: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): android.content.Intent? {
        return googleSignInClient?.signInIntent
    }

    suspend fun handleSignInResult(data: android.content.Intent?): Result<String> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken

            if (idToken != null) {
                Result.success(idToken)
            } else {
                Result.failure(Exception("Failed to get ID token"))
            }
        } catch (e: ApiException) {
            Result.failure(GoogleAuthMapper.mapGoogleExceptionToDomain(e))
        } catch (e: Exception) {
            Result.failure(Exception("Google sign in failed: ${e.message}"))
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            googleSignInClient?.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun revokeAccess(): Result<Unit> {
        return try {
            googleSignInClient?.revokeAccess()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}