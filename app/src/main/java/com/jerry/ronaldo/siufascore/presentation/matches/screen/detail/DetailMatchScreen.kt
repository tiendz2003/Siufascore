package com.jerry.ronaldo.siufascore.presentation.matches.screen.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.domain.model.MatchEvent
import com.jerry.ronaldo.siufascore.presentation.matches.DetailMatchViewModel
import com.jerry.ronaldo.siufascore.presentation.ui.DetailTopAppBar
import com.jerry.ronaldo.siufascore.presentation.ui.Purple
import com.jerry.ronaldo.siufascore.utils.formatDisplayDate
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMatchScreen(
    modifier: Modifier = Modifier,
    detailViewModel: DetailMatchViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState by detailViewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Purple,
        topBar = {
            DetailTopAppBar(
                onBackClick = onBackClick,
                homeName = uiState.match?.homeTeam?.name ?: "?",
                awayName = uiState.match?.awayTeam?.name ?: "?",
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item("match_header") {
                MatchHeaderCard(uiState)
            }
            item("match_row") {
                MatchTabRow(
                    uiState = uiState,
                    onSelectedTab = { tab->
                        detailViewModel.sendIntent(DetailMatchIntent.SelectTab(tab))
                    }
                )
            }

        }
    }
}

@Composable
fun MatchHeaderCard(uiState: DetailMatchState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFF8F9FA)
                        )
                    )
                )
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            val homeGoals = uiState.goalEvents.filter {
                it.team.id == uiState.homeTeamId
            }

            val awayGoals = uiState.goalEvents.filter {
                it.team.id == uiState.awayTeamId
            }
            CountdownTimer(
                days = 50,
                hours = 10,
                minutes = 10,
                seconds = 0
            )
            MatchSection(
                awayTeamName = uiState.match?.awayTeam?.name,
                awayTeamLogo = uiState.match?.awayTeam?.logo,
                homeTeamName = uiState.match?.homeTeam?.name,
                homeTeamLogo = uiState.match?.homeTeam?.logo,
                fulltimeScore = "${uiState.match?.score?.fulltime?.home.toString()} - ${uiState.match?.score?.fulltime?.away.toString()}"
            )
            GoalsList(
                homeGoals = homeGoals,
                awayGoals = awayGoals,
            )
            MatchInfo(
                date = uiState.match?.date?.formatDisplayDate() ?: "",
                venue = uiState.match?.venue?.name ?: ""
            )
        }

    }
}

@Composable
fun MatchInfo(
    date: String, venue: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = venue,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MatchSection(
    awayTeamName: String?,
    awayTeamLogo: String?,
    homeTeamName: String?,
    homeTeamLogo: String?,
    fulltimeScore: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Team Section
        DetailTeamSection(
            modifier = Modifier
                .weight(1f),
            isHome = true,
            teamName = homeTeamName ?: "?",
            teamColor = listOf(
                Color.Red,
                Color.Blue
            ),
            teamLogo = homeTeamLogo ?: ""
        )

        // Center Section với thời gian
        ScoreSection(
            score = fulltimeScore ?: ""
        )
        // Away Team Section
        DetailTeamSection(
            modifier = Modifier
                .weight(1f),
            isHome = false,
            teamName = awayTeamName ?: "",
            teamColor = listOf(
                Color.Red,
                Color.Blue
            ),
            teamLogo = awayTeamLogo ?: ""
        )
    }
}

@Composable
fun ScoreSection(score: String) {
    Card(
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = score,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
    }
}


