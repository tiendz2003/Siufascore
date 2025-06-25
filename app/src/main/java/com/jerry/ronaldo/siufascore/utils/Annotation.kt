package com.jerry.ronaldo.siufascore.utils

import javax.inject.Qualifier
//FOOTBALL-DATA.ORG
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FootBallOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FootballRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FootBallClientId
//YOUTUBE
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class YoutubeOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class YoutubeClientId

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class YoutubeRetrofit
//GNEWS
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NewsOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NewsClientId

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NewsRetrofit

//Coroutine Context
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher
