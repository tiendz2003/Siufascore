package com.jerry.ronaldo.siufascore.presentation.livestream

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.R.drawable
import com.jerry.ronaldo.siufascore.databinding.FragmentIVSPlayerBinding
import com.jerry.ronaldo.siufascore.utils.PlayerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IVSPlayerFragment : Fragment() {

    private var _binding: FragmentIVSPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IVSPlayerViewModel by activityViewModels()
    private lateinit var qualityAdapter: QualityAdapter

    private var controlsHideHandler = Handler(Looper.getMainLooper())
    private val hideControlsRunnable = Runnable { hideControls() }
    private var isControlsVisible = true
    private var isFullscreen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIVSPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupControls()
        observeViewModel()
        setupAutoHideControls()
    }

    override fun onResume() {
        super.onResume()
        val playerView = binding.playerView.apply {
            controlsEnabled = false
        }
        viewModel.setPlayer(playerView.player)
    }

    private fun setupControls() {
        with(binding.includeControls) {
            cardCenterPlay.setOnClickListener {
                viewModel.togglePlayPause()
            }
            buttonBack.setOnClickListener {
                if (isFullscreen) {
                    exitFullscreen()
                } else {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
            buttonPlayPause.setOnClickListener {
                viewModel.togglePlayPause()
            }
            buttonFullscreen.setOnClickListener {
                toggleFullscreen()
            }
            root.setOnClickListener {
                toggleControlsVisibility()
            }
        }

    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playerState.collect { state ->
                    updatePlayerState(state)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    updateLoadingState(isLoading)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe error state
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorState.collect { error ->
                    error?.let { showError(it) }
                }
            }
        }
    }

    private fun updatePlayerState(state: PlayerState) {
        with(binding.includeControls) {
            when (state) {
                PlayerState.Playing -> {
                    imageCenterPlay.setImageResource(drawable.ic_pause)
                    buttonPlayPause.setImageResource(drawable.ic_pause)
                    progressLoading.isVisible = false


                    // Auto-hide controls after delay
                    if (isControlsVisible) {
                        resetControlsTimer()
                    }
                }

                PlayerState.Paused -> {
                    imageCenterPlay.setImageResource(drawable.ic_play_arrow)
                    buttonPlayPause.setImageResource(drawable.ic_play_arrow)
                    progressLoading.isVisible = false

                    // Keep controls visible when paused
                    controlsHideHandler.removeCallbacks(hideControlsRunnable)
                    showControls()
                }

                PlayerState.Buffering -> {
                    progressLoading.isVisible = true
                    // Show buffering indicator
                }

                PlayerState.Ended -> {
                    imageCenterPlay.setImageResource(drawable.baseline_replay_24)
                    buttonPlayPause.setImageResource(drawable.baseline_replay_24)
                    progressLoading.isVisible = false
                    showControls()
                }

                else -> {}
            }
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.groupLoading.isVisible = isLoading


        if (isLoading) {
            showControls()
            controlsHideHandler.removeCallbacks(hideControlsRunnable)
        }
    }

    private fun showError(error: String) {
        binding.cardError.isVisible = true
        binding.textError.text = error
        binding.btnRetry.setOnClickListener {
            binding.cardError.isVisible = false
            viewModel.retry()
        }
        showControls()
        controlsHideHandler.removeCallbacks(hideControlsRunnable)
    }

    private fun setupAutoHideControls() {
        // Initial state - show controls
        showControls()
        resetControlsTimer()
    }

    private fun resetControlsTimer() {
        controlsHideHandler.removeCallbacks(hideControlsRunnable)
        if (viewModel.shouldAutoHideControls()) {
            controlsHideHandler.postDelayed(hideControlsRunnable, CONTROLS_HIDE_DELAY)
        }
    }

    private fun toggleControlsVisibility() {
        if (isControlsVisible) {
            hideControls()
        } else {
            showControls()
            resetControlsTimer()
        }
    }

    private fun showControls() {
        if (!isControlsVisible) {
            isControlsVisible = true
            with(binding.includeControls) {
                cardCenterPlay.isVisible = true
                layoutTopControls.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(ANIMATION_DURATION)
                    .start()

                layoutBottomControls.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(ANIMATION_DURATION)
                    .start()

                topGradient.animate()
                    .alpha(1f)
                    .setDuration(ANIMATION_DURATION)
                    .start()

                bottomGradient.animate()
                    .alpha(1f)
                    .setDuration(ANIMATION_DURATION)
                    .start()
            }
        }
    }

    private fun hideControls() {
        if (isControlsVisible && viewModel.shouldAutoHideControls()) {
            isControlsVisible = false
            with(binding.includeControls) {
                cardCenterPlay.isVisible = false
                layoutTopControls.animate()
                    .alpha(0f)
                    .translationY(-layoutTopControls.height.toFloat())
                    .setDuration(ANIMATION_DURATION)
                    .start()

                layoutBottomControls.animate()
                    .alpha(0f)
                    .translationY(layoutBottomControls.height.toFloat())
                    .setDuration(ANIMATION_DURATION)
                    .start()

                topGradient.animate()
                    .alpha(0f)
                    .setDuration(ANIMATION_DURATION)
                    .start()

                bottomGradient.animate()
                    .alpha(0f)
                    .setDuration(ANIMATION_DURATION)
                    .start()
            }
        }
    }

    private fun toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen()
        } else {
            enterFullscreen()
        }
    }

    private fun enterFullscreen() {

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun exitFullscreen() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateUiForOrientation(newConfig.orientation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        controlsHideHandler.removeCallbacks(hideControlsRunnable)
        viewModel.release()
        _binding = null
    }

    private fun updateUiForOrientation(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Chế độ toàn màn hình (ngang)
            binding.containerComments.visibility = View.GONE
            binding.playerView.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT,

                )

            isFullscreen = true
            activity?.window?.let { window ->
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    hide(WindowInsetsCompat.Type.statusBars())
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
            binding.includeControls.buttonFullscreen.setImageResource(drawable.baseline_fullscreen_exit_24)
            resetControlsTimer()
        } else {
            // Chế độ bình thường (dọc)
            binding.containerComments.visibility = View.VISIBLE
            binding.playerView.layoutParams =
                (binding.playerView.layoutParams as ConstraintLayout.LayoutParams).apply {
                    width = ConstraintLayout.LayoutParams.MATCH_PARENT
                    height = 0
                    dimensionRatio = "16:9"
                }
            isFullscreen = false
            activity?.window?.let { window ->
                WindowCompat.getInsetsController(window, window.decorView)
                    .show(WindowInsetsCompat.Type.statusBars())
            }

            binding.includeControls.buttonFullscreen.setImageResource(R.drawable.outline_fullscreen_24)
            resetControlsTimer()
        }
    }

    companion object {
        private const val CONTROLS_HIDE_DELAY = 3000L
        private const val ANIMATION_DURATION = 300L
        private const val DEMO_STREAM_URL =
            "https://fcc3ddae59ed.us-west-2.playback.live-video.net/api/video/v1/us-west-2.893648527354.channel.DmumNckWFTqz.m3u8"
    }
}