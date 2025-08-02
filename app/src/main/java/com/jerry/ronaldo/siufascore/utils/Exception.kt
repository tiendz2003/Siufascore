package com.jerry.ronaldo.siufascore.utils

sealed class AuthException(message: String) : Exception(message) {
    data object InvalidCredentials : AuthException("Invalid email or password")
    data object UserNotFound : AuthException("User not found")
    data object EmailAlreadyExists : AuthException("Email already registered")
    data object NetworkError : AuthException("Lỗi mạng")
    data object WeakPassword : AuthException("Mật khẩu quá yếu")
    data object InvalidEmail : AuthException("Email không hợp lệ")
    data object ServerError : AuthException("Lỗi máy chủ")
    data object CancelledByUser : AuthException(" Người dùng đã hủy")
    data object EmailAlreadyInUse : AuthException("Email đã được sử dụng")
    data class Unknown(override val message: String) : AuthException(message)
}
sealed class FavoriteException(message: String) : Exception(message) {
    data object UserNotLoggedIn : FavoriteException("Người dùng chưa đăng nhập")
    data object TeamAlreadyFavorite : FavoriteException("Đội bóng đã có trong danh sách yêu thích")
    data object TeamNotFound : FavoriteException("Không tìm thấy đội bóng trong danh sách yêu thích")
    data class FirestoreError(val error: Exception) : FavoriteException("Firestore error: ${error.message}")
    data object PlayerAlreadyFavorite : FavoriteException("Cầu thủ đã có trong danh sách yêu thích")
    data object PlayerNotFound : FavoriteException("Cầu thủ không tìm thấy trong danh sách yêu thích")
}
enum class AuthProvider {
    EMAIL_PASSWORD,
    GOOGLE,
    FACEBOOK
}

