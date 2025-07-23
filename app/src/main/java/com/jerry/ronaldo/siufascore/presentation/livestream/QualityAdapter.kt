package com.jerry.ronaldo.siufascore.presentation.livestream

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.databinding.ItemQualityBinding
import com.jerry.ronaldo.siufascore.utils.StreamQuality

class QualityAdapter(
    private val onQualitySelected: (StreamQuality?) -> Unit
) : RecyclerView.Adapter<QualityAdapter.QualityViewHolder>() {

    private var qualities = listOf<StreamQuality?>()
    private var selectedQuality: StreamQuality? = null

    fun updateQualities(newQualities: List<StreamQuality?>, current: StreamQuality?) {
        qualities = newQualities
        selectedQuality = current
        notifyDataSetChanged()
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

        fun bind(quality: StreamQuality?) {
            binding.apply {
                // Set quality info
                if (quality == null) {
                    textQualityName.text = root.context.getString(R.string.auto_quality)
                    textQualityDetails.text = root.context.getString(R.string.auto_quality_description)
                } else {
                    textQualityName.text = quality.name
                    textQualityDetails.text = "${quality.width}×${quality.height} • ${quality.bitrate}"
                }

                // Set selection state
                radioQuality.isChecked = quality == selectedQuality || (quality == null && selectedQuality == null)

                // Handle click
                root.setOnClickListener {
                    onQualitySelected(quality)
                }

                radioQuality.setOnClickListener {
                    onQualitySelected(quality)
                }
            }
        }
    }
}