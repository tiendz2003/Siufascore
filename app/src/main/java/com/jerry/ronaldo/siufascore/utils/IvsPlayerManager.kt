package com.jerry.ronaldo.siufascore.utils

import android.util.Size
import androidx.core.net.toUri
import com.amazonaws.ivs.player.Player
import com.amazonaws.ivs.player.Quality
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IvsPlayerManager @Inject constructor() {
    private var availableQualities: Set<Quality> = emptySet()
    private var currentQuality: Quality? = null
    private var isAutoQualityEnabled = true
    private var player:Player ?= null
    private lateinit var playerListener: Player.Listener
    private var isInit = false
    private var onQualitiesChanged: ((List<StreamQuality>) -> Unit)? = null
    private var onQualityChanged: ((StreamQuality) -> Unit)? = null

    fun setQualityCallbacks(
        onQualitiesChanged: (List<StreamQuality>) -> Unit,
        onQualityChanged: (StreamQuality) -> Unit
    ) {
        this.onQualitiesChanged = onQualitiesChanged
        this.onQualityChanged = onQualityChanged
    }
    fun initializePlayer(
        onStateChanged: (Player.State) -> Unit,
        onVideoSizeChanged: (Size) -> Unit,
        onError: (Throwable) -> Unit
    ){
        player?.let { p ->
            try {
                if (isInit) {
                    Timber.d("Đã khởi tạo rồi")
                    return
                }
                playerListener = p.init(
                    onVideoSizeChanged = onVideoSizeChanged,
                    onStateChanged = {state->
                        onStateChanged(state)
                        if (state == Player.State.READY) {
                            Timber.d("đÃ SETUP XONG")
                            updateAvailableQualities()
                        }
                    },
                    onError = onError,
                    onQualityChanged = {quality->
                        handleQualityChanged(quality)

                    },

                )
                p.setRebufferToLive(true)
                isInit = true
                Timber.d("Khởi tạo player thành công")
            }catch (e:Exception){
                Timber.e(e, "Lỗi khi khởi tạo player")
                onError(e)
            }
        }
    }
    private fun handleQualityChanged(quality: Quality?) {
        player?.let { p ->
           updateCurrentQuality(quality, isAutoQualityEnabled)
            val qualityOptions = getQualityOptions()
            onQualitiesChanged?.invoke(qualityOptions)
            val currentOption = if (isAutoQualityEnabled) {
                StreamQuality.createAutoQuality(true)
            } else {
                quality?.let { StreamQuality.fromQuality(it, true) }
            }

            currentOption?.let { option ->
                onQualityChanged?.invoke(option)
                Timber.d("Chất lượng đã đổi: ${option.displayName}")
            }
        }
    }

    fun setQuality(qualityOption: StreamQuality) {
        player?.let { p ->
            try {
                if (qualityOption.isAutoQuality) {
                    p.isAutoQualityMode = true
                    updateCurrentQuality(null,true)
                    Timber.d("Tự động auto")
                } else {
                    qualityOption.quality?.let { quality ->
                        p.quality = quality
                        updateCurrentQuality(null,false)
                        Timber.d("Chuyển chất lượng qua: ${qualityOption.displayName} (${quality.width}x${quality.height})")
                    }
                }

                val qualityOptions = getQualityOptions()
                onQualitiesChanged?.invoke(qualityOptions)
            } catch (e: Exception) {
                Timber.e(e, "Error setting quality")
            }
        } ?: run {
            Timber.w("Player not available for setQuality")
        }
    }
    /**
     * HÀM NÀY SẼ QUAN LẤY DANH SÁCH CHẤT LƯỢNG MÀ PLAYER ĐÃ CÓ
     * TRUYỀN VÀO CALLBACK SAU ĐÓ Ở HÀM INIT SẼ LẮNG NGHE CALLBACK VÀ TRUYỀN RA UI
     * */
    private fun updateAvailableQualities() {
        player?.let { p ->
            val qualities = p.qualities
            updateQualities(qualities)
            updateCurrentQuality(p.quality, isAutoQualityEnabled)

            val qualityOptions = getQualityOptions()
            onQualitiesChanged?.invoke(qualityOptions)

            Timber.d("Available qualities updated: ${qualities.size} options")
            qualities.forEach { quality ->
                Timber.d("Quality: ${quality.name} - ${quality.width}x${quality.height} - ${quality.bitrate}bps")
            }
        }
    }
    private fun updateQualities(qualities: Set<Quality>) {
        availableQualities = qualities
    }
    private fun updateCurrentQuality(quality: Quality?, isAuto:Boolean) {
        currentQuality = quality
        isAutoQualityEnabled = isAuto
        Timber.d("Cập nhật chất lượng hiện tại: $quality")
    }
    fun getQualityOptions(): List<StreamQuality> {
        val options = mutableListOf<StreamQuality>()
        options.add(StreamQuality.createAutoQuality(isAutoQualityEnabled))
        val sortedQualities = availableQualities.sortedWith(
            compareByDescending<Quality> { it.height  }.thenByDescending { it.bitrate }
        )
        sortedQualities.forEach { quality->
            val isSelected = !isAutoQualityEnabled && currentQuality?.let { current ->
                // Compare qualities properly using their properties
                current.name == quality.name &&
                        current.height == quality.height &&
                        current.bitrate == quality.bitrate
            } ?: false
            options.add(
                StreamQuality.fromQuality(
                    quality,
                    isSelected
                )
            )
        }
        return options
    }

    fun setPlayer(player: Player) {
        if (this.player != null && this.player != player) {
            releaseInternal()
        }
        this.player = player
        isInit = false
    }

    fun loadAndPlay(url: String) {
        player?.let { p ->
            p.load(url.toUri())
            p.play()
        }
    }
    fun pause() {
        player?.pause()
    }
    fun play(){
        player?.play()
    }
    fun replay() {
        player?.let { p ->
            p.seekTo(0)
            p.play()
        }
    }
    fun getPlayerState(): Player.State? {
        return player?.state?.also { state ->
            Timber.d("State: $state")
        }
    }
    private fun releaseInternal() {
        player?.let { p ->
            try {
                if (::playerListener.isInitialized) {
                    p.removeListener(playerListener)
                    Timber.d("Player listener removed")
                }
                p.release()
                Timber.d("Player released internally")
            } catch (e: Exception) {
                Timber.e(e, "Error releasing player internally")
            }
        }
        isInit = false
    }
    fun setAutoMaxQuality() {
        player?.let { p ->
            try {
                val qualities = p.qualities
                if (qualities.isNotEmpty()) {
                    // LẤY CHẤT LƯỢNG CAO NHẤT TRONG DANH SÁCH
                    val maxQuality = qualities.maxByOrNull { it.height * it.width + it.bitrate }
                    maxQuality?.let { quality ->
                        p.setAutoMaxQuality(quality)
                        Timber.d("Auto chất lượng: ${quality.name} (${quality.width}x${quality.height})")
                    }
                } else {
                    Timber.w("Không có chất lượng nào để đặt auto max")
                }
            } catch (e: Exception) {
                Timber.e(e, "Lỗi khi đặt chất lượng tự động tối đa")
            }
        } ?: run {
            Timber.w("Lỗi ")
        }
    }


    fun release() {
        player?.let { p ->
            try {
                if (::playerListener.isInitialized) {
                    p.removeListener(playerListener)
                    Timber.d("Đã xóa callback")
                }
                p.release()
                Timber.d("Player đã giải phỏng")
            } catch (e: Exception) {
                Timber.e(e, "Lỗi giải phóng player")
            } finally {
                player = null
                isInit = false
            }
        }
    }
}