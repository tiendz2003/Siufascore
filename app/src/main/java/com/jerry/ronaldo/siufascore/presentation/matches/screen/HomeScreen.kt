package com.jerry.ronaldo.siufascore.presentation.matches.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.presentation.matches.MatchesIntent
import com.jerry.ronaldo.siufascore.presentation.matches.MatchesViewModel
import com.jerry.ronaldo.siufascore.presentation.matches.screen.item.CompetitionSelectorItem
import com.jerry.ronaldo.siufascore.presentation.matches.screen.item.MatchItem
import com.jerry.ronaldo.siufascore.presentation.matches.screen.item.MatchLiveItem
import com.jerry.ronaldo.siufascore.presentation.ui.Purple
import com.jerry.ronaldo.siufascore.utils.MatchStatus
import com.jerry.ronaldo.siufascore.utils.extractDateFromUtc
import com.jerry.ronaldo.siufascore.utils.formatDisplayDate
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun HomeScreen(onMatchClick: (String) -> Unit, viewmodel: MatchesViewModel = hiltViewModel()) {
    val tabs = listOf("Lịch thi đấu", "Bảng xếp hạng")
    val pagerState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        CompetitionSelector(
            selectedCompetitionId = uiState.competitionId ?: "PL",
            onCompetitionSelected = { competitionId ->
                Timber.tag("HomeScreen").d("Competition selected: $competitionId")
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
                            homeScore = match.score.fullTime.home,
                            awayScore = match.score.fullTime.away,
                            matchTime = "45'+2",
                            venue = match.area.name,
                            status = MatchStatus.from(match.status)!!,
                            onMatchClick = {}
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
                .heightIn(min = 400.dp, max = 850.dp)
        ) { page ->
            when (page) {
                0 -> MatchesScreen(viewmodel)
                else -> StandingScreen()
            }
        }

    }

}


@Composable
fun MatchesScreen(viewmodel: MatchesViewModel) {
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewmodel.processIntent(MatchesIntent.LoadMatchesByLeague("PL"))
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF00BFFF),
                            Color(0xFF1E90FF)
                        )
                    )
                )
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = "Vòng ${uiState.currentMatchday ?: "0"}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 28.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        MatchList(uiState.matches)
    }
}

@Composable
fun MatchList(matches: List<Match>) {
    Timber.tag("MatchesScreen").d("Matches: $matches")
    //sắp xếp lại các trận đấu theo ngày(sử dụng group by để nhóm)
    val groupMatches = matches.groupBy { match ->
        match.utcDate.extractDateFromUtc()
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
                    fontSize = 20.sp,
                    color = Purple,
                    textAlign = TextAlign.Center,
                )
            }
            matchesForDate.forEach { match ->
                MatchItem(
                    match = match
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
    selectedCompetitionId: String,
    onCompetitionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    val competitions = remember {
        listOf(
            CompetitionSelector(
                id = "PL",
                logo = R.drawable.premier_league,
                name = "Premier League"
            ),
            CompetitionSelector(
                id = "SA",
                logo = R.drawable.seria,
                name = "Seria A"
            ),
            CompetitionSelector(
                id = "CL",
                logo = R.drawable.uefa_champions_league_,
                name = "Champions League"
            ),
            CompetitionSelector(
                id = "PD",
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
                        onCompetitionSelected(competition.id)
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
    val id: String,
    @DrawableRes val logo: Int,
    val name: String
)