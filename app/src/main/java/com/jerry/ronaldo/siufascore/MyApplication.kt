package com.jerry.ronaldo.siufascore

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.jerry.ronaldo.siufascore.data.source.GoogleAuthDataSource
import com.jerry.ronaldo.siufascore.utils.AuthConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), SingletonImageLoader.Factory {
    @Inject
    lateinit var googleAuthDataSource: GoogleAuthDataSource
    override fun onCreate() {
        super.onCreate()
        /*FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)*/
        // Initialize Google Sign-In
        googleAuthDataSource.initialize(AuthConfig.GOOGLE_WEB_CLIENT_ID)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25) // 25% RAM
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50MB
                    .build()
            }
            .build()
    }
}