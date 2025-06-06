package com.jerry.ronaldo.siufascore.data.di

import com.jerry.ronaldo.siufascore.data.repository.MatchRepositoryImpl
import com.jerry.ronaldo.siufascore.domain.repository.MatchRepository
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
    abstract fun bindNetworkMonitor(
        connectivityManagerNetworkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor
}
