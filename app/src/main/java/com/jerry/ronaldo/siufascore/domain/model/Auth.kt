package com.jerry.ronaldo.siufascore.domain.model

import com.jerry.ronaldo.siufascore.utils.AuthProvider

data class User(
    val id: String,
    val email: String,
    val name: String,
    val profilePictureUrl: String? = null,
    val provider: AuthProvider,
    val isEmailVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)