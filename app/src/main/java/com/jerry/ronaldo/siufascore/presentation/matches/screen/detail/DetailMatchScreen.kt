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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.domain.model.Player
import com.jerry.ronaldo.siufascore.domain.model.TeamLineup
import com.jerry.ronaldo.siufascore.presentation.matches.MatchesViewModel
import com.jerry.ronaldo.siufascore.presentation.ui.DetailTopAppBar
import com.jerry.ronaldo.siufascore.presentation.ui.Purple
import com.jerry.ronaldo.siufascore.presentation.ui.SiufascoreTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMatchScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,

    ) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Purple,
        topBar = {
            DetailTopAppBar(
                onBackClick = onBackClick,
                homeName = "Liverpool",
                awayName = "Chelsea",
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
            item {
                MatchHeaderCard()
            }
            item {
                MatchTabRow()
            }

        }
    }
}

@Composable
fun MatchHeaderCard() {
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
            val sampleHomeGoals = listOf(
                Goal("Magalhaes", "63'"),
                Goal("Salah", "75'", isPenalty = true)
            )

            val sampleAwayGoals = listOf(
                Goal("Sterling", "23'"),
                Goal("Havertz", "89'")
            )
            CountdownTimer(
                days = 50,
                hours = 10,
                minutes = 10,
                seconds = 0
            )
            MatchSection()
            GoalsList(
                homeGoals = sampleHomeGoals,
                awayGoals = sampleAwayGoals
            )
            MatchInfo("24/7", "Old Trafford")
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
fun MatchSection() {
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
            teamName = "LIV",
            teamColor = listOf(
                Color.Red,
                Color.Blue
            ),
            teamLogo = "adawd"
        )

        // Center Section với thời gian
        ScoreSection(
            score = "2 - 2"
        )
        // Away Team Section
        DetailTeamSection(
            modifier = Modifier
                .weight(1f),
            isHome = false,
            teamName = "CHE",
            teamColor = listOf(
                Color.Red,
                Color.Blue
            ),
            teamLogo = "adawd"
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

@Preview
@Composable
fun MatchSectionPreview() {
    SiufascoreTheme {
        MatchHeaderCard()
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
fun MatchTabRow(viewmodel: MatchesViewModel = hiltViewModel()) {
    val uiState by viewmodel.uiState.collectAsState()
    val tabs = listOf("Stats", "Line up", "Events")
    val pagerState = rememberPagerState(
        initialPage = 1,
        initialPageOffsetFraction = 0f
    ) { tabs.size }
    val coroutineScope = rememberCoroutineScope()
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
                                color = if (isSelected) {
                                    Purple
                                } else {
                                    Color.Transparent
                                }
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = title,
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
                val sampleTeamLineup = TeamLineup(
                    formation = "4-2-3-1",
                    lineup = listOf(
                        Player(3188, "David De Gea", "Goalkeeper", 1),
                        Player(15905, "Diogo Dalot", "Right-Back", 20),
                        Player(3360, "Raphaël Varane", "Centre-Back", 19),
                        Player(3326, "Harry Maguire", "Centre-Back", 5),
                        Player(7898, "Luke Shaw", "Left-Back", 23),
                        Player(3366, "Paul Pogba", "Central Midfield", 6),
                        Player(7905, "Scott McTominay", "Defensive Midfield", 39),
                        Player(3257, "Bruno Fernandes", "Attacking Midfield", 18),
                        Player(146, "Jadon Sancho", "Left Winger", 25),
                        Player(44, "Cristiano Ronaldo", "Centre-Forward", 7),
                        Player(3331, "Marcus Rashford", "Right Winger", 10)
                    ),
                    bench = listOf(
                        Player(5457, "Dean Henderson", "Goalkeeper", 26),
                        Player(3492, "Victor Nilsson-Lindelöf", "Centre-Back", 2),
                        Player(7897, "Phil Jones", "Centre-Back", 4)
                    )
                )
                when (page) {
                    0 ->
                        MatchStatsScreen(modifier = Modifier.fillMaxWidth())

                    1 ->
                        LineUpScreen(
                            teamLineup = sampleTeamLineup,
                            modifier = Modifier.fillMaxWidth(),
                            showBench = true,
                            onPlayerClick = { player ->
                                println("Clicked on ${player.name}")
                            }
                        )

                    else -> {
                        H2HScreen(
                            modifier = Modifier.fillMaxWidth(),
                            matches = uiState.matches,
                            onMatchClick = {}
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
    homeGoals: List<Goal>,
    awayGoals: List<Goal>,
    homeTeamColor: Color = Color(0xFF8E44AD),
    awayTeamColor: Color = Color(0xFF3498DB),

    ) {
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
                    goal = goal,
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
                    goal = goal,
                    isHome = false,
                    teamColor = awayTeamColor
                )
            }
        }
    }
}

@Composable
fun GoalScorerItem(
    goal: Goal,
    isHome: Boolean,
    teamColor: Color = Color(0xFF8E44AD),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(fraction = 0.5f) // Chỉ chiếm 50% chiều rộng
            .height(48.dp),
        shape = RoundedCornerShape(
            topStart = if (isHome) 24.dp else 8.dp,
            topEnd = if (isHome) 8.dp else 24.dp,
            bottomStart = if (isHome) 24.dp else 8.dp,
            bottomEnd = if (isHome) 8.dp else 24.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = teamColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isHome) Arrangement.Start else Arrangement.End
        ) {
            if (isHome) {
                // Home team: Icon - Name - Time
                GoalIcon(goal)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = goal.playerName,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = goal.minute,
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            } else {
                // Away team: Time - Name - Icon
                Text(
                    text = goal.minute,
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = goal.playerName,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.width(8.dp))
                GoalIcon(
                    goal = goal
                )
            }
        }
    }
}


@Composable
private fun GoalIcon() {
    Surface(
        modifier = Modifier.size(24.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Icon bóng đá hoặc X như trong hình
            Text(
                text = "⚽", // Có thể thay bằng Icon hoặc drawable
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun GoalIcon(goal: Goal) {
    Surface(
        modifier = Modifier.size(24.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                goal.isOwnGoal -> Text("⚽", fontSize = 10.sp, color = Color.Red)
                goal.isPenalty -> Text(
                    "P",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                goal.isRedCard -> Text("🟥", fontSize = 10.sp)
                goal.isYellowCard -> Text("🟨", fontSize = 10.sp)
                else -> Text("⚽", fontSize = 10.sp, color = Color.White)
            }
        }
    }
}

data class Goal(
    val playerName: String,
    val minute: String,
    val isOwnGoal: Boolean = false,
    val isPenalty: Boolean = false,
    val isRedCard: Boolean = false,
    val isYellowCard: Boolean = false
)
