package com.jerry.ronaldo.siufascore.utils

import androidx.compose.ui.graphics.Color
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.data.repository.HighlightRepositoryImpl.Companion.CL_PLAYLIST
import com.jerry.ronaldo.siufascore.data.repository.HighlightRepositoryImpl.Companion.LALIGA_PLAYLIST
import com.jerry.ronaldo.siufascore.data.repository.HighlightRepositoryImpl.Companion.PREMIER_LEAGUE_PLAYLIST
import com.jerry.ronaldo.siufascore.data.repository.HighlightRepositoryImpl.Companion.SERIE_A_PLAYLIST
import com.jerry.ronaldo.siufascore.presentation.ui.Purple

object LeagueData {
    val leagues = listOf(
        League(
            id = "premier_league",
            name = "Premier League",
            iconRes = R.drawable.premier_league,
            country = "England",
            color = Purple
        ),
        League(
            id = "la_liga",
            name = "La Liga",
            iconRes = R.drawable.laliga,
            country = "Spain",
            color = Color(0xFFFF6900)
        ),
        League(
            id = "serie_a",
            name = "Serie A",
            iconRes = R.drawable.seria,
            country = "Italy",
            color = Color(0xFF0066CC)
        ),
        League(
            id = "champions_league",
            name = "Champions League",
            iconRes = R.drawable.uefa_champions_league_,
            country = "Europe",
            color = Color(0xFF0B1426)
        ),
    )
    fun getPlaylistById(leagueId:String):String{
        return when(leagueId){
            "premier_league" -> PREMIER_LEAGUE_PLAYLIST
            "la_liga" -> LALIGA_PLAYLIST
            "serie_a" -> SERIE_A_PLAYLIST
            "champions_league" -> CL_PLAYLIST
            else -> PREMIER_LEAGUE_PLAYLIST
        }
    }
}


// 1. Data Models
data class League(
    val id: String,
    val name: String,
    val iconRes: Int,
    val country: String,
    val color: Color = Color.Unspecified
)