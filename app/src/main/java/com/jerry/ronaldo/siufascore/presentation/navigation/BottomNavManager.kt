package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

interface TopLevelRoute {
    val icon: ImageVector
    val title: String
}
@Serializable
data object Home : TopLevelRoute {
    override val icon: ImageVector
        get() = Icons.Default.Home
    override val title: String
        get() = "Trang chủ"
}
@Serializable
data object News : TopLevelRoute {
    override val icon: ImageVector
        get() = Icons.Default.FavoriteBorder
    override val title: String
        get() = "Tin tức"
}
@Serializable
data object LiveStream : TopLevelRoute {
    override val icon: ImageVector
        get() = Icons.Default.Favorite
    override val title: String
        get() = "Trực tiếp"
}

val TOP_LEVEL_ROUTES: List<TopLevelRoute> = listOf(
    Home, News, LiveStream
)

class TopLevelBackStack<T : Any>(startKey: T) {
    private var topLevelStacks: LinkedHashMap<T, SnapshotStateList<T>> = linkedMapOf(
        startKey to mutableStateListOf(startKey)
    )
    var topLevelKey by mutableStateOf(startKey)
        private set
    val backStack = mutableStateListOf(startKey)

    private fun updateBackStack() {
        backStack.apply {
            clear()
            addAll(topLevelStacks.flatMap { it.value })
        }
    }

    fun addTopLevel(key: T) {
        if (topLevelStacks[key] == null) {
            topLevelStacks[key] = mutableStateListOf(key)
        } else {
            topLevelStacks.apply {
                remove(key)?.let {
                    put(key, it)
                }
            }
        }
        topLevelKey = key
        updateBackStack()
    }

    fun addKey(key: T) {
        topLevelStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun removeLast() {
        val removedKey = topLevelStacks[topLevelKey]?.removeLastOrNull()
        topLevelStacks.remove(removedKey)
        topLevelKey = topLevelStacks.keys.last()
        updateBackStack()
    }
}