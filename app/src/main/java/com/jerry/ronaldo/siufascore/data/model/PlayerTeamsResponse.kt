package com.jerry.ronaldo.siufascore.data.model


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class PlayerTeamsResponse(
    @SerialName("errors")
    val errors: List<String?>,
    @SerialName("get")
    val `get`: String,
    @SerialName("paging")
    val paging: Paging,
    @SerialName("parameters")
    val parameters: Parameters,
    @SerialName("response")
    val response: List<Response>,
    @SerialName("results")
    val results: Int
) {
    @Keep
    @Serializable
    data class Paging(
        @SerialName("current")
        val current: Int,
        @SerialName("total")
        val total: Int
    )

    @Keep
    @Serializable
    data class Parameters(
        @SerialName("player")
        val player: String
    )

    @Keep
    @Serializable
    data class Response(
        @SerialName("seasons")
        val seasons: List<Int>,
        @SerialName("team")
        val team: Team
    ) {
        @Keep
        @Serializable
        data class Team(
            @SerialName("id")
            val id: Int,
            @SerialName("logo")
            val logo: String,
            @SerialName("name")
            val name: String
        )
    }
}