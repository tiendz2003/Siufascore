package com.jerry.ronaldo.siufascore.presentation.search.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pages
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.domain.model.PlayerSearch
import com.jerry.ronaldo.siufascore.domain.model.TeamSearch
import com.jerry.ronaldo.siufascore.presentation.search.SearchEvent
import com.jerry.ronaldo.siufascore.presentation.search.SearchIntent
import com.jerry.ronaldo.siufascore.presentation.search.SearchType
import com.jerry.ronaldo.siufascore.presentation.search.SearchViewModel
import com.jerry.ronaldo.siufascore.presentation.search.item.DefaultSearchContent
import com.jerry.ronaldo.siufascore.presentation.search.item.SearchEmptyContent
import com.jerry.ronaldo.siufascore.presentation.search.item.SearchLoadingContent
import com.jerry.ronaldo.siufascore.presentation.search.item.TeamSearchItem
import com.jerry.ronaldo.siufascore.presentation.ui.Purple
import com.jerry.ronaldo.siufascore.presentation.ui.Purple80
import com.jerry.ronaldo.siufascore.presentation.ui.TypeFilterChips
import com.jerry.ronaldo.siufascore.utils.getLeagueIdFromCountry

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onPlayerClick: (Int) -> Unit,
    onTeamClick: (Int, Int) -> Unit
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    val isExpanded by searchViewModel.expandedClubIds.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        searchViewModel.singleEvent.collect { effect ->
            when (effect) {
                is SearchEvent.NavigateToTeamDetail -> {
                    onTeamClick(effect.teamId, effect.leagueId)
                }

                is SearchEvent.ShowSnackbar -> {}
                is SearchEvent.NavigateToPlayerDetail -> {
                    onPlayerClick(effect.playerId)
                }
            }
        }
    }
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        SearchTopBar(
            searchQuery = searchQuery,
            onQueryChanged = { query ->
                searchQuery = query
                searchViewModel.sendIntent(SearchIntent.UpdateQuery(query))
            },
            onBackClick = onBackClick,
            onSearch = { query ->
                searchViewModel.sendIntent(SearchIntent.Search(query))
            },
            onClear = {
                searchQuery = ""
                searchViewModel.sendIntent(SearchIntent.ClearSearch)
            },
            modifier = Modifier.statusBarsPadding()
        )
        TypeFilterChips(
            items = SearchType.entries,
            selectedType = uiState.selectedSearchType,
            onTypeSelected = {type->
                searchViewModel.sendIntent(SearchIntent.ChangedSearchType(type))
            },
            icon = { type->
                type.icon
            },
        )
        when {
            uiState.isLoading && uiState.query.isNotEmpty() -> {
                SearchLoadingContent(
                    searchType = uiState.selectedSearchType
                )
            }

            uiState.hasResults -> {
                when (uiState.selectedSearchType) {
                    SearchType.TEAMS -> {
                        SearchResultsContent(
                            teams = uiState.teams,
                            onTeamClick = { team ->
                                searchViewModel.sendIntent(
                                    SearchIntent.NavigateToTeamDetail(
                                        team.id,
                                        team.country.getLeagueIdFromCountry() ?: 0
                                    )
                                )
                            }
                        )
                    }

                    SearchType.PLAYERS -> {
                        PlayersResultsContent(
                            players = uiState.players,
                            canLoadMore = uiState.canLoadMorePlayers,
                            isLoadingMore = uiState.isLoadingMorePlayers,
                            showPagination = uiState.showPagination,
                            currentPage = uiState.currentPage,
                            totalPages = uiState.totalPages,
                            resultsInfo = uiState.resultsInfo,
                            pageInfo = uiState.pageInfo,
                            isExpanded = isExpanded,
                            onPlayerClick = { playerId ->
                                searchViewModel.sendIntent(
                                    SearchIntent.NavigateToPlayerDetail(
                                        playerId
                                    )
                                )
                            },
                            onLoadMore = {
                                searchViewModel.sendIntent(
                                    SearchIntent.LoadMorePlayers(uiState.query)
                                )
                            },
                            onPageClick = { page ->
                                searchViewModel.sendIntent(
                                    SearchIntent.LoadPlayersPage(uiState.query, page)
                                )
                            },
                            onExpanded = { id ->
                                searchViewModel.onClubClicked(id)
                            }
                        )
                    }
                }
            }

            uiState.showEmptyState -> {
                SearchEmptyContent(query = uiState.query, searchType = uiState.selectedSearchType)
            }

            else -> {
                DefaultSearchContent()
            }
        }
    }
}

