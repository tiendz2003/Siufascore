package com.jerry.ronaldo.siufascore.presentation.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    @StringRes titleRes: Int,
    navigationIcon: ImageVector,
    navigationIconContentDescription: String,
    actionIcon: ImageVector,
    actionIconContentDescription: String,
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
) {

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = titleRes),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigationClick
            ) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = navigationIconContentDescription,
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(
                onClick = onActionClick
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = actionIconContentDescription,
                    tint = Color.White
                )
            }
        },
        colors = colors,
        modifier = modifier.testTag("siufascoreTopAppbar")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopAppBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    homeName: String,
    awayName: String,
) {
    TopAppBar(
        title = {
            Text(
                text = "$homeName v $awayName",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp
                ),
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Purple,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
        ),
        scrollBehavior = scrollBehavior
    )
}