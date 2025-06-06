package com.jerry.ronaldo.siufascore.utils

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class FootballAuthorizationInterceptor @Inject constructor(
    @FootBallClientId
    private val clientId: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.request().newBuilder().addHeader("X-Auth-Token", clientId)
            .build().let(chain::proceed)
    }
}