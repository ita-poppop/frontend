package com.ita.poppop.view.empty.info.detail.recommend

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ita.poppop.R
import com.ita.poppop.databinding.ItemInfoRecommendBinding

class InfoRecommendRVAdapter: ListAdapter<InfoRecommendRVItem, InfoRecommendRVAdapter.InfoRecommendViewHolder>(
    InfoRecommendDiffutillCallback()
)  {

    interface InfoRecommendItemClickListener{
        fun onItemClick(position: Int)
    }
    private lateinit var infoRecommendItemClickListener : InfoRecommendItemClickListener

    fun setInfoRecommendItemClickListener(itemClickListener: InfoRecommendItemClickListener){
        infoRecommendItemClickListener = itemClickListener
    }

    class InfoRecommendViewHolder(val binding: ItemInfoRecommendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoRecommendRVItem) {
            binding.apply {
                Glide.with(ivInfoRecommend.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.app_logo)
                    .centerCrop()
                    .into(ivInfoRecommend)
                tvInfoRecommendTitle.text = item.title
                tvRecommendLocation.text = item.location
            }
        }
        fun bindClickListeners(
            onItemClick: (Int) -> Unit
        ) {
            binding.clInfoRecommend.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }

    class InfoRecommendDiffutillCallback : DiffUtil.ItemCallback<InfoRecommendRVItem>() {
        override fun areItemsTheSame(oldItem: InfoRecommendRVItem, newItem: InfoRecommendRVItem): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: InfoRecommendRVItem,
            newItem: InfoRecommendRVItem
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoRecommendViewHolder {
        val binding = ItemInfoRecommendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InfoRecommendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InfoRecommendViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.bindClickListeners(
            onItemClick = { infoRecommendItemClickListener.onItemClick(it) }
        )
    }
}