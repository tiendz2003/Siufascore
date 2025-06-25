package com.jerry.ronaldo.siufascore.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.jerry.ronaldo.siufascore.BuildConfig
import com.jerry.ronaldo.siufascore.data.remote.FootBallApiService
import com.jerry.ronaldo.siufascore.data.remote.NewsApiService
import com.jerry.ronaldo.siufascore.data.remote.YoutubeApiService
import com.jerry.ronaldo.siufascore.utils.FootBallClientId
import com.jerry.ronaldo.siufascore.utils.FootBallOkHttp
import com.jerry.ronaldo.siufascore.utils.FootballAuthorizationInterceptor
import com.jerry.ronaldo.siufascore.utils.FootballRetrofit
import com.jerry.ronaldo.siufascore.utils.NewsClientId
import com.jerry.ronaldo.siufascore.utils.NewsOkHttp
import com.jerry.ronaldo.siufascore.utils.NewsRetrofit
import com.jerry.ronaldo.siufascore.utils.YoutubeAuthorizationInterceptor
import com.jerry.ronaldo.siufascore.utils.YoutubeClientId
import com.jerry.ronaldo.siufascore.utils.YoutubeOkHttp
import com.jerry.ronaldo.siufascore.utils.YoutubeRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    //cung cấp apikey
    @Provides
    @FootBallClientId
    fun provideFootballClientId() = BuildConfig.FOOTBALL_API_KEY

    @Provides
    @YoutubeClientId
    fun provideYoutubeClientId() = BuildConfig.YOUTUBE_API_KEY

    @Provides
    @NewsClientId
    fun provideNewsClientId() = BuildConfig.NEWS_API_KEY


    @Provides
    @Singleton
    fun provideFootballAuthorizationInterceptor(
        @FootBallClientId clientId: String
    ): FootballAuthorizationInterceptor =
        FootballAuthorizationInterceptor(clientId)

    @Provides
    @Singleton
    fun provideYoutubeAuthorizationInterceptor(
        @YoutubeClientId clientId: String
    ): YoutubeAuthorizationInterceptor =
        YoutubeAuthorizationInterceptor(clientId)


    @Provides
    @Singleton
    @FootBallOkHttp
    fun provideFootballOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authorizationInterceptor: FootballAuthorizationInterceptor
    ): OkHttpClient {
        return provideOkHttpClient(
            httpLoggingInterceptor = httpLoggingInterceptor,
            authorizationInterceptor = authorizationInterceptor
        )
    }

    @Provides
    @Singleton
    @YoutubeOkHttp
    fun provideYoutubeOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authorizationInterceptor: YoutubeAuthorizationInterceptor
    ): OkHttpClient {
        return provideOkHttpClient(
            httpLoggingInterceptor = httpLoggingInterceptor,
            authorizationInterceptor = authorizationInterceptor
        )
    }

    @Provides
    @Singleton
    @NewsOkHttp
    fun provideNewsOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addNetworkInterceptor(httpLoggingInterceptor)
            .build()
    }

    private fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authorizationInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addNetworkInterceptor(httpLoggingInterceptor)
            .addInterceptor(authorizationInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @Singleton
    @FootballRetrofit
    fun provideFootballRetrofit(
        @FootBallOkHttp okHttpClient: OkHttpClient, json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://api.football-data.org/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    @YoutubeRetrofit
    fun provideYoutubeRetrofit(
        @YoutubeOkHttp okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    @NewsRetrofit
    fun provideNewsRetrofit(
        @NewsOkHttp okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://eventregistry.org/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideFootballApiService(@FootballRetrofit retrofit: Retrofit): FootBallApiService {
        return retrofit.create(FootBallApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideYoutubeApiService(@YoutubeRetrofit retrofit: Retrofit): YoutubeApiService {
        return retrofit.create(YoutubeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsApiService(@NewsRetrofit retrofit: Retrofit): NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
            encodeDefaults = true
        }
    }
}