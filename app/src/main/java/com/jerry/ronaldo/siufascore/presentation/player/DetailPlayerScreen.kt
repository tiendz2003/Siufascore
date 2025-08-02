package com.jerry.ronaldo.siufascore.presentation.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.presentation.player.item.PlayerStatItem
import com.jerry.ronaldo.siufascore.presentation.player.item.StatSection
import com.jerry.ronaldo.siufascore.presentation.ui.Purple
import com.jerry.ronaldo.siufascore.utils.ExtractedColors
import com.jerry.ronaldo.siufascore.utils.extractAllColors
import kotlinx.coroutines.launch


@Composable
fun DetailPlayerScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailPlayerViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            PlayerHeader(
                playerImage = uiState.playerPhoto,
                playerTeamLogo = uiState.teamLogo,
                playerName = uiState.playerName,
                playerTeam = uiState.teamName,
                playerPosition = uiState.position,
                isFavorite = uiState.isFavoritePlayer,
                onToggleFollow = {
                    viewModel.sendIntent(DetailPlayerIntent.ToggleFollowPlayer)
                },
                onBackClick = { onBackClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )
        }
        item {
            val tabs = listOf(
                PlayerDetailTab.OVERVIEW,
                PlayerDetailTab.STATS
            )
            val selectedTab = tabs.indexOf(uiState.selectedTab).coerceAtLeast(0)

            ScrollableTabRow(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(
                        color = Color.Transparent
                    )
                    .padding(bottom = 16.dp),
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 12.dp,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                        height = 3.dp,
                        color = Purple
                    )
                }
            ) {
                tabs.forEachIndexed { index, tabs ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            viewModel.sendIntent(DetailPlayerIntent.SelectTab(tabs))
                        },
                        text = {
                            Text(
                                tabs.title, style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 14.sp
                                )
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut())
                    }.using(SizeTransform(clip = false))
                }
            ) { index ->
                when (tabs[index]) {
                    PlayerDetailTab.OVERVIEW -> {
                        OverviewPlayerSection(
                            uiState = uiState
                        )
                    }

                    PlayerDetailTab.STATS -> {
                        StatsSection(
                            uiState = uiState,
                            availableSeasons = uiState.availableSeasons,
                            onSaveSeason = { season ->
                                viewModel.sendIntent(DetailPlayerIntent.ChangeSeason(season))
                            },
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsSection(
    modifier: Modifier = Modifier,
    uiState: DetailPlayerUiState,
    availableSeasons: List<Int>,
    onSaveSeason: (Int) -> Unit
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    if (!uiState.hasStatData) {
        EmptyStatsState(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
        ) {
            SeasonSelector(
                season = uiState.currentSeason.toString(),
                onChangeSeason = {
                    showBottomSheet = true
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PlayerStatItem(
                    label = "Ra sân (Dự bị)",
                    value = uiState.totalAppearances.toString(),
                    subValue = uiState.totalSubstitutes.toString(),
                    modifier = Modifier.weight(1f)
                )
                PlayerStatItem(
                    label = "Kiến tạo",
                    value = uiState.totalAssists.toString(),
                    modifier = Modifier.weight(1f)
                )
                PlayerStatItem(
                    label = "Ghi bàn",
                    value = uiState.totalGoals.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.stats.forEach { (leagueName, stat) ->
                    StatSection(
                        title = leagueName.name,
                        stats = stat,
                        leagueLogo = leagueName.logo ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }


        }
    }

    if (showBottomSheet) {
        SeasonSelectionBottomSheet(
            initialSelectedSeason = uiState.currentSeason,
            onDismiss = {
                showBottomSheet = false
            },
            onSave = { season ->
                onSaveSeason(season)
                coroutineScope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            },
            sheetState = sheetState,
            availableSeasons = availableSeasons
        )
    }
}

@Composable
fun SeasonSelector(season: String, modifier: Modifier = Modifier, onChangeSeason: () -> Unit) {
    Box(
        modifier
            .wrapContentSize()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color.White, RoundedCornerShape(12.dp))
                .background(Purple) // Tùy chỉnh màu nền
                .clickable {
                    onChangeSeason()
                }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = season,
                fontSize = 16.sp,
                color = Color.White
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown icon",
                tint = Color.White
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonSelectionBottomSheet(
    initialSelectedSeason: Int,
    onDismiss: () -> Unit,
    onSave: (selectedSeason: Int) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    availableSeasons: List<Int>
) {
    var selectedSeason by remember { mutableIntStateOf(initialSelectedSeason) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Purple,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                "Mùa giải",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            HorizontalDivider()
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(availableSeasons) { season ->
                    SeasonRow(
                        season = season,
                        isSelected = (season == selectedSeason),
                        onSeasonClick = { selectedSeason = it }
                    )
                }
            }
            Button(
                onClick = { onSave(selectedSeason) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                )
            ) {
                Text(
                    "Lưu", style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun SeasonRow(
    season: Int,
    isSelected: Boolean,
    onSeasonClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSeasonClick(season) }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = season.toString(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 24.sp
            ),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        RadioButton(
            selected = isSelected,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.White,
                unselectedColor = Color.White.copy(alpha = 0.6f)
            ),
            onClick = { onSeasonClick(season) }
        )
    }
}

@Composable
fun PlayerHeader(
    modifier: Modifier = Modifier,
    playerImage: String,
    playerTeamLogo: String,
    playerName: String,
    playerTeam: String,
    playerPosition: String,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onToggleFollow: () -> Unit = {}
) {
    var extractedColors by remember { mutableStateOf(ExtractedColors()) }
    val context = LocalContext.current
    LaunchedEffect(playerTeamLogo) {
        extractedColors = context.extractAllColors(playerImage)
    }
    Box(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            extractedColors.vibrant,
                            extractedColors.dominant,
                            Color.Black
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .systemBarsPadding()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                onBackClick()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Button(
                onClick = {
                    onToggleFollow()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    if (isFavorite) "Bỏ theo dõi" else "Theo dõi",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.Bottom
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = playerImage,
                    contentDescription = "Kostas Tsimikas",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize(0.95f)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Player Details
            Column(
                modifier = Modifier.offset(y = (-8).dp) // Nâng text lên một chút
            ) {
                Text(
                    text = playerName,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                color = Color.White
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = playerTeamLogo,
                            contentDescription = "team Logo",
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(" • ", color = Color.White.copy(alpha = 0.8f))
                    Text(playerTeam, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Text(" • ", color = Color.White.copy(alpha = 0.8f))
                    Text(
                        playerPosition,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStatsState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Analytics,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.White.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chưa có thống kê",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Thống kê của cầu thủ sẽ được hiển thị ở đây",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}