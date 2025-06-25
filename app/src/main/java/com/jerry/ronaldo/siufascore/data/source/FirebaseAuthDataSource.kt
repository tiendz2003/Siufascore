package com.jerry.ronaldo.siufascore.data.source


import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.data.mapper.FirebaseAuthMapper
import com.jerry.ronaldo.siufascore.domain.model.User
import com.jerry.ronaldo.siufascore.utils.AuthException
import com.jerry.ronaldo.siufascore.utils.AuthResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) {

    val currentUser = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = FirebaseAuthMapper.mapFirebaseUserToDomain(auth.currentUser)
            trySend(user)
        }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = FirebaseAuthMapper.mapFirebaseUserToDomain(result.user)

            if (user != null) {
                AuthResult.AuthenticatedSuccess(user)
            } else {
                AuthResult.Error(AuthException.Unknown("Failed to get user data"))
            }
        } catch (e: Exception) {
            AuthResult.Error(FirebaseAuthMapper.mapFirebaseExceptionToDomain(e))
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = FirebaseAuthMapper.mapFirebaseUserToDomain(result.user)

            if (user != null) {
                AuthResult.AuthenticatedSuccess(user)
            } else {
                AuthResult.Error(AuthException.Unknown("Failed to create user"))
            }
        } catch (e: Exception) {
            AuthResult.Error(FirebaseAuthMapper.mapFirebaseExceptionToDomain(e))
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun signInWithGoogle(context:Context): AuthResult {
        return try {
            val credentialManager = CredentialManager.create(context)
            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashNonce = digest.fold("") { str, byte -> str + "%02x".format(byte) }
            val signInOptions = GetSignInWithGoogleOption.Builder(
                context.getString(R.string.web_client_id),
            ).setNonce(hashNonce).build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOptions)
                .build()
            val result = credentialManager.getCredential(context, request)
            handleSignInResult(result)
        } catch (e: Exception) {
            AuthResult.Error(FirebaseAuthMapper.mapFirebaseExceptionToDomain(e))
        }
    }

    suspend fun signInWithFacebook(accessToken: String): AuthResult {
        return try {
            val credential = FacebookAuthProvider.getCredential(accessToken)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = FirebaseAuthMapper.mapFirebaseUserToDomain(result.user)

            if (user != null) {
                AuthResult.AuthenticatedSuccess(user)
            } else {
                AuthResult.Error(AuthException.Unknown("Failed to get user data"))
            }
        } catch (e: Exception) {
            AuthResult.Error(FirebaseAuthMapper.mapFirebaseExceptionToDomain(e))
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(FirebaseAuthMapper.mapFirebaseExceptionToDomain(e))
        }
    }

    suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.sendEmailVerification().await()
                Result.success(Unit)
            } else {
                Result.failure(AuthException.UserNotFound)
            }
        } catch (e: Exception) {
            Result.failure(FirebaseAuthMapper.mapFirebaseExceptionToDomain(e))
        }
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.delete().await()
                Result.success(Unit)
            } else {
                Result.failure(AuthException.UserNotFound)
            }
        } catch (e: Exception) {
            Result.failure(FirebaseAuthMapper.mapFirebaseExceptionToDomain(e))
        }
    }

    suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(photoUrl?.toUri())
                    .build()

                user.updateProfile(profileUpdates).await()
                Result.success(Unit)
            } else {
                Result.failure(AuthException.UserNotFound)
            }
        } catch (e: Exception) {
            Result.failure(FirebaseAuthMapper.mapFirebaseExceptionToDomain(e))
        }
    }

    suspend fun updateEmail(newEmail: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.updateEmail(newEmail).await()
                Result.success(Unit)
            } else {
                Result.failure(AuthException.UserNotFound)
            }
        } catch (e: Exception) {
            Result.failure(FirebaseAuthMapper.mapFirebaseExceptionToDomain(e))
        }
    }

    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.updatePassword(newPassword).await()
                Result.success(Unit)
            } else {
                Result.failure(AuthException.UserNotFound)
            }
        } catch (e: Exception) {
            Result.failure(FirebaseAuthMapper.mapFirebaseExceptionToDomain(e))
        }
    }

    fun getCurrentUser(): User? {
        return FirebaseAuthMapper.mapFirebaseUserToDomain(firebaseAuth.currentUser)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private suspend fun handleSignInResult(result: GetCredentialResponse): AuthResult {
        return try {
            when (val credential = result.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            val googleIdTokenCredential = GoogleIdTokenCredential
                                .createFrom(credential.data)
                            val googleIdToken = googleIdTokenCredential.idToken
                            val authCredential = GoogleAuthProvider.getCredential(
                                googleIdToken, null
                            )
                            val user =
                                FirebaseAuthMapper.mapFirebaseUserToDomain(
                                    firebaseAuth.signInWithCredential(
                                        authCredential
                                    ).await().user
                                )
                            if (user != null) {
                                AuthResult.AuthenticatedSuccess(user)
                            } else {
                                AuthResult.Error(AuthException.Unknown("Failed to get user data"))
                            }
                        } catch (e: GoogleIdTokenParsingException) {
                            AuthResult.Error(AuthException.Unknown("${e.message}"))
                        }
                    } else {
                        AuthResult.Error(AuthException.Unknown("Failed to get user data"))
                    }
                }

                else -> {
                    AuthResult.Error(AuthException.Unknown("Failed to get user data"))
                }
            }
        } catch (e: Exception) {
            AuthResult.Error(AuthException.Unknown("Failed to get user data:${e.message}"))
        }
    }
}
