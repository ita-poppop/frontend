package com.ita.poppop.view.empty.info.review.image

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ita.poppop.databinding.ItemInfoReviewImageBinding

class InfoReviewImageRVAdapter: ListAdapter<InfoReviewImageRVItem, InfoReviewImageRVAdapter.InfoReviewImageViewHolder>(
    InfoReviewImageDiffutillCallback()
)  {

    interface InfoReviewImageItemClickListener{
        fun onItemClick(position: Int)
    }
    private lateinit var infoReviewImageItemClickListener : InfoReviewImageItemClickListener

    fun setInfoReviewImageItemClickListener(itemClickListener: InfoReviewImageItemClickListener){
        infoReviewImageItemClickListener = itemClickListener
    }

    class InfoReviewImageViewHolder(val binding: ItemInfoReviewImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoReviewImageRVItem) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .into(ivReviewImage)

            }
        }
    }

    class InfoReviewImageDiffutillCallback : DiffUtil.ItemCallback<InfoReviewImageRVItem>() {
        override fun areItemsTheSame(oldItem: InfoReviewImageRVItem, newItem: InfoReviewImageRVItem): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: InfoReviewImageRVItem,
            newItem: InfoReviewImageRVItem
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoReviewImageViewHolder {
        val binding = ItemInfoReviewImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InfoReviewImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InfoReviewImageViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            if(::infoReviewImageItemClickListener.isInitialized) {
                infoReviewImageItemClickListener.onItemClick(position)
            }
        }
    }
}