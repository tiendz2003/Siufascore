package com.jerry.ronaldo.siufascore.presentation.livestream

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.databinding.ItemQualityBinding
import com.jerry.ronaldo.siufascore.utils.StreamQuality
import timber.log.Timber

class QualityAdapter(
    private val onQualitySelected: (StreamQuality) -> Unit
) : RecyclerView.Adapter<QualityAdapter.QualityViewHolder>() {

    private var qualities = emptyList<StreamQuality>()

    fun submitList(newQualities: List<StreamQuality>) {
        Timber.d("submitList: $newQualities")
        val oldQualities = qualities
        qualities = newQualities

        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = oldQualities.size
            override fun getNewListSize() = newQualities.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldQualities[oldItemPosition]
                val newItem = newQualities[newItemPosition]
                return oldItem.isAutoQuality == newItem.isAutoQuality &&
                        oldItem.quality?.name == newItem.quality?.name
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldQualities[oldItemPosition] == newQualities[newItemPosition]
            }
        })

        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QualityViewHolder {
        val binding = ItemQualityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QualityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QualityViewHolder, position: Int) {
        holder.bind(qualities[position])
    }

    override fun getItemCount(): Int = qualities.size

    inner class QualityViewHolder(
        private val binding: ItemQualityBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(qualityOption: StreamQuality) {
            with(binding) {
                textQualityName.text = qualityOption.displayName
                radioQualitySelect.isChecked = qualityOption.isSelected
                // Setup quality icon
                setupQualityIcon(qualityOption)
                // Setup quality details
                setupQualityDetails(qualityOption)
                // Setup quality badge
                setupQualityBadge(qualityOption)
                // Show active indicator
                viewActiveIndicator.isVisible = qualityOption.isSelected
                // Handle clicks
                root.setOnClickListener {
                    onQualitySelected(qualityOption)
                }
                radioQualitySelect.setOnClickListener {
                    onQualitySelected(qualityOption)
                }

                // Visual feedback
                root.alpha = if (qualityOption.isSelected) 1.0f else 0.8f
            }
        }

        private fun setupQualityIcon(qualityOption: StreamQuality) {
            val iconRes = when {
                qualityOption.isAutoQuality -> R.drawable.baseline_hdr_auto_24
                qualityOption.displayName == "720p"-> R.drawable.baseline_4k_24
                qualityOption.displayName == "480p" -> R.drawable.baseline_hd_24
                qualityOption.displayName == "360p" -> R.drawable.baseline_sd_24
                qualityOption.displayName == "160p" -> R.drawable.baseline_sd_24
                else -> R.drawable.baseline_hdr_auto_24
            }
            binding.imageQualityIcon.setImageResource(iconRes)

        }

        private fun setupQualityDetails(qualityOption: StreamQuality) {
            val details = qualityOption.getQualityDetails()
            binding.textQualityDetails.text = details
            binding.textQualityDetails.isVisible = details.isNotEmpty()
        }

        private fun setupQualityBadge(qualityOption: StreamQuality) {
            when {
                qualityOption.isAutoQuality && qualityOption.isSelected -> {
                    binding.textQualityBadge.text = "ACTIVE"
                    binding.textQualityBadge.isVisible = true
                }

                qualityOption.quality?.height != null -> {
                    when {
                        qualityOption.quality.height >= 2160 -> {
                            binding.textQualityBadge.text = "4K"
                            binding.textQualityBadge.isVisible = true
                        }
                        qualityOption.quality.height >= 1080 -> {
                            binding.textQualityBadge.text = "HD"
                            binding.textQualityBadge.isVisible = true
                        }
                        qualityOption.quality.height >= 720 -> {
                            binding.textQualityBadge.text = "HD"
                            binding.textQualityBadge.isVisible = true
                        }
                        else -> {
                            binding.textQualityBadge.isVisible = false
                        }
                    }
                }

                else -> {
                    binding.textQualityBadge.isVisible = false
                }
            }
        }
    }
}