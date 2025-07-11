package com.ita.poppop.view.empty.info.story

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ita.poppop.R
import com.ita.poppop.databinding.ItemInfoStoryBinding

class InfoStoryRVAdapter: ListAdapter<InfoStoryRVItem, InfoStoryRVAdapter.InfoStoryViewHolder>(
    InfoStoryDiffutillCallback()
) {

    interface InfoStoryItemClickListener{
        fun onItemClick(position: Int)
    }
    private lateinit var infoStoryItemClickListener : InfoStoryItemClickListener

    fun setInfoStoryItemClickListener(itemClickListener: InfoStoryItemClickListener){
        infoStoryItemClickListener = itemClickListener
    }

    class InfoStoryViewHolder(val binding: ItemInfoStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoStoryRVItem) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(item.profileUrl)
                    .circleCrop()
                    .into(ivStoryProfile)
                tvStoryName.text = item.name.replace("\"", "")

                val strokeColor = if (item.isRead) {
                    android.R.color.transparent  // 읽었으면 투명
                } else {
                    R.color.primary_blue  // 안읽었으면 파랑
                }
                val usernameColor = if (item.isRead) {
                    R.color.gray_900
                } else {
                    R.color.primary_blue
                }
                mcvStoryProfile.strokeColor = ContextCompat.getColor(itemView.context, strokeColor)
                tvStoryName.setTextColor(ContextCompat.getColor(itemView.context, usernameColor))
            }
        }
        fun bindClickListeners(
            onItemClick: (Int) -> Unit
        ) {
            binding.clItemInfoStory.setOnClickListener {
                Log.d("InfoStoryRVAdapter", "Clicked clItemInfoStory, adapterPosition=$adapterPosition")
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClick(pos)
                }
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
        holder.bindClickListeners(
            onItemClick = { infoStoryItemClickListener.onItemClick(it) }
        )
    }
}