@Composable
private fun SearchTopBar(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChanged,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Tìm câu lạc bộ,cầu thủ,...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClear) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearch(searchQuery) }
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.5f
                    ),
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = Purple,
                    focusedContainerColor = Purple80,
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun SearchResultsContent(
    teams: List<TeamSearch>,
    onTeamClick: (TeamSearch) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Tìm thấy ${teams.size} câu lạc bộ",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            items = teams,
            key = { it.id }
        ) { team ->
            TeamSearchItem(
                team = team,
                onClick = { onTeamClick(team) }
            )
        }
    }
}


@Composable
fun PlayersResultsContent(
    players: List<PlayerSearch>,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    showPagination: Boolean,
    currentPage: Int,
    totalPages: Int,
    isExpanded: Set<Int>,
    resultsInfo: String,
    pageInfo: String,
    onExpanded: (Int) -> Unit,
    onPlayerClick: (Int) -> Unit,
    onLoadMore: () -> Unit,
    onPageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item("results_header") {
            PlayersResultsHeader(
                resultsInfo = resultsInfo,
                pageInfo = pageInfo,
                showPagination = showPagination
            )
        }
        items(
            items = players,
            key = { player -> player.id }
        ) { player ->
            PlayerSearchItem(
                player = player,
                isExpanded = isExpanded.contains(player.id),
                onExpanded = { onExpanded(player.id) },
                onClick = {
                    onPlayerClick(player.id)
                }
            )
        }
        if (canLoadMore) {
            item("load_more") {
                LoadMoreButton(
                    onClick = onLoadMore,
                    isLoading = isLoadingMore
                )
            }
        }
        if (showPagination && totalPages > 1) {
            item("pagination") {
                PaginationControls(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    onPageClick = onPageClick,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        item("bottom_spacing") {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PlayersResultsHeader(
    resultsInfo: String,
    pageInfo: String,
    showPagination: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (resultsInfo.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = resultsInfo,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (showPagination && pageInfo.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Pages,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = pageInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerPhotoSection(
    photoUrl: String,
    playerName: String,
    position: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background circle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        )

        // Player photo
        AsyncImage(
            model = photoUrl,
            contentDescription = "$playerName photo",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                ),
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            error = painterResource(R.drawable.ic_launcher_foreground),
            contentScale = ContentScale.Crop
        )

        if (!position.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(
                        color = getPositionColor(position),
                        shape = CircleShape
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = getPositionShort(position),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun PlayerSearchItem(
    player: PlayerSearch,
    onExpanded: () -> Unit,
    onClick: () -> Unit,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    val rotationIcon by animateFloatAsState(
        targetValue = if (isExpanded) 270f else 0f,
        label = "arrow rotation"
    )
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onExpanded() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp,
            pressedElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = onExpanded
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player photo
                PlayerPhotoSection(
                    photoUrl = player.photoUrl,
                    playerName = player.displayName,
                    position = player.position
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Player info
                PlayerInfoSection(
                    player = player,
                    modifier = Modifier.weight(1f)
                )

                // Arrow icon
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "View details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(32.dp)
                        .rotate(rotationIcon)
                )
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val goals by remember { mutableIntStateOf((10..200).random()) }
                PlayerDetailsContent(
                    nationality = player.nationality ?: "Không rõ ",
                    position = player.position ?: "Không rõ",
                    age = player.age ?: 0,
                    number = player.number ?: 0,
                    weight = player.weight ?: "Không rõ",
                    goals = goals,
                    onFullBioClick = {
                        onClick()
                    },
                )
            }
        }
    }
}

@Composable
fun PlayerDetailsContent(
    nationality: String,
    position: String,
    age: Int,
    number: Int,
    weight: String,
    goals: Int,
    onFullBioClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val cardElevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 8.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_elevation"
    )

    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(cardScale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D1B69)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Top section with enhanced styling
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatColumn(
                        label = "Quốc tịch",
                        value = nationality,
                        modifier = Modifier.weight(1f)
                    )

                    StatColumn(
                        label = "Vị trí",
                        value = position,
                        modifier = Modifier.weight(1f)
                    )

                    StatColumn(
                        label = "Tuổi",
                        value = age.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Bottom section with performance stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PerformanceStatColumn(
                        label = "Appearances",
                        value = number.toString(),
                        icon = Icons.Default.EmojiEvents,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )

                    PerformanceStatColumn(
                        label = "Goals",
                        value = weight,
                        icon = Icons.Default.Sports,
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f)
                    )

                    PerformanceStatColumn(
                        label = "Assists",
                        value = goals.toString(),
                        icon = Icons.Default.Favorite,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Enhanced Full Bio Button
                Button(
                    onClick = onFullBioClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A3A8A)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = "Xem chi tiết",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PerformanceStatColumn(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            style = MaterialTheme.typography.titleLarge.copy(
                letterSpacing = 0.5.sp,
                fontSize = 14.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            style = MaterialTheme.typography.titleLarge.copy(
                letterSpacing = 0.5.sp,
                fontSize = 14.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leadingIcon?.invoke()

            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
private fun PlayerInfoSection(
    player: PlayerSearch,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Player name
        Text(
            text = player.displayName,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Position and nationality
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!player.position.isNullOrEmpty()) {
                Text(
                    text = player.position,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = getPositionColor(player.position),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!player.nationality.isNullOrEmpty()) {
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!player.nationality.isNullOrEmpty()) {
                Text(
                    text = player.nationality,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Age and physical info
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (player.age != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = player.ageDisplay,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (player.physicalInfo.isNotEmpty()) {
                if (player.age != null) {
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = player.physicalInfo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Blue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Birth info (if available)
        player.birth?.let { birth ->
            if (birth.birthInfo.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = birth.birthInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadMoreButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Loading more players...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = "Load More Players",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Page Navigation",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                // Previous button
                item {
                    PageButton(
                        text = "‹",
                        isSelected = false,
                        isEnabled = currentPage > 1,
                        onClick = { onPageClick(currentPage - 1) }
                    )
                }

                // Page numbers
                items(getPaginationRange(currentPage, totalPages)) { page ->
                    if (page == -1) {
                        // Ellipsis
                        Text(
                            text = "...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                    } else {
                        PageButton(
                            text = page.toString(),
                            isSelected = page == currentPage,
                            isEnabled = true,
                            onClick = { onPageClick(page) }
                        )
                    }
                }

                // Next button
                item {
                    PageButton(
                        text = "›",
                        isSelected = false,
                        isEnabled = currentPage < totalPages,
                        onClick = { onPageClick(currentPage + 1) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PageButton(
    text: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.size(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            },
            contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

// Helper functions
private fun getPositionColor(position: String): Color {
    return when (position.lowercase()) {
        "goalkeeper" -> Color(0xFF4CAF50)
        "defender" -> Color(0xFF2196F3)
        "midfielder" -> Color(0xFFFFC107)
        "attacker" -> Color(0xFFE53E3E)
        else -> Color(0xFF9E9E9E)
    }
}

private fun getPositionShort(position: String): String {
    return when (position.lowercase()) {
        "goalkeeper" -> "GK"
        "defender" -> "DEF"
        "midfielder" -> "MID"
        "attacker" -> "ATT"
        else -> "N/A"
    }
}

private fun getPaginationRange(currentPage: Int, totalPages: Int): List<Int> {
    if (totalPages <= 7) {
        return (1..totalPages).toList()
    }

    val result = mutableListOf<Int>()

    // Always show first page
    result.add(1)

    when {
        currentPage <= 4 -> {
            // Show 1, 2, 3, 4, 5, ..., totalPages
            result.addAll(2..5)
            result.add(-1) // Ellipsis
            result.add(totalPages)
        }

        currentPage >= totalPages - 3 -> {
            // Show 1, ..., totalPages-4, totalPages-3, totalPages-2, totalPages-1, totalPages
            result.add(-1) // Ellipsis
            result.addAll((totalPages - 4)..totalPages)
        }

        else -> {
            // Show 1, ..., currentPage-1, currentPage, currentPage+1, ..., totalPages
            result.add(-1) // Ellipsis
            result.addAll((currentPage - 1)..(currentPage + 1))
            result.add(-1) // Ellipsis
            result.add(totalPages)
        }
    }

    return result.distinct()
}
