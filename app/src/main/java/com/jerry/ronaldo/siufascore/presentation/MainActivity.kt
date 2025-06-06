package com.jerry.ronaldo.siufascore.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                networkMonitor = networkMonitor
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
}


