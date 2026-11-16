package com.alphaflix.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.alphaflix.app.R
import com.alphaflix.app.databinding.ItemMediaGridBinding
import com.alphaflix.app.models.MediaItem

class MediaGridAdapter(
    private val onItemClick: (MediaItem) -> Unit
) : ListAdapter<MediaItem, MediaGridAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemMediaGridBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMediaGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvTitle.text = item.displayTitle
            tvRating.text = item.displayRating

            Glide.with(ivPoster)
                .load(item.fullPosterUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)
                .into(ivPoster)

            root.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<MediaItem>() {
            override fun areItemsTheSame(a: MediaItem, b: MediaItem) = a.id == b.id
            override fun areContentsTheSame(a: MediaItem, b: MediaItem) = a == b
        }
    }
}