// Version alternative với padding thay vì offset
@Composable
fun DetailTeamSection(
    modifier: Modifier = Modifier,
    isHome: Boolean,
    teamName: String,
    teamColor: List<Color>,
    teamLogo: String,
    cornerRadius: Dp = 12.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        // Background Card
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(cornerRadius),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = teamColor,
                            startX = if (isHome) 0f else Float.POSITIVE_INFINITY,
                            endX = if (isHome) Float.POSITIVE_INFINITY else 0f
                        )
                    )
                    .padding(
                        start = if (isHome) 60.dp else 16.dp,
                        end = if (isHome) 16.dp else 60.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = teamName,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Team Logo
        Surface(
            modifier = Modifier
                .size(44.dp)
                .align(
                    if (isHome) Alignment.CenterStart
                    else Alignment.CenterEnd
                )
                .padding(
                    start = if (isHome) 8.dp else 0.dp,
                    end = if (isHome) 0.dp else 8.dp
                ),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 8.dp,
            border = BorderStroke(2.dp, Color.White.copy(alpha = 0.9f))
        ) {
            AsyncImage(
                model = teamLogo,
                contentDescription = "Team Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                contentScale = ContentScale.Fit,
                placeholder = painterResource(R.drawable.premier_league),
                error = painterResource(R.drawable.premier_league)
            )
        }
    }
}


@Composable
fun CountdownTimer(days: Int, hours: Int, minutes: Int, seconds: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TimeUnit(days, "ngày")
        TimeUnit(hours, "giờ")
        TimeUnit(minutes, "phút")
        TimeUnit(seconds, "giây")

    }
}

