package com.jerry.ronaldo.siufascore.presentation.livestream

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.data.model.LiveComment
import com.jerry.ronaldo.siufascore.databinding.ItemCommentBinding
import com.jerry.ronaldo.siufascore.utils.toTimeAgo

class CommentsAdapter:ListAdapter<LiveComment,CommentsAdapter.CommentViewHolder>(CommentDiffCallback()) {
    inner class CommentViewHolder(
        private val binding:ItemCommentBinding
    ):RecyclerView.ViewHolder(binding.root){
        fun bind(comment: LiveComment) {
            binding.apply {
                textUsername.text = comment.userName
                textComment.text = comment.comment
                textTimestamp.text = comment.timestamp.toTimeAgo()
                loadUserAvatar(comment.userImage)
            }
        }

        private fun loadUserAvatar(imageUrl: String) {
            binding.imageUserAvatar.load(imageUrl ){
                crossfade(true) // Hiệu ứng mờ dần khi tải ảnh
                placeholder(R.drawable.outline_person_24)
                error(R.drawable.outline_person_24)
                transformations(CircleCropTransformation()) // Áp dụng bo tròn thành hình tròn
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
class CommentDiffCallback : DiffUtil.ItemCallback<LiveComment>() {
    override fun areItemsTheSame(oldItem: LiveComment, newItem: LiveComment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LiveComment, newItem: LiveComment): Boolean {
        return oldItem == newItem
    }
}