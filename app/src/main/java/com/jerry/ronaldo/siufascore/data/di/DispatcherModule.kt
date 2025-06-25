package com.jerry.ronaldo.siufascore.data.di

import com.jerry.ronaldo.siufascore.utils.DefaultDispatcher
import com.jerry.ronaldo.siufascore.utils.IODispatcher
import com.jerry.ronaldo.siufascore.utils.MainDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @IODispatcher
    @Provides
    fun provideIODispatcher() = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun provideMainDispatcher() = Dispatchers.Main

    @DefaultDispatcher
    @Provides
    fun provideDefaultDispatcher() = Dispatchers.Default + SupervisorJob()

}