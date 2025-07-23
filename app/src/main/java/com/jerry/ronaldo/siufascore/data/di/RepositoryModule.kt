package com.jerry.ronaldo.siufascore.data.di

import com.jerry.ronaldo.siufascore.data.remote.AuthRepository
import com.jerry.ronaldo.siufascore.data.repository.AuthRepositoryImpl
import com.jerry.ronaldo.siufascore.data.repository.FavoriteTeamsRepositoryImpl
import com.jerry.ronaldo.siufascore.data.repository.FootballRepositoryImpl
import com.jerry.ronaldo.siufascore.data.repository.HighlightRepositoryImpl
import com.jerry.ronaldo.siufascore.data.repository.LiveStreamRepositoryImpl
import com.jerry.ronaldo.siufascore.data.repository.NewsRepositoryImpl
import com.jerry.ronaldo.siufascore.data.repository.SearchRepositoryImpl
import com.jerry.ronaldo.siufascore.domain.repository.FavoriteTeamsRepository
import com.jerry.ronaldo.siufascore.domain.repository.FootballRepository
import com.jerry.ronaldo.siufascore.domain.repository.HighlightRepository
import com.jerry.ronaldo.siufascore.domain.repository.LiveStreamRepository
import com.jerry.ronaldo.siufascore.domain.repository.NewsRepository
import com.jerry.ronaldo.siufascore.domain.repository.SearchRepository
import com.jerry.ronaldo.siufascore.utils.ConnectivityManagerNetworkMonitor
import com.jerry.ronaldo.siufascore.utils.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMatchRepository(
        footballRepositoryImpl: FootballRepositoryImpl
    ): FootballRepository

    @Binds
    @Singleton
    abstract fun bindHighlightRepository(
        highlightRepository: HighlightRepositoryImpl
    ): HighlightRepository

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepository: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepository: SearchRepositoryImpl
    ): SearchRepository


    @Binds
    abstract fun bindNetworkMonitor(
        connectivityManagerNetworkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteTeamsRepository(
        favoriteTeamsRepositoryImpl: FavoriteTeamsRepositoryImpl
    ): FavoriteTeamsRepository
    @Binds
    @Singleton
    abstract fun bindLiveStreamRepository(
        liveStreamRepositoryImpl: LiveStreamRepositoryImpl
    ): LiveStreamRepository
}
