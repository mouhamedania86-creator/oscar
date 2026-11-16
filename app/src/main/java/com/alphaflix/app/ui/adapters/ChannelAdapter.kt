package com.alphaflix.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.alphaflix.app.R
import com.alphaflix.app.databinding.ItemChannelBinding
import com.alphaflix.app.models.Channel

class ChannelAdapter(
    private val onItemClick: (Channel) -> Unit
) : ListAdapter<Channel, ChannelAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemChannelBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemChannelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val channel = getItem(position)
        with(holder.binding) {
            tvChannelName.text = channel.displayName
            tvCategory.text = channel.category ?: ""

            Glide.with(ivChannelLogo)
                .load(channel.fullLogoUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_channel)
                .error(R.drawable.placeholder_channel)
                .into(ivChannelLogo)

            root.setOnClickListener { onItemClick(channel) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Channel>() {
            override fun areItemsTheSame(a: Channel, b: Channel) = a.id == b.id
            override fun areContentsTheSame(a: Channel, b: Channel) = a == b
        }
    }
}
