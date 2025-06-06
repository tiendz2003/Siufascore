package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jerry.ronaldo.siufascore.presentation.ui.Purple

@Composable
internal fun RowScope.BottomAppBarItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    iconPainter: Painter,
    containerColor: Color = Color.White,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    iconColor: Color = Purple,
    textColor: Color = Purple,
    label: String,
    visibleItem: VisibleItem = VisibleItem.BOTH,
) {
    // Simplified animation states
    val animationSpec = remember {
        tween<Float>(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        )
    }

    val scaleAnimation by animateFloatAsState(
        targetValue = if (selected) 0.95f else 1f,
        animationSpec = animationSpec,
        label = "scale_animation"
    )

    val iconAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.6f,
        animationSpec = animationSpec,
        label = "icon_alpha"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (selected && visibleItem != VisibleItem.ICON) 1f else 0f,
        animationSpec = animationSpec,
        label = "text_alpha"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_scale"
    )

    Surface(
        color = containerColor,
        contentColor = contentColor,
        modifier = Modifier
            .weight(1f)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = false,
                    radius = 30.dp,
                    color = Color.Gray // Chọn màu ripple nếu muốn

                ),
            ),
    ) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .scale(scaleAnimation),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when (visibleItem) {
                VisibleItem.BOTH -> {
                    BothVisibleContent(
                        iconPainter = iconPainter,
                        iconColor = iconColor,
                        iconAlpha = iconAlpha,
                        iconScale = iconScale,
                        label = label,
                        textColor = textColor,
                        textAlpha = textAlpha,
                        selected = selected
                    )
                }

                VisibleItem.ICON -> {
                    IconOnlyContent(
                        iconPainter = iconPainter,
                        iconColor = iconColor,
                        iconAlpha = iconAlpha,
                        iconScale = iconScale
                    )
                }

                VisibleItem.LABEL -> {
                    LabelToggleContent(
                        iconPainter = iconPainter,
                        iconColor = iconColor,
                        iconAlpha = iconAlpha,
                        iconScale = iconScale,
                        label = label,
                        textColor = textColor,
                        selected = selected
                    )
                }
            }
        }
    }
}

@Composable
private fun BothVisibleContent(
    iconPainter: Painter,
    iconColor: Color,
    iconAlpha: Float,
    iconScale: Float,
    label: String,
    textColor: Color,
    textAlpha: Float,
    selected: Boolean
) {
    Icon(
        painter = iconPainter,
        contentDescription = null,
        tint = iconColor,
        modifier = Modifier
            .alpha(iconAlpha)
            .scale(iconScale)
    )

    // Smooth text appearance/disappearance
    AnimatedVisibility(
        visible = selected,
        enter = fadeIn(animationSpec = tween(250, easing = FastOutSlowInEasing)) +
                slideInVertically(
                    animationSpec = tween(250, easing = FastOutSlowInEasing),
                    initialOffsetY = { it / 2 }
                ),
        exit = fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing)) +
                slideOutVertically(
                    animationSpec = tween(200, easing = FastOutSlowInEasing),
                    targetOffsetY = { it / 2 }
                )
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun IconOnlyContent(
    iconPainter: Painter,
    iconColor: Color,
    iconAlpha: Float,
    iconScale: Float
) {
    Icon(
        painter = iconPainter,
        contentDescription = null,
        tint = iconColor,
        modifier = Modifier
            .alpha(iconAlpha)
            .scale(iconScale)
    )
}

@Composable
private fun LabelToggleContent(
    iconPainter: Painter,
    iconColor: Color,
    iconAlpha: Float,
    iconScale: Float,
    label: String,
    textColor: Color,
    selected: Boolean
) {
    // Crossfade between icon and label for smooth transition
    Crossfade(
        targetState = selected,
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        ),
        label = "icon_label_crossfade"
    ) { isSelected ->
        if (isSelected) {
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip,
            )
        } else {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .alpha(iconAlpha)
                    .scale(iconScale)
            )
        }
    }
}

internal enum class AnimationState {
    Start,
    Finish,
}

enum class VisibleItem {
    ICON,
    LABEL,
    BOTH,
}