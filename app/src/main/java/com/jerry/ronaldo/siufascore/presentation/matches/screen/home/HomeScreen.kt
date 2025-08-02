package com.jerry.ronaldo.siufascore.presentation.matches.screen.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.presentation.matches.MatchesEffect
import com.jerry.ronaldo.siufascore.presentation.matches.MatchesIntent
import com.jerry.ronaldo.siufascore.presentation.matches.MatchesViewModel
import com.jerry.ronaldo.siufascore.presentation.matches.screen.item.CompetitionSelectorItem
import com.jerry.ronaldo.siufascore.presentation.matches.screen.item.MatchItem
import com.jerry.ronaldo.siufascore.presentation.matches.screen.item.MatchLiveItem
import com.jerry.ronaldo.siufascore.presentation.ui.Purple
import com.jerry.ronaldo.siufascore.utils.MatchStatus
import com.jerry.ronaldo.siufascore.utils.extractDateFromUtc
import com.jerry.ronaldo.siufascore.utils.extractRoundNumber
import com.jerry.ronaldo.siufascore.utils.formatDisplayDate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun HomeScreen(
    onMatchClick: (Int) -> Unit,
    viewmodel: MatchesViewModel = hiltViewModel(),
) {
    val tabs = listOf("Lịch thi đấu", "Bảng xếp hạng")
    val pagerState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var selectedName by remember { mutableStateOf("Premier League") }
    var selectedLogo by remember { mutableIntStateOf(R.drawable.premier_league) }
    Timber.tag("HomeScreen").d("$uiState")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        CompetitionSelector(
            selectedCompetitionId = uiState.competitionId ?: 39,
            onCompetitionSelected = { competitionId, competitionLogo, competitionName ->
                Timber.tag("HomeScreen").d("Competition selected: $competitionId")
                selectedName = competitionName
                selectedLogo = competitionLogo
                viewmodel.sendIntent(MatchesIntent.LoadMatchesByLeague(competitionId))
            }
        )
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = "Đang diễn ra",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 12.sp,
            color = Purple
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.liveMatches.isEmpty()) {
                EmptyMatchesLive(Modifier.padding(16.dp))
            } else {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.liveMatches, key = { match -> match.id }) { match ->
                        MatchLiveItem(
                            matchId = match.id,
                            homeTeam = match.homeTeam,
                            awayTeam = match.awayTeam,
                            homeScore = match.score.fulltime.home,
                            awayScore = match.score.fulltime.away,
                            matchTime = "45'+2",
                            venue = match.venue.name,
                            status = MatchStatus.from(match.status.short)!!,
                            onMatchClick = {

                            }
                        )
                    }
                }
            }
        }
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            divider = {},
            indicator = {},
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Color.LightGray.copy(
                        alpha = 0.2f
                    )
                )
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = pagerState.currentPage == index
                Tab(
                    selected = isSelected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Cyan,
                                        Color(0xFF1E90FF)
                                    )
                                )
                            } else {
                                SolidColor(Color.Transparent)
                            }
                        )
                        .padding(vertical = 6.dp)
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.White else Color(0xFF350364),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 18.sp,
                        style = if (isSelected) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        HorizontalPager(
            pageSize = PageSize.Fill,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 400.dp, max = 1200.dp)
        ) { page ->
            when (page) {
                0 -> MatchesScreen(viewmodel, selectedName, selectedLogo,onMatchClick)
                else -> StandingScreen()
            }
        }

    }

}


@Composable
fun MatchesScreen(
    viewmodel: MatchesViewModel,
    selectedName: String,
    selectedLogo: Int,
    onMatchClick: (Int) -> Unit
) {
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewmodel.singleEvent.collectLatest { effect ->
            when (effect) {
                is MatchesEffect.NavigateToDetailMatch -> {
                    onMatchClick(effect.matchId)
                }

                is MatchesEffect.ShowError -> {
                    //show error
                }

                else -> {}
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = Color.White,
            )
    ) {

        RoundTabsHeader(
            competitionName = selectedName,
            competitionIcon = selectedLogo,
            currentMatchday = uiState.currentMatchday.extractRoundNumber().toInt(),
            totalMatchdays = uiState.availableMatchday.size,
            onRoundSelected = { selectedRound ->
                viewmodel.sendIntent(MatchesIntent.SetMatchday(selectedRound.toString()))
            }
        )
        MatchList(
            matches = uiState.matches,
            onMatchClick = { matchId ->
                viewmodel.sendIntent(
                    MatchesIntent.NavigateToDetailMatch(matchId)
                )
            }
        )
    }
}

@Composable
fun RoundTabsHeader(
    competitionName: String?,
    competitionIcon: Int,
    currentMatchday: Int,
    totalMatchdays: Int,
    onRoundSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00BFFF),
                        Color(0xFF1E90FF)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(competitionIcon),
                    contentDescription = "Logo Competition",
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = competitionName ?: "Lịch thi đấu",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        RoundScrollTabs(
            currentMatchday = currentMatchday,
            totalMatchdays = totalMatchdays,
            onRoundSelected = onRoundSelected
        )

    }
}


