package com.jerry.ronaldo.siufascore.data.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jerry.ronaldo.siufascore.data.source.FacebookAuthDataSource
import com.jerry.ronaldo.siufascore.data.source.FirebaseAuthDataSource
import com.jerry.ronaldo.siufascore.data.source.GoogleAuthDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthDataSourceModule {

    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(
        firebaseAuth: FirebaseAuth,
    ): FirebaseAuthDataSource {
        return FirebaseAuthDataSource(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideGoogleAuthDataSource(
        @ApplicationContext context: Context
    ): GoogleAuthDataSource {
        return GoogleAuthDataSource(context)
    }

    @Provides
    @Singleton
    fun provideFacebookAuthDataSource(): FacebookAuthDataSource {
        return FacebookAuthDataSource()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFireStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}