package com.jerry.ronaldo.siufascore.utils

enum class ConceptNews(val conceptUri: String) {
    FOOTBALL("http://en.wikipedia.org/wiki/Association_football"),
    PREMIER_LEAGUE("http://en.wikipedia.org/wiki/Premier_League"),
    LA_LIGA("http://en.wikipedia.org/wiki/La_Liga"),
    SERIE_A("http://en.wikipedia.org/wiki/Serie_A"),
    CHAMPIONS_LEAGUE("http://en.wikipedia.org/wiki/UEFA_Champions_League"),
    CLUB("");

    fun getClubUrl(clubName: String): String {
        return "http://en.wikipedia.org/wiki/${clubName.replace(" ", "_")}\""
    }
}