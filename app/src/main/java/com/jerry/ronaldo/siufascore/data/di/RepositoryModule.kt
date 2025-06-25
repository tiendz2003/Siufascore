package com.jerry.ronaldo.siufascore.data.di

import com.jerry.ronaldo.siufascore.data.repository.HighlightRepositoryImpl
import com.jerry.ronaldo.siufascore.data.repository.MatchRepositoryImpl
import com.jerry.ronaldo.siufascore.data.repository.NewsRepositoryImpl
import com.jerry.ronaldo.siufascore.domain.repository.HighlightRepository
import com.jerry.ronaldo.siufascore.domain.repository.MatchRepository
import com.jerry.ronaldo.siufascore.domain.repository.NewsRepository
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
        matchRepositoryImpl: MatchRepositoryImpl
    ): MatchRepository

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
    abstract fun bindNetworkMonitor(
        connectivityManagerNetworkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor
}
