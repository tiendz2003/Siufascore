package com.jerry.ronaldo.siufascore.data.mapper


import com.jerry.ronaldo.siufascore.data.model.PlayerDetailResponse
import com.jerry.ronaldo.siufascore.data.model.PlayerTeamsResponse
import com.jerry.ronaldo.siufascore.data.model.PlayerTrophiesResponse
import com.jerry.ronaldo.siufascore.domain.model.CardStats
import com.jerry.ronaldo.siufascore.domain.model.DribbleStats
import com.jerry.ronaldo.siufascore.domain.model.DuelStats
import com.jerry.ronaldo.siufascore.domain.model.FoulStats
import com.jerry.ronaldo.siufascore.domain.model.GameStats
import com.jerry.ronaldo.siufascore.domain.model.GoalStats
import com.jerry.ronaldo.siufascore.domain.model.LeagueInfo
import com.jerry.ronaldo.siufascore.domain.model.PassStats
import com.jerry.ronaldo.siufascore.domain.model.PenaltyStats
import com.jerry.ronaldo.siufascore.domain.model.PlayerBirth
import com.jerry.ronaldo.siufascore.domain.model.PlayerSearch
import com.jerry.ronaldo.siufascore.domain.model.PlayerSeasonStats
import com.jerry.ronaldo.siufascore.domain.model.PlayerStatistics
import com.jerry.ronaldo.siufascore.domain.model.PlayerTeam
import com.jerry.ronaldo.siufascore.domain.model.PlayerTrophy
import com.jerry.ronaldo.siufascore.domain.model.ShotStats
import com.jerry.ronaldo.siufascore.domain.model.SubstituteStats
import com.jerry.ronaldo.siufascore.domain.model.TackleStats
import com.jerry.ronaldo.siufascore.domain.model.TeamInfo


fun PlayerDetailResponse.mapToDomain(): PlayerStatistics {
    // API trả về một danh sách các response, nhưng chúng ta chỉ cần cái đầu tiên
    // vì đang truy vấn theo ID và mùa giải cụ thể.
    val playerResponse = this.response.firstOrNull()
        ?: throw NoSuchElementException("Player data not found in API response.")

    val playerSearch = playerResponse.player.toDomainPlayerSearch()
    val seasonStats = playerResponse.statistics.map { it.toDomainPlayerSeasonStats() }

    return PlayerStatistics(
        player = playerSearch,
        statistics = seasonStats
    )
}

private fun PlayerDetailResponse.Response.Player.toDomainPlayerSearch(): PlayerSearch {
    return PlayerSearch(
        id = this.id,
        name = this.name,
        firstname = this.firstname,
        lastname = this.lastname,
        age = this.age,
        birth = this.birth.toDomainBirth(),
        nationality = this.nationality,
        height = this.height,
        weight = this.weight,
        injured = this.injured,
        photo = this.photo
    )
}

private fun PlayerDetailResponse.Response.Player.Birth.toDomainBirth(): PlayerBirth {
    return PlayerBirth(
        date = this.date,
        place = this.place,
        country = this.country
    )
}

private fun PlayerDetailResponse.Response.Statistic.toDomainPlayerSeasonStats(): PlayerSeasonStats {
    return PlayerSeasonStats(
        team = this.team.toDomainTeamInfo(),
        league = this.league.toDomainLeagueInfo(),
        games = this.games.toDomainGameStats(),
        substitutes = this.substitutes.toDomainSubstituteStats(),
        shots = this.shots.toDomainShotStats(),
        goals = this.goals.toDomainGoalStats(),
        passes = this.passes.toDomainPassStats(),
        tackles = this.tackles.toDomainTackles(),
        duels = this.duels.toDomainDuelStats(),
        dribbles = this.dribbles.toDomainDribbleStats(),
        fouls = this.fouls.toDomainFoulStats(),
        cards = this.cards.toDomainCardStats(),
        penalty = this.penalty.toDomainPenaltyStats()
    )
}

// Các hàm private nhỏ để chuyển đổi từng phần
private fun PlayerDetailResponse.Response.Statistic.Team.toDomainTeamInfo() =
    TeamInfo(id, name, logo)

private fun PlayerDetailResponse.Response.Statistic.League.toDomainLeagueInfo() =
    LeagueInfo(id ?: 0, name, country, logo, flag, season)

private fun PlayerDetailResponse.Response.Statistic.Games.toDomainGameStats() =
    GameStats(appearences, lineups, minutes, number, position, rating, captain)

private fun PlayerDetailResponse.Response.Statistic.Substitutes.toDomainSubstituteStats() =
    SubstituteStats(inX, out, bench)

private fun PlayerDetailResponse.Response.Statistic.Shots.toDomainShotStats() = ShotStats(total, on)
private fun PlayerDetailResponse.Response.Statistic.Goals.toDomainGoalStats() =
    GoalStats(total, conceded, assists, saves)

private fun PlayerDetailResponse.Response.Statistic.Passes.toDomainPassStats() =
    PassStats(total, key, accuracy)

private fun PlayerDetailResponse.Response.Statistic.Tackles.toDomainTackles() =
    TackleStats(total, blocks, interceptions)

private fun PlayerDetailResponse.Response.Statistic.Duels.toDomainDuelStats() =
    DuelStats(total, won)

private fun PlayerDetailResponse.Response.Statistic.Dribbles.toDomainDribbleStats() =
    DribbleStats(attempts, success, past)

private fun PlayerDetailResponse.Response.Statistic.Fouls.toDomainFoulStats() =
    FoulStats(drawn, committed)

private fun PlayerDetailResponse.Response.Statistic.Cards.toDomainCardStats() =
    CardStats(yellow, yellowred, red)

private fun PlayerDetailResponse.Response.Statistic.Penalty.toDomainPenaltyStats() =
    PenaltyStats(won, commited, scored, missed, saved)
fun PlayerTeamsResponse.Response.toDomainModel(): PlayerTeam {
    return PlayerTeam(
        teamId = team.id,
        teamName = team.name,
        teamLogo = team.logo,
        seasons = seasons
    )
}

fun PlayerTrophiesResponse.Response.toDomainModel(): PlayerTrophy {
    return PlayerTrophy(
        country = country,
        league = league,
        place = place,
        season = season
    )
}

fun PlayerTeamsResponse.toDomainModel(): List<PlayerTeam> {
    return response.map { it.toDomainModel() }
}

fun PlayerTrophiesResponse.toDomainModel(): List<PlayerTrophy> {
    return response.map { it.toDomainModel() }
}