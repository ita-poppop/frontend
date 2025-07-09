package com.ita.poppop.view.main.map

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ita.poppop.databinding.ItemMapBinding

class MapRVAdapter: ListAdapter<MapRVItem, MapRVAdapter.MapViewHolder>(MapDiffutillCallback()) {

    interface MapItemClickListener{
        fun onItemClick(position: Int)
    }
    private lateinit var mapItemClickListener : MapItemClickListener

    fun setMapItemClickListener(itemClickListener: MapItemClickListener){
        mapItemClickListener = itemClickListener
    }

    class MapViewHolder(val binding: ItemMapBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MapRVItem) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .into(ivMap)
                tvMapTitle.text = item.title
                tvMapPeriod.text = item.period
            }
        }
        fun bindClickListeners(
            onItemClick: (Int) -> Unit
        ) {
            binding.cvItemMap.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }

    class MapDiffutillCallback: DiffUtil.ItemCallback<MapRVItem>() {
        override fun areItemsTheSame(oldItem: MapRVItem, newItem: MapRVItem): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: MapRVItem,
            newItem: MapRVItem
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
        val binding = ItemMapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MapViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.bindClickListeners(
            onItemClick = { mapItemClickListener.onItemClick(it) }
        )
    }
}