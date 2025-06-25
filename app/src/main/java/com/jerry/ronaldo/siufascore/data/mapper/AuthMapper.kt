package com.jerry.ronaldo.siufascore.data.mapper

import com.facebook.FacebookException
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.jerry.ronaldo.siufascore.domain.model.User
import com.jerry.ronaldo.siufascore.utils.AuthException
import com.jerry.ronaldo.siufascore.utils.AuthProvider

object FirebaseAuthMapper {

    fun mapFirebaseUserToDomain(firebaseUser: FirebaseUser?): User? {
        return firebaseUser?.let { user ->
            User(
                id = user.uid,
                email = user.email ?: "",
                name = user.displayName ?: "Không rõ",
                profilePictureUrl = user.photoUrl?.toString(),
                isEmailVerified = user.isEmailVerified,
                provider = mapProviderToDomain(user.providerData.firstOrNull()?.providerId)
            )
        }
    }

    fun mapFirebaseExceptionToDomain(exception: Exception): AuthException {
        return when (exception) {
            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_INVALID_EMAIL" -> AuthException.InvalidCredentials
                    "ERROR_WRONG_PASSWORD" -> AuthException.InvalidCredentials
                    "ERROR_USER_NOT_FOUND" -> AuthException.UserNotFound
                    "ERROR_USER_DISABLED" -> AuthException.InvalidCredentials
                    "ERROR_TOO_MANY_REQUESTS" -> AuthException.InvalidCredentials
                    "ERROR_OPERATION_NOT_ALLOWED" -> AuthException.InvalidCredentials
                    "ERROR_EMAIL_ALREADY_IN_USE" -> AuthException.EmailAlreadyInUse
                    "ERROR_WEAK_PASSWORD" -> AuthException.WeakPassword
                    "ERROR_NETWORK_REQUEST_FAILED" -> AuthException.NetworkError
                    else -> AuthException.Unknown(exception.message ?: "Unknown error")
                }
            }

            else -> AuthException.Unknown(exception.message ?: "Unknown error")
        }
    }

    private fun mapProviderToDomain(providerId: String?): AuthProvider {
        return when (providerId) {
            "password" -> AuthProvider.EMAIL_PASSWORD
            "google.com" -> AuthProvider.GOOGLE
            "facebook.com" -> AuthProvider.FACEBOOK
            else -> AuthProvider.EMAIL_PASSWORD
        }
    }
}


object GoogleAuthMapper {

    fun mapGoogleExceptionToDomain(exception: ApiException): AuthException {
        return when (exception.statusCode) {
            7 -> AuthException.NetworkError // NETWORK_ERROR
            12501 -> AuthException.CancelledByUser // SIGN_IN_CANCELLED
            12502 -> AuthException.CancelledByUser // SIGN_IN_CURRENTLY_IN_PROGRESS
            else -> AuthException.Unknown(exception.message ?: "Google sign in failed")
        }
    }
}


object FacebookAuthMapper {

    fun mapFacebookExceptionToDomain(exception: Exception): AuthException {
        return when (exception) {
            is FacebookException -> {
                when {
                    exception.message?.contains("User cancelled") == true ->
                        AuthException.CancelledByUser

                    exception.message?.contains("network") == true ->
                        AuthException.NetworkError

                    else -> AuthException.Unknown(
                        exception.message ?: "Facebook sign in failed"
                    )
                }
            }

            else -> AuthException.Unknown(exception.message ?: "Facebook sign in failed")
        }
    }
}
