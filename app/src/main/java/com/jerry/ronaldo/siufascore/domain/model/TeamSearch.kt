package com.jerry.ronaldo.siufascore.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TeamSearch(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String?,
    val venueName: String?,
)
@Serializable
data class PlayerSearch(
    val id: Int,
    val name: String,
    val firstname: String?,
    val lastname: String?,
    val age: Int?,
    val birth: PlayerBirth?,
    val nationality: String?,
    val height: String?,
    val weight: String?,
    val injured: Boolean? = null,
    val number: Int? = null,
    val position: String? = null,
    val photo: String?
) {
    val displayName: String get() = name.ifEmpty { "$firstname $lastname".trim() }
    val photoUrl: String get() = photo ?: "https://media.api-sports.io/football/players/$id.png"
    val ageDisplay: String get() = age?.let { "$it tuổi" } ?: "Không rõ tuổi"
    val physicalInfo: String get() = listOfNotNull(height, weight).joinToString(" • ")
}

@Serializable
data class PlayerBirth(
    val date: String?,
    val place: String?,
    val country: String?
) {
    val birthInfo: String get() = listOfNotNull(place, country).joinToString(", ")
}
