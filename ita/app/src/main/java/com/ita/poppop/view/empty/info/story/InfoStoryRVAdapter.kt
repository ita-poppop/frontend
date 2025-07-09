package com.ita.poppop.view.empty.info.story

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ita.poppop.databinding.ItemInfoStoryBinding

class InfoStoryRVAdapter: ListAdapter<InfoStoryRVItem, InfoStoryRVAdapter.InfoStoryViewHolder>(
    InfoStoryDiffutillCallback()
) {

    class InfoStoryViewHolder(val binding: ItemInfoStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoStoryRVItem) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .into(ibStoryProfile)
                tvStoryName.text = item.name
            }
        }
    }

    class InfoStoryDiffutillCallback : DiffUtil.ItemCallback<InfoStoryRVItem>() {
        override fun areItemsTheSame(oldItem: InfoStoryRVItem, newItem: InfoStoryRVItem): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: InfoStoryRVItem,
            newItem: InfoStoryRVItem
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoStoryViewHolder {
        val binding = ItemInfoStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InfoStoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InfoStoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

