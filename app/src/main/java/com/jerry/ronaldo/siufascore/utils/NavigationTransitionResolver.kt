package com.jerry.ronaldo.siufascore.utils

import androidx.compose.animation.ContentTransform
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationTransitionResolver @Inject constructor() {
    fun resolveTransition(
        fromRoute: Any?,
        toRoute: Any,
        isPopup: Boolean = false
    ): ContentTransform {

        val fromCategory = fromRoute?.let { RouteClassifier.classifyRoute(it) }
        val toCategory = RouteClassifier.classifyRoute(toRoute)
        val isBack = isPopup || RouteClassifier.isBackNavigation(fromRoute, toRoute)
        println("➡️ Navigating: [${fromRoute?.javaClass?.simpleName}] -> [${toRoute.javaClass.simpleName}] | isBack: $isBack")
        println("➡️ Categories: [$fromCategory] -> [$toCategory]")
        return when (toCategory) {
            RouteCategory.Tab -> handleTabNavigation(fromCategory, isBack)
            RouteCategory.Detail -> handleDetailNavigation(fromCategory, isBack)
            RouteCategory.Settings -> handleSettingsNavigation(isBack)
            RouteCategory.Search -> {
                println("✅ Resolving to SEARCH transition (fade)")
                ScreenTransitions.fade(1000)
            }

            RouteCategory.Auth -> ScreenTransitions.slideHorizontal(500)
        }
    }
    private fun handleTabNavigation(
        fromCategory: RouteCategory?,
        isBack: Boolean
    ): ContentTransform {
        return when (fromCategory) {
            RouteCategory.Tab -> ScreenTransitions.slideHorizontal(1000)
            else -> if (isBack) ScreenTransitions.slideHorizontalReverse()
            else ScreenTransitions.slideHorizontal()
        }
    }
    private fun handleDetailNavigation(
        fromCategory: RouteCategory?,
        isBack: Boolean
    ): ContentTransform {
        return when (fromCategory) {
            RouteCategory.Tab -> ScreenTransitions.slideHorizontal()
            RouteCategory.Detail -> if (isBack) ScreenTransitions.slideHorizontalReverse()
            else ScreenTransitions.slideHorizontal()
            else -> if (isBack) ScreenTransitions.slideHorizontalReverse()
            else ScreenTransitions.slideHorizontal()
        }
    }
    private fun handleSettingsNavigation(isBack: Boolean): ContentTransform {
        return if (isBack) ScreenTransitions.scaleReverse()
        else ScreenTransitions.slideHorizontal(
            duration = 3000,
        )
    }

}