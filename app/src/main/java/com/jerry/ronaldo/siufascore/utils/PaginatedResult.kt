package com.jerry.ronaldo.siufascore.utils

data class PaginatedResult<T>(
    val data: List<T>,
    val currentPage: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val totalResults: Int
) {
    val isEmpty: Boolean get() = data.isEmpty()
    val isFirstPage: Boolean get() = currentPage == 1
    val isLastPage: Boolean get() = !hasNextPage
}