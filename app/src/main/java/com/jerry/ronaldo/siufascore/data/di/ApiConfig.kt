package com.jerry.ronaldo.siufascore.data.di

data class ApiConfig(
    val baseUrl:String,
    val apiKey:String
)
data class YoutubeApiConfig(
    val baseUrl:String = "https://www.googleapis.com/youtube/v3/",
    val apiKey:String
)