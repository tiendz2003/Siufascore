package com.jerry.ronaldo.siufascore.utils


sealed class Resource<out T> {
    data class Success<T>(val data: T): Resource<T>()
    data class Error(val exception:Exception): Resource<Nothing>()
    data object Loading: Resource<Nothing>()
}