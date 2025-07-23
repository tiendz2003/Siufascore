package com.jerry.ronaldo.siufascore.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.databinding.ActivityLiveStreamPlayerBinding
import com.jerry.ronaldo.siufascore.presentation.livestream.IVSPlayerFragment
import com.jerry.ronaldo.siufascore.presentation.livestream.IVSPlayerIntent
import com.jerry.ronaldo.siufascore.presentation.livestream.IVSPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@AndroidEntryPoint
class LiveStreamPlayerActivity : AppCompatActivity() {
    private val binding: ActivityLiveStreamPlayerBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLiveStreamPlayerBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
    }

    companion object {
        const val MATCH_ID = "MATCH_ID"
    }

    private val ivsViewModel: IVSPlayerViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val matchId = intent.getIntExtra(MATCH_ID, -1)
        setContentView(R.layout.activity_live_stream_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragmentContainer)) { v, insets ->
            // Lấy thông tin khoảng trống của cả system bars (status bar, nav bar) và bàn phím (IME)
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            // Tính toán padding dưới cùng:
            // - Khi bàn phím đóng, ime.bottom là 0, ta sẽ dùng systemBars.bottom (chiều cao nav bar).
            // - Khi bàn phím mở, ime.bottom > 0, ta sẽ dùng giá trị này.
            // -> Lấy giá trị lớn hơn giữa hai cái sẽ xử lý được cả hai trường hợp.
            val targetBottomPadding = maxOf(systemBars.bottom, ime.bottom)

            // Áp dụng padding cho container của Fragment
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, targetBottomPadding)

            // Trả về insets gốc để các view con khác có thể sử dụng nếu cần
            insets
        }
        setMatchId(matchId)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, IVSPlayerFragment())
                .commit()
        }

    }





    private fun setMatchId(matchId: Int) {
        ivsViewModel.sendIntent(IVSPlayerIntent.SetMatchId(matchId))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }


}

