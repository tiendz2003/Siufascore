package com.jerry.ronaldo.siufascore.utils

import com.jerry.ronaldo.siufascore.domain.model.User

sealed class GoogleSignInResult {
    data class Success(
        val idToken: String,
        val email: String,
        val name: String,
        val photoUrl: String?
    ) : GoogleSignInResult()

    data class Error(val exception: Exception) : GoogleSignInResult()
    data object Cancelled : GoogleSignInResult()
}

sealed class FacebookSignInResult {
    data class Success(
        val accessToken: String,
        val email: String,
        val name: String,
        val photoUrl: String?
    ) : FacebookSignInResult()

    data class Error(val exception: Exception) : FacebookSignInResult()
    data object Cancelled : FacebookSignInResult()
}

sealed class AuthResult {
    data class AuthenticatedSuccess(val user: User) : AuthResult()
    data object Unauthenticated : AuthResult()
    data class Error(val exception: AuthException) : AuthResult()
    data object Loading : AuthResult()
}