package com.jerry.ronaldo.siufascore.presentation.highlight.screen

import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import timber.log.Timber

@Composable
fun YoutubePlayerScreen(
    modifier: Modifier = Modifier,
    videoId: String,
    startSecond: Float = 0f,
    onCurrentSecondChanged: (Float) -> Unit = {},
    onPlayerReady: (YouTubePlayer) -> Unit = {},
    onError: (PlayerConstants.PlayerError) -> Unit = {},
    onEnterFullscreen: () -> Unit = {},
    onExitFullscreen: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var youtubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }
    var fullscreenViewParent by remember { mutableStateOf<ViewGroup?>(null) }
    var fullScreenView by remember { mutableStateOf<View?>(null) }
    var exitFullscreenCallback by remember { mutableStateOf<(() -> Unit)?>(null) }
    val activity = LocalActivity.current as? ComponentActivity
    val rootContainer = remember {
        activity?.findViewById<ViewGroup>(android.R.id.content)
    }
    var originalOrientation by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(videoId) {

    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            YouTubePlayerView(context).apply {
                enableAutomaticInitialization = false
                lifecycleOwner.lifecycle.addObserver(this)
                val options = IFramePlayerOptions.Builder()
                    .controls(1)
                    .rel(0)
                    .fullscreen(1)
                    .build()

                initialize(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youtubePlayer = youTubePlayer

                        if (startSecond > 0) {
                            youTubePlayer.loadVideo(videoId, startSecond)
                        } else {
                            youTubePlayer.cueVideo(videoId, 0f)
                        }
                        onPlayerReady(youTubePlayer)
                    }

                    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                        onCurrentSecondChanged(second)
                    }


                    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                        onError(error)
                    }
                }, options)
                addFullscreenListener(object : FullscreenListener {
                    override fun onExitFullscreen() {
                        Timber.tag("YouTubeFullscreen").d("Exiting fullscreen")
                        activity?.let {act->
                            originalOrientation?.let {orientation->
                                act.requestedOrientation = orientation
                            }
                            // Remove fullscreen view from root
                            fullScreenView?.let { view ->
                                rootContainer?.removeView(view)
                                fullscreenViewParent?.addView(view)
                            }


                            // Clear references
                            fullScreenView = null
                            fullscreenViewParent = null
                            exitFullscreenCallback = null
                        }
                    }

                    override fun onEnterFullscreen(
                        fullscreenView: View,
                        exitFullscreen: () -> Unit
                    ) {
                        Timber.tag("YouTubeFullscreen").d("Entering fullscreen")

                        activity?.let {act->
                            originalOrientation = act.requestedOrientation
                            // xoay ngang
                            act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            // Store references
                            fullScreenView = fullscreenView
                            exitFullscreenCallback = exitFullscreen
                            // Add fullscreen view to root container
                            rootContainer?.let { container ->
                                fullscreenViewParent = fullscreenView.parent as? ViewGroup
                                fullscreenViewParent?.removeView(fullscreenView)

                                // Add to root với fullscreen layout params
                                container.addView(
                                    fullscreenView,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                        }
                    }
                })
            }
        },
        update = {
            youtubePlayer?.let { player ->
                if (startSecond > 0) {
                    player.loadVideo(videoId, startSecond)
                } else {
                    player.cueVideo(videoId, 0f)
                }
            }
        }
    )
    DisposableEffect(lifecycleOwner) {
        onDispose {
            youtubePlayer = null
        }
    }
}