@Composable
fun RoundScrollTabs(
    currentMatchday: Int,
    totalMatchdays: Int,
    onRoundSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var isFirstOpenApp by remember { mutableStateOf(false) }

    LaunchedEffect(currentMatchday, totalMatchdays) {
        coroutineScope.launch {
            val targetIndex = (currentMatchday - 1).coerceAtLeast(0)
            if (!isFirstOpenApp) {
                listState.scrollToItem(
                    index = targetIndex,
                    scrollOffset = -200 // Center the item
                )
                isFirstOpenApp = true
            } else {
                listState.animateScrollToItem(
                    index = targetIndex,
                    scrollOffset = -200 // Center the item
                )
            }
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(totalMatchdays) { index ->
            val roundNumber = index + 1
            val isSelected = roundNumber == currentMatchday
            val isCompleted = roundNumber < currentMatchday
            val isUpcoming = roundNumber > currentMatchday

            RoundTab(
                roundNumber = roundNumber,
                isSelected = isSelected,
                isCompleted = isCompleted,
                isUpcoming = isUpcoming,
                onClick = { onRoundSelected(roundNumber) }
            )
        }
    }
}


@Composable
fun RoundTab(
    roundNumber: Int,
    isSelected: Boolean,
    isCompleted: Boolean,
    isUpcoming: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> Color.White
            else -> Purple
        },
        animationSpec = tween(300),
        label = "backgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isSelected -> Color(0xFF1E90FF)
            isCompleted -> Color.White.copy(alpha = 0.8f)
            else -> Color.White.copy(alpha = 0.6f)
        },
        animationSpec = tween(300),
        label = "contentColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .scale(scale)
            .size(
                width = if (isSelected) 70.dp else 60.dp,
                height = if (isSelected) 40.dp else 36.dp
            )
            .clickable(
                indication = ripple(
                    color = if (isSelected) Color(0xFF1E90FF) else Color.White,
                    radius = 30.dp
                ),
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        shape = RoundedCornerShape(if (isSelected) 12.dp else 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$roundNumber",
                    color = contentColor,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = if (isSelected) 16.sp else 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                when {
                    isSelected -> {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(
                                    Color(0xFF1E90FF),
                                    CircleShape
                                )
                        )
                    }

                    isCompleted -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Hoàn thành",
                            tint = contentColor,
                            modifier = Modifier.size(8.dp)
                        )
                    }

                    isUpcoming -> {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Sắp đá",
                            tint = contentColor,
                            modifier = Modifier.size(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatchList(matches: List<Match>, onMatchClick: (Int) -> Unit = {}) {
    Timber.tag("MatchesScreen").d("Matches: $matches")
    //sắp xếp lại các trận đấu theo ngày(sử dụng group by để nhóm)
    val groupMatches = matches.groupBy { match ->
        match.date.extractDateFromUtc()
    }.toSortedMap()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        groupMatches.forEach { (date, matchesForDate) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.formatDisplayDate(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 20.sp,
                    color = Purple,
                    textAlign = TextAlign.Center,
                )
            }
            matchesForDate.forEach { match ->
                MatchItem(
                    match = match,
                    onMatchClick = { matchId ->
                        onMatchClick(matchId)
                    }
                )
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun CompetitionSelector(
    selectedCompetitionId: Int,
    onCompetitionSelected: (Int, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    val competitions = remember {
        listOf(
            CompetitionSelector(
                id = 39,
                logo = R.drawable.premier_league,
                name = "Premier League"
            ),
            CompetitionSelector(
                id = 135,
                logo = R.drawable.seria,
                name = "Seria A"
            ),
            CompetitionSelector(
                id = 78,
                logo = R.drawable.bundesliga_logo,
                name = "Bundesliga"
            ),
            CompetitionSelector(
                id = 140,
                logo = R.drawable.laliga,
                name = "La Liga"
            )
        )
    }
    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 8.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        flingBehavior = rememberSnapFlingBehavior(listState)
    ) {
        itemsIndexed(
            competitions,
            key = { index, competition ->
                competition.id
            }
        ) { index, competition ->
            CompetitionSelectorItem(
                competition = competition,
                isSelected = competition.id == selectedCompetitionId,
                onClick = {
                    if (competition.id != selectedCompetitionId) {
                        onCompetitionSelected(competition.id, competition.logo, competition.name)
                    }
                },
                animationDelay = index * 50
            )
        }
    }
}


@Composable
fun EmptyMatchesLive(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .drawBehind {
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                drawRoundRect(
                    color = Purple,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = pathEffect
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Hiện không có trận đấu nào đang diễn ra",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

data class CompetitionSelector(
    val id: Int,
    @DrawableRes val logo: Int,
    val name: String
)