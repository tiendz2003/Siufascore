package com.jerry.ronaldo.siufascore.presentation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.graphics.Insets
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jerry.ronaldo.siufascore.data.remote.AuthRepository
import com.jerry.ronaldo.siufascore.presentation.auth.AuthViewModel
import com.jerry.ronaldo.siufascore.presentation.ui.SiufascoreApp
import com.jerry.ronaldo.siufascore.presentation.ui.SiufascoreTheme
import com.jerry.ronaldo.siufascore.presentation.ui.rememberAppState
import com.jerry.ronaldo.siufascore.utils.NetworkMonitor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var authRepository: AuthRepository
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupSplashScreen(splashScreen)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            // Chỉ tiêu thụ phần navigation bar
            val remainingInsets = WindowInsetsCompat.Builder(insets)
                .setInsets(
                    WindowInsetsCompat.Type.navigationBars(),
                    Insets.NONE
                ) // Xóa navigation bar insets
                .build()
            ViewCompat.onApplyWindowInsets(v, remainingInsets)
            remainingInsets
        }

        setContent {
            val appState = rememberAppState(
                networkMonitor = networkMonitor,
                authRepository = authRepository
            )
            CompositionLocalProvider(

            ) {
                SiufascoreTheme(
                    darkTheme = false,
                ) {
                    SiufascoreApp(
                        appState = appState,
                    )
                }
            }
        }
    }

    private fun setupSplashScreen(splashScreen: SplashScreen) {
        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value.isLoading
        }

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val iconView = splashScreenView.iconView
            val splashView = splashScreenView.view

            val iconAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
                duration = 800
                interpolator = DecelerateInterpolator()

                addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    iconView.apply {
                        alpha = value
                        scaleX = 0.8f + (value * 0.2f)
                        scaleY = 0.8f + (value * 0.2f)
                    }
                }
            }

            // Animation cho background
            val backgroundAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
                duration = 600
                startDelay = 200
                interpolator = AccelerateInterpolator()

                addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    splashView.alpha = value
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        enableEdgeToEdge()
                        splashScreenView.remove()
                    }
                })
            }

            // Chạy animation
            AnimatorSet().apply {
                playTogether(iconAnimator, backgroundAnimator)
                start()
            }
        }
    }
}


