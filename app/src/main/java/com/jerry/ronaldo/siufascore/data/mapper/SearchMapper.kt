package com.jerry.ronaldo.siufascore.data.mapper

import com.jerry.ronaldo.siufascore.data.model.PlayersSearchResponse
import com.jerry.ronaldo.siufascore.data.model.TeamsSearchResponse
import com.jerry.ronaldo.siufascore.domain.model.PlayerBirth
import com.jerry.ronaldo.siufascore.domain.model.PlayerSearch
import com.jerry.ronaldo.siufascore.domain.model.TeamSearch

fun TeamsSearchResponse.toDomain(): List<TeamSearch> {
    return response.map { team ->
        TeamSearch(
            id = team.team.id,
            name = team.team.name,
            country = team.team.country,
            logo = team.team.logo,
            venueName = team.venue.name
        )
    }
}

fun PlayersSearchResponse.toDomain(): List<PlayerSearch> {
    return response.map { player ->
        PlayerSearch(
            id = player.player.id,
            name = player.player.name,
            firstname = player.player.firstname,
            lastname = player.player.lastname,
            age = player.player.age,
            birth = player.player.birth?.toDomain(),
            nationality = player.player.nationality,
            height = player.player.height,
            weight = player.player.weight,
            number = player.player.number,
            position = player.player.position,
            photo = player.player.photo,
        )
    }
}

fun PlayersSearchResponse.Response.Player.Birth.toDomain():PlayerBirth {
    return PlayerBirth(
        date = this.date,
        place = this.place,
        country = this.country
    )
}