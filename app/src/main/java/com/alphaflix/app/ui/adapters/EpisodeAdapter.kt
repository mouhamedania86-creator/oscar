package com.alphaflix.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.alphaflix.app.R
import com.alphaflix.app.databinding.ItemEpisodeBinding
import com.alphaflix.app.models.Episode

class EpisodeAdapter(
    private val onItemClick: (Episode) -> Unit
) : ListAdapter<Episode, EpisodeAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemEpisodeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemEpisodeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ep = getItem(position)
        with(holder.binding) {
            tvEpisodeNumber.text = "الحلقة ${ep.episodeNumber}"
            tvEpisodeTitle.text = ep.displayTitle
            tvDuration.text = ep.duration?.let { "${it}د" } ?: ""

            Glide.with(ivThumbnail)
                .load(ep.fullThumbnailUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_poster)
                .into(ivThumbnail)

            root.setOnClickListener { onItemClick(ep) }
            btnPlay.setOnClickListener { onItemClick(ep) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Episode>() {
            override fun areItemsTheSame(a: Episode, b: Episode) = a.id == b.id
            override fun areContentsTheSame(a: Episode, b: Episode) = a == b
        }
    }
}
