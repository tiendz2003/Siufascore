package com.jerry.ronaldo.siufascore.utils

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class FootballAuthorizationInterceptor @Inject constructor(
    @FootBallClientId
    private val clientId: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.request().newBuilder()
            .addHeader("x-rapidapi-key", clientId)
            .addHeader("x-rapidapi-host", "v3.football.api-sports.io")
            .build().let(chain::proceed)
    }
}

class YoutubeAuthorizationInterceptor @Inject constructor(
    @YoutubeClientId
    private val clientId: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.newBuilder()
            .addQueryParameter("key", clientId)
            .build()
        val newRequest = originalRequest.newBuilder().url(url).build()
        return chain.proceed(newRequest)
    }
}
