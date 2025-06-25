package com.jerry.ronaldo.siufascore.data.model


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CurrentRoundResponse(
    @SerialName("errors")
    val errors: List<String?>,
    @SerialName("get")
    val `get`: String,
    @SerialName("parameters")
    val parameters: Parameters,
    @SerialName("response")
    val response: List<String>,
) {

    @Keep
    @Serializable
    data class Parameters(
        @SerialName("current")
        val current: String,
        @SerialName("league")
        val league: String,
        @SerialName("season")
        val season: String
    )
}