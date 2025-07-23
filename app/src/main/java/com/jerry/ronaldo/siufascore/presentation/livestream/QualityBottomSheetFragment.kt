package com.jerry.ronaldo.siufascore.presentation.livestream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.databinding.FragmentQualityBottomSheetBinding
import com.jerry.ronaldo.siufascore.utils.StreamQuality
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QualityBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentQualityBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var qualityAdapter: QualityAdapter
    private var onQualitySelected: ((StreamQuality) -> Unit)? = null
    private var currentQualities: List<StreamQuality> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQualityBottomSheetBinding.inflate(inflater, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        updateCurrentQualityDisplay()
    }

    private fun setupRecyclerView() {
        qualityAdapter = QualityAdapter { qualityOption ->
            onQualitySelected?.invoke(qualityOption)
            dismiss()
        }

        binding.recyclerViewQualities.apply {
            adapter = qualityAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }

        if (currentQualities.isNotEmpty()) {
            qualityAdapter.submitList(currentQualities)
        }
    }

    private fun updateCurrentQualityDisplay() {
        val currentQuality = currentQualities.find { it.isSelected }
        currentQuality?.let { quality ->
            binding.textCurrentQuality.text = quality.displayName
        }
    }

    fun updateQualities(qualities: List<StreamQuality>) {
        currentQualities = qualities
        if (::qualityAdapter.isInitialized) {
            qualityAdapter.submitList(qualities)
            updateCurrentQualityDisplay()
        }
    }

    fun setOnQualitySelectedListener(listener: (StreamQuality) -> Unit) {
        onQualitySelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "QualityBottomSheetFragment"

        fun newInstance(
            qualities: List<StreamQuality>,
            onQualitySelected: (StreamQuality) -> Unit
        ): QualityBottomSheetFragment {
            return QualityBottomSheetFragment().apply {
                updateQualities(qualities)
                setOnQualitySelectedListener(onQualitySelected)
            }
        }
    }
}