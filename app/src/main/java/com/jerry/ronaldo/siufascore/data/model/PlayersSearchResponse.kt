package com.jerry.ronaldo.siufascore.data.model


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class PlayersSearchResponse(
    @SerialName("errors")
    val errors: List<String?>,
    @SerialName("get")
    val `get`: String,
    @SerialName("paging")
    val paging: Paging,
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
    ){
        val hasNextPage: Boolean get() = current < total
        val hasPreviousPage: Boolean get() = current > 1
    }


    @Keep
    @Serializable
    data class Response(
        @SerialName("player")
        val player: Player
    ) {
        @Keep
        @Serializable
        data class Player(
            @SerialName("age")
            val age: Int?,
            @SerialName("birth")
            val birth: Birth?,
            @SerialName("firstname")
            val firstname: String?,
            @SerialName("height")
            val height: String?,
            @SerialName("id")
            val id: Int,
            @SerialName("lastname")
            val lastname: String?,
            @SerialName("name")
            val name: String,
            @SerialName("nationality")
            val nationality: String?,
            @SerialName("number")
            val number: Int?,
            @SerialName("photo")
            val photo: String?,
            @SerialName("position")
            val position: String?,
            @SerialName("weight")
            val weight: String?
        ) {
            @Keep
            @Serializable
            data class Birth(
                @SerialName("country")
                val country: String?,
                @SerialName("date")
                val date: String?,
                @SerialName("place")
                val place: String?
            )
        }
    }
}