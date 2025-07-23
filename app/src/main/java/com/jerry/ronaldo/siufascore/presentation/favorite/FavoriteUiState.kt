package com.jerry.ronaldo.siufascore.presentation.favorite

import androidx.annotation.Keep
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.jerry.ronaldo.siufascore.base.Intent
import com.jerry.ronaldo.siufascore.base.SingleEvent
import com.jerry.ronaldo.siufascore.base.ViewState
import com.jerry.ronaldo.siufascore.data.model.FavoriteTeam
import com.jerry.ronaldo.siufascore.utils.Chip

data class FavoriteUiState(
    val favoriteTeamsByLeague: Map<String, List<FavoriteTeam>> = emptyMap(),
    val selectedFavoriteType: FavoriteType = FavoriteType.DEFAULT,
    val selectedLeagueType: AvailableLeague = AvailableLeague.DEFAULT,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isTogglingNotification: Boolean = false,
    val toggledTeamId: Int? = null
) : ViewState {
    val currentTeams: List<FavoriteTeam>
        get() = favoriteTeamsByLeague[selectedLeagueType.displayName] ?: emptyList()

    val isEmpty: Boolean
        get() = favoriteTeamsByLeague.isEmpty()
    //sẽ triển khai cho player và matches sau,mặc định là true trước
    val hasResult: Boolean =true


}
sealed class FavoriteIntent : Intent {
    data class SelectLeague(val leagueName: AvailableLeague) : FavoriteIntent()
    data class ToggleNotification(val teamId: Int) : FavoriteIntent()
    data class RemoveFavoriteTeam(val teamId: Int) : FavoriteIntent()
    data class NavigateToTeamDetail(val teamId: Int, val leagueId: Int) : FavoriteIntent()
    data object Refresh : FavoriteIntent()
}

// Events
sealed interface FavoriteEvent : SingleEvent {
    data class NavigateToTeamDetail(val teamId: Int, val leagueId: Int) : FavoriteEvent
    data class ShowMessage(val message: String) : FavoriteEvent
    data object ShowNoFavoriteTeamsMessage : FavoriteEvent
}
@Keep
enum class FavoriteType(
    override val displayName: String,
    val icon: ImageVector
) : Chip {
    TEAMS("Câu lạc bộ", Icons.Default.Groups),
    PLAYERS("Cầu thủ", Icons.Default.Person),
    MATCHES("Trận đấu", Icons.Default.Event);
    companion object {
        val DEFAULT = TEAMS
    }
}
@Keep
enum class AvailableLeague(val displayName: String) {
    PREMIER_LEAGUE("Premier League"),
    SERIE_A("Serie A"),
    LA_LIGA("La Liga"),
    LIGUE_1("Ligue 1"),
    BUNDESLIGA("Bundesliga");
    companion object{
        val DEFAULT = PREMIER_LEAGUE
    }
}
