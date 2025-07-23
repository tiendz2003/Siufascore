package com.jerry.ronaldo.siufascore.presentation.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppNavigationSuiteScaffold(
    navigationSuiteItems: NiaNavigationSuiteScope.() -> Unit,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    content: @Composable () -> Unit,
) {
    val layoutType = NavigationSuiteScaffoldDefaults
        .calculateFromAdaptiveInfo(windowAdaptiveInfo)
    val navigationSuiteItemColors = NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = AppNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = AppNavigationDefaults.navigationContentColor(),
            selectedTextColor = AppNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = AppNavigationDefaults.navigationContentColor(),
            indicatorColor = Color.Transparent,

        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = AppNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = AppNavigationDefaults.navigationContentColor(),
            selectedTextColor = AppNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = AppNavigationDefaults.navigationContentColor(),
            indicatorColor = Color.Transparent,
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = AppNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = AppNavigationDefaults.navigationContentColor(),
            selectedTextColor = AppNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = Color.Transparent,
        ),
    )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            NiaNavigationSuiteScope(
                navigationSuiteScope = this,
                navigationSuiteItemColors = navigationSuiteItemColors,
            ).run(navigationSuiteItems)
        },
        layoutType = layoutType,
        containerColor = Color.White,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContentColor = Color.White,
            navigationBarContainerColor = PremierPurpleDark,
            navigationRailContainerColor = Color.Transparent,
        ),
        modifier = modifier,
    ) {
        content()
    }
}

class NiaNavigationSuiteScope internal constructor(
    private val navigationSuiteScope: NavigationSuiteScope,
    private val navigationSuiteItemColors: NavigationSuiteItemColors,
) {
    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        selectedIcon: @Composable () -> Unit = icon,
        label: @Composable (() -> Unit)? = null,
    ) = navigationSuiteScope.item(
        selected = selected,
        onClick = onClick,
        icon = {
            if (selected) {
                selectedIcon()
            } else {
                icon()
            }
        },
        label = label,
        colors = navigationSuiteItemColors,
        modifier = modifier,
    )
}

object AppNavigationDefaults {
    @Composable
    fun navigationContentColor() = Color.White

    @Composable
    fun navigationSelectedItemColor() = Color.White

    @Composable
    fun navigationIndicatorColor() = Color.Transparent
}

@Composable
fun AnimationNavigationIcon(
    iconRes: ImageVector? = null,
    selectedIconRes: ImageVector? = iconRes,
    selected: Boolean,
    label: String
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_scale"
    )
    val rotation by animateFloatAsState(
        targetValue = if (selected) 360f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "icon_rotation"
    )
    Box(
        modifier = Modifier
            .scale(scale)
            .rotate(
                if (selected) rotation else 0f
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Color(0xFF6366F1).copy(0.1f),
                        CircleShape
                    )
            )
        }
        Icon(
            imageVector = if (selected) selectedIconRes!! else iconRes!!,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
fun AnimationNavigationLabel(
    text: String,
    selected: Boolean
) {
    val fontWeight by animateIntAsState(
        targetValue = if (selected) 600 else 400,
        label = "font_weight"
    )
    val letterSpacing by animateFloatAsState(
        targetValue = if (selected) 0.5f else 0f,
        label = "letter_spacing"
    )
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight(fontWeight),
            letterSpacing = letterSpacing.sp
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}