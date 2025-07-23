package com.jerry.ronaldo.siufascore.presentation.livestream

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.databinding.FragmentIVSPlayerBinding
import com.jerry.ronaldo.siufascore.utils.PlayerState
import com.jerry.ronaldo.siufascore.utils.StreamQuality
import com.jerry.ronaldo.siufascore.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class IVSPlayerFragment : Fragment() {

    private var _binding: FragmentIVSPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IVSPlayerViewModel by activityViewModels()
    private var qualityBottomSheet: QualityBottomSheetFragment? = null
    private val commentsAdapter by lazy {
        CommentsAdapter()
    }
    private var controlsHideHandler = Handler(Looper.getMainLooper())
    private val hideControlsRunnable = Runnable {
        viewModel.sendIntent(IVSPlayerIntent.HideControls)
    }

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
        setupCommentSection()
        setupCommentInput()
        viewModel.sendIntent(IVSPlayerIntent.LoadComments(viewModel.uiState.value.matchId.toString()))
        setupControls()
        observeViewModel()
        observeEvents()
        //  handleBack()
    }

    override fun onResume() {
        super.onResume()
        //Khoi tao player
        val playerView = binding.playerView.apply {
            controlsEnabled = false
        }
        viewModel.sendIntent(IVSPlayerIntent.PlayerReady(playerView.player))
        viewModel.sendIntent(IVSPlayerIntent.LoadStream)
    }

    private fun showQualityBottomSheet() {
        val currentState = viewModel.uiState.value
        Timber.d("IVSPlayerFragment: ${currentState.availableQualities}")
        if (currentState.availableQualities.isEmpty()) {
            return
        }

        qualityBottomSheet?.dismiss()

        qualityBottomSheet = QualityBottomSheetFragment.newInstance(
            qualities = currentState.availableQualities,
            onQualitySelected = { qualityOption ->
                viewModel.sendIntent(IVSPlayerIntent.SelectQuality(qualityOption))
            }
        )

        qualityBottomSheet?.show(childFragmentManager, QualityBottomSheetFragment.TAG)
    }

    private fun updateQualityDisplay(currentQuality: StreamQuality?) {
        val qualityText = currentQuality?.displayName ?: "Auto"

        binding.includeControls.textQuality.text = qualityText

    }

    private fun setupControls() {
        with(binding.includeControls) {
            cardCenterPlay.setOnClickListener {
                viewModel.sendIntent(IVSPlayerIntent.PlayPause)
            }
            buttonBack.setOnClickListener {
                val isFullScreen = viewModel.uiState.value.isFullscreen
                if (isFullScreen) {
                    viewModel.sendIntent(IVSPlayerIntent.ToggleFullscreen)
                } else {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
            buttonPlayPause.setOnClickListener {
                viewModel.sendIntent(IVSPlayerIntent.PlayPause)
            }
            buttonFullscreen.setOnClickListener {
                viewModel.sendIntent(IVSPlayerIntent.ToggleFullscreen)
            }
            textQuality.setOnClickListener {
                showQualityBottomSheet()
            }
            root.setOnClickListener {
                viewModel.sendIntent(IVSPlayerIntent.ToggleControlsVisibility)
            }
        }
        binding.btnRetry.setOnClickListener {
            viewModel.sendIntent(IVSPlayerIntent.Retry)
        }
    }

    private fun setupCommentSection() {
        with(binding.containerComments.recyclerViewComments) {
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            commentsAdapter.registerAdapterDataObserver(object :
                RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (positionStart >= commentsAdapter.itemCount - itemCount) {
                        scrollToPosition(commentsAdapter.itemCount - 1)
                    }
                }
            })
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                    if (firstVisibleItem == 0 && dy < 0) {
                        viewModel.uiState.value.matchId?.let { matchId ->
                            viewModel.sendIntent(IVSPlayerIntent.LoadMoreComments(matchId.toString()))
                        }
                    }
                }
            })
        }
    }

    private fun setupCommentInput() {
        with(binding.containerComments) {
            editTextComment.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        val text = s?.toString()?.trim() ?: ""
                        binding.containerComments.fabSendComment.isEnabled = text.isNotEmpty()
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                }
            )
            fabSendComment.setOnClickListener {
                sendComment()
            }
        }
    }

    private fun sendComment() {
        val currentMatchId = viewModel.uiState.value.matchId
        val commentText = binding.containerComments.editTextComment.text?.toString()?.trim()
        if (!commentText.isNullOrEmpty() && currentMatchId != null) {
            viewModel.sendIntent(
                IVSPlayerIntent.PostComment(
                    currentMatchId.toString(),
                    commentText
                )
            )
            binding.containerComments.editTextComment.text?.clear()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUi(state)
                    updatePlayerState(state.playerState)
                    updateLoadingState(state.isLoading)
                    updateControlsVisibility(state.isControlsVisible)
                    updateQualityDisplay(state.currentQuality)
                    updateErrorState(state.errorMessage)
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.singleEvent.collect { event ->
                    when (event) {
                        is IVSPlayerEvent.EnterFullscreen -> enterFullscreen()
                        is IVSPlayerEvent.ExitFullscreen -> exitFullscreen()
                        is IVSPlayerEvent.StartControlsTimer -> startControlsTimer()
                        is IVSPlayerEvent.StopControlsTimer -> stopControlsTimer()
                        is IVSPlayerEvent.ShowError -> {
                            requireActivity().showSnackbar(event.message)
                        }

                        IVSPlayerEvent.CommentPostedSuccessfully -> {
                            requireActivity().showSnackbar(
                                getString(R.string.comment_posted_successfully)
                            )
                        }

                        IVSPlayerEvent.ScrollToBottom -> {
                            scrollToBottom()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun updateUi(state: IVSPlayerViewUiState) {
        binding.includeControls.textTitle.text = state.matchInfo
        commentsAdapter.submitList(state.comments)
        with(binding.containerComments) {
            imageUserAvatar.load(
                state.currentUserImg?.ifEmpty { R.drawable.outline_person_24 }
            ) {
                crossfade(true)
                placeholder(R.drawable.outline_person_24)
                error(R.drawable.outline_person_24)
                transformations(CircleCropTransformation())
            }


            layoutEmptyComments.isVisible = state.comments.isEmpty() && !state.isLoading

            progressLoadingMore.isVisible = state.isLoadingMore
            fabSendComment.isEnabled = !state.isPosting &&
                    !editTextComment.text.isNullOrBlank()
        }
    }

    private fun scrollToBottom() {
        binding.containerComments.recyclerViewComments.scrollToPosition(commentsAdapter.itemCount - 1)
    }

    fun refreshComments() {
        viewModel.sendIntent(IVSPlayerIntent.RefreshComments)
    }

    private fun updateErrorState(errorMessage: String?) {
        binding.cardError.isVisible = errorMessage != null
        binding.includeControls.root.isVisible = errorMessage == null
        errorMessage?.let {
            binding.textError.text = it
        }
    }

    private fun updatePlayerState(playerState: PlayerState) {
        with(binding.includeControls) {
            when (playerState) {
                PlayerState.Playing -> {
                    imageCenterPlay.setImageResource(R.drawable.ic_pause)
                    buttonPlayPause.setImageResource(R.drawable.ic_pause)
                    progressLoading.isVisible = false
                    cardCenterPlay.isVisible = true
                }

                PlayerState.Paused -> {
                    imageCenterPlay.setImageResource(R.drawable.ic_play_arrow)
                    buttonPlayPause.setImageResource(R.drawable.ic_play_arrow)
                    progressLoading.isVisible = false
                    cardCenterPlay.isVisible = true
                }

                PlayerState.Buffering -> {
                    progressLoading.isVisible = true
                    cardCenterPlay.isVisible = false
                }

                PlayerState.Ended -> {
                    imageCenterPlay.setImageResource(R.drawable.baseline_replay_24)
                    buttonPlayPause.setImageResource(R.drawable.baseline_replay_24)
                    progressLoading.isVisible = false
                    cardCenterPlay.isVisible = true
                }

                PlayerState.Error -> {
                    progressLoading.isVisible = false
                    cardCenterPlay.isVisible = true
                }

                else -> {
                    progressLoading.isVisible = false
                    cardCenterPlay.isVisible = true
                }
            }
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.groupLoading.isVisible = isLoading
    }


    private fun updateControlsVisibility(isVisible: Boolean) {
        if (isVisible) {
            showControlsAnimation()
        } else {
            hideControlsAnimation()
        }
    }

    private fun showControlsAnimation() {
        with(binding.includeControls) {
            cardCenterPlay.isVisible = true

            layoutTopControls.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(ANIMATION_DURATION)//Handle bắt đầu chạy và giảm dần
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


    private fun hideControlsAnimation() {
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

    private fun startControlsTimer() {
        stopControlsTimer()
        controlsHideHandler.postDelayed(hideControlsRunnable, CONTROLS_HIDE_DELAY)
    }

    private fun stopControlsTimer() {
        controlsHideHandler.removeCallbacks(hideControlsRunnable)
    }

    private fun updateUiForOrientation(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Chế độ toàn màn hình (ngang)
            binding.containerComments.root.visibility = View.GONE
            (binding.playerView.layoutParams as ConstraintLayout.LayoutParams).apply {
                width = ConstraintLayout.LayoutParams.MATCH_PARENT // hoặc 0dp (MATCH_CONSTRAINT)
                height = ConstraintLayout.LayoutParams.MATCH_PARENT // hoặc 0dp (MATCH_CONSTRAINT)
                dimensionRatio = null // Xóa tỷ lệ khung hình để lấp đầy màn hình
            }

            activity?.window?.let { window ->
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    hide(WindowInsetsCompat.Type.statusBars())
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
            binding.includeControls.buttonFullscreen.setImageResource(R.drawable.baseline_fullscreen_exit_24)
        } else {
            // Chế độ bình thường (dọc)
            binding.containerComments.root.visibility = View.VISIBLE
            (binding.playerView.layoutParams as ConstraintLayout.LayoutParams).apply {
                width = ConstraintLayout.LayoutParams.MATCH_PARENT
                height = 0 // Để constraint layout tự tính toán theo tỷ lệ
                dimensionRatio = "16:9"
            }
            activity?.window?.let { window ->
                WindowCompat.getInsetsController(window, window.decorView)
                    .show(WindowInsetsCompat.Type.statusBars())
            }

            binding.includeControls.buttonFullscreen.setImageResource(R.drawable.outline_fullscreen_24)
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
        stopControlsTimer()
        viewModel.release()
        qualityBottomSheet?.dismiss()
        qualityBottomSheet = null
        _binding = null
    }


    companion object {
        private const val CONTROLS_HIDE_DELAY = 3000L
        private const val ANIMATION_DURATION = 300L
        private const val DEMO_STREAM_URL =
            "https://fcc3ddae59ed.us-west-2.playback.live-video.net/api/video/v1/us-west-2.893648527354.channel.DmumNckWFTqz.m3u8"
    }
}