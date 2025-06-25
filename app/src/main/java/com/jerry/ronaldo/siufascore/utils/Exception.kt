package com.jerry.ronaldo.siufascore.utils

sealed class AuthException(message: String) : Exception(message) {
    data object InvalidCredentials : AuthException("Invalid email or password")
    data object UserNotFound : AuthException("User not found")
    data object EmailAlreadyExists : AuthException("Email already registered")
    data object NetworkError : AuthException("Lỗi mạng")
    data object WeakPassword : AuthException("Password is too weak")
    data object InvalidEmail : AuthException("Invalid email format")
    data object ServerError : AuthException("Server error occurred")
    data object CancelledByUser : AuthException("Authentication cancelled by user")
    data object EmailAlreadyInUse : AuthException("Email đã được sử dụng")
    data class Unknown(override val message: String) : AuthException(message)
}
sealed class FavoriteTeamException(message: String) : Exception(message) {
    data object UserNotLoggedIn : FavoriteTeamException("User not logged in.")
    data object TeamAlreadyFavorite : FavoriteTeamException("Team is already a favorite.")
    data object TeamNotFound : FavoriteTeamException("Team not found in favorites.")
    data class FirestoreError(val error: Exception) : FavoriteTeamException("Firestore error: ${error.message}")
}
enum class AuthProvider {
    EMAIL_PASSWORD,
    GOOGLE,
    FACEBOOK
}

