package com.jerry.ronaldo.siufascore.data.model


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class TeamsSearchResponse(
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
        @SerialName("search")
        val search: String
    )

    @Keep
    @Serializable
    data class Response(
        @SerialName("team")
        val team: Team,
        @SerialName("venue")
        val venue: Venue
    ) {
        @Keep
        @Serializable
        data class Team(
            @SerialName("code")
            val code: String?,
            @SerialName("country")
            val country: String,
            @SerialName("founded")
            val founded: Int?,
            @SerialName("id")
            val id: Int,
            @SerialName("logo")
            val logo: String,
            @SerialName("name")
            val name: String,
            @SerialName("national")
            val national: Boolean
        )

        @Keep
        @Serializable
        data class Venue(
            @SerialName("address")
            val address: String?,
            @SerialName("capacity")
            val capacity: Int?,
            @SerialName("city")
            val city: String?,
            @SerialName("id")
            val id: Int?,
            @SerialName("image")
            val image: String?,
            @SerialName("name")
            val name: String?,
            @SerialName("surface")
            val surface: String?
        )
    }
}