@Composable
fun TimeUnit(value: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF00BFFF),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun MatchTabRow(uiState: DetailMatchState, onSelectedTab: (MatchDetailTab) -> Unit) {
    val tabs = listOf(
        MatchDetailTab.OVERVIEW,
        MatchDetailTab.LINEUPS,
        MatchDetailTab.EVENTS,
        MatchDetailTab.H2H
    )
    val initialPage  = tabs.indexOf(uiState.selectedTab).takeIf { it >=0 }?:0
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f
    ) { tabs.size }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(uiState.selectedTab) {
        val targetPage = tabs.indexOf(uiState.selectedTab)
        if(targetPage > 0 && targetPage != pagerState.currentPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        if(pagerState.currentPage < tabs.size) {
            val currentTab = tabs[pagerState.currentPage]
            if(currentTab != uiState.selectedTab) {
                onSelectedTab(currentTab)
            }
        }
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                divider = {},
                indicator = {},
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Color.LightGray.copy(
                            alpha = 0.2f
                        )
                    )
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = pagerState.currentPage == index
                    Tab(
                        selected = isSelected,
                        onClick = {
                            onSelectedTab(tab)
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                color = if (isSelected) {
                                    Purple
                                } else {
                                    Color.Transparent
                                }
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = tab.title,
                            color = if (isSelected) Color.White else Purple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
            HorizontalPager(
                pageSize = PageSize.Fill,
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            ) { page ->

                when (tabs[page]) {
                    MatchDetailTab.OVERVIEW ->
                        MatchStatsScreen(
                            modifier = Modifier.fillMaxWidth(),
                            homeTeamName = uiState.match?.homeTeam?.name ?: "?",
                            awayTeamName = uiState.match?.awayTeam?.name ?: "?",
                            statistics = uiState.statistics
                        )

                    MatchDetailTab.LINEUPS  ->
                        LineUpScreen(
                            homeFormation = uiState.awayTeamFormation ?: "Đang tải sơ đồ",
                            awayFormation = uiState.homeTeamFormation ?: "Đang tải sơ đồ",
                            homeLineup = uiState.homeTeamLineup,
                            awayLineup = uiState.awayTeamLineup,
                            modifier = Modifier.fillMaxWidth(),
                            showBench = true,
                            onPlayerClick = { player ->

                            }
                        )

                    MatchDetailTab.EVENTS -> {
                        MatchEventScreen(
                            uiState = uiState
                        )
                    }
                    MatchDetailTab.H2H -> {
                        Timber.tag("DetailMatchScreen").d("H2H: ${uiState.h2hError}")
                        H2HScreen(
                            modifier = Modifier.fillMaxSize(),
                            matches = uiState.h2hInfo ?: emptyList(),
                            onMatchClick = {

                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoalsList(
    modifier: Modifier = Modifier,
    homeGoals: List<MatchEvent>, // ✅ Sử dụng domain model
    awayGoals: List<MatchEvent>, // ✅ Sử dụng domain model
    homeTeamColor: Color = Color(0xFF8E44AD),
    awayTeamColor: Color = Color(0xFF3498DB)
) {

    if (homeGoals.isEmpty() && awayGoals.isEmpty()) {
        // No goals state
        NoGoalsState(modifier = modifier)
        return
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Home team goals - align to start
        homeGoals.forEach { goal ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                GoalScorerItem(
                    goalEvent = goal,
                    isHome = true,
                    teamColor = homeTeamColor
                )
            }
        }

        // Away team goals - align to end
        awayGoals.forEach { goal ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                GoalScorerItem(
                    goalEvent = goal,
                    isHome = false,
                    teamColor = awayTeamColor
                )
            }
        }
    }
}

@Composable
fun GoalScorerItem(
    goalEvent: MatchEvent,
    isHome: Boolean,
    teamColor: Color = Color(0xFF8E44AD),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(fraction = 0.5f) // Tăng chiều rộng để fit content
            .height(56.dp), // Tăng height để fit assist info
        shape = RoundedCornerShape(
            topStart = if (isHome) 24.dp else 8.dp,
            topEnd = if (isHome) 8.dp else 24.dp,
            bottomStart = if (isHome) 24.dp else 8.dp,
            bottomEnd = if (isHome) 8.dp else 24.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = teamColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isHome) Arrangement.Start else Arrangement.End
        ) {
            if (isHome) {
                GoalIcon(goalEvent = goalEvent)
                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = goalEvent.player.name,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Show assist if available
                    goalEvent.assist?.let { assist ->
                        Text(
                            text = "Kiến tạo: ${assist.shortName}",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = goalEvent.displayTime, // ✅ Sử dụng computed property
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (goalEvent.detail.isNotEmpty()) {
                        Text(
                            text = getGoalTypeDisplay(goalEvent.detail),
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                // Away team: Time - Name/Assist - Icon
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = goalEvent.displayTime,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (goalEvent.detail.isNotEmpty()) {
                        Text(
                            text = getGoalTypeDisplay(goalEvent.detail),
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = goalEvent.player.name,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    goalEvent.assist?.let { assist ->
                        Text(
                            text = "Assist: ${assist.shortName}",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))
                GoalIcon(goalEvent = goalEvent)
            }
        }
    }
}

@Composable
private fun NoGoalsState(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "⚽",
                fontSize = 32.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No Goals Yet",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Goals will appear here when scored",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper function để format goal type
private fun getGoalTypeDisplay(detail: String): String {
    return when {
        detail.contains("Penalty", ignoreCase = true) -> "Penalty"
        detail.contains("Own Goal", ignoreCase = true) -> "Own Goal"
        detail.contains("Header", ignoreCase = true) -> "Header"
        detail.contains("Free-kick", ignoreCase = true) -> "Free-kick"
        detail.contains("Normal Goal", ignoreCase = true) -> "Goal"
        else -> if (detail.length > 15) detail.take(12) + "..." else detail
    }
}

@Composable
fun GoalIcon(
    goalEvent: MatchEvent,
    modifier: Modifier = Modifier
) {
    val (icon, backgroundColor) = when {
        goalEvent.detail.contains(
            "Penalty",
            ignoreCase = true
        ) -> "⚽" to Color.Red.copy(alpha = 0.2f)

        goalEvent.detail.contains(
            "Own Goal",
            ignoreCase = true
        ) -> "😞" to Color.Yellow.copy(alpha = 0.2f)

        goalEvent.detail.contains(
            "Header",
            ignoreCase = true
        ) -> "🎯" to Color.Blue.copy(alpha = 0.2f)

        goalEvent.detail.contains(
            "Free-kick",
            ignoreCase = true
        ) -> "🚀" to Purple.copy(alpha = 0.2f)

        else -> "⚽" to Color.Green.copy(alpha = 0.2f) // Regular goal
    }

    Box(
        modifier = modifier
            .size(32.dp)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            fontSize = 16.sp
        )
    }
}
