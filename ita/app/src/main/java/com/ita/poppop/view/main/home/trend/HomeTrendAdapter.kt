package com.ita.poppop.view.main.home.trend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ita.poppop.data.remote.dto.popups.TrendData
import com.ita.poppop.databinding.ItemHomeTrendLayoutBinding


class HomeTrendAdapter(
    private val onClick: (Int) -> Unit,
    private var items :  List<TrendData>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        setHasStableIds(true)
    }

    inner class HomeTrendViewHolder(
        private val binding: ItemHomeTrendLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : TrendData) {
            binding.root.setOnClickListener{
                onClick(item.id)
            }
            Glide.with(binding.root)
                .load(item.imageUrl)
                .centerCrop()
                .into(binding.ivTrendPoster)

            binding.tvTrendTitle.text = item.title
            binding.tvTrendLocation.text = item.location
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        return HomeTrendViewHolder(ItemHomeTrendLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HomeTrendViewHolder).bind(items[position])
    }


    // 아이템 반환 메서드
    private fun getItem(position: Int): TrendData {
        return items[position]
    }

    // 아이템 개수 반환 메서
    override fun getItemCount(): Int = items.size

    // 아이템 고유 ID 반환 메서드
    override fun getItemId(position: Int): Long {
        return if (position in items.indices) {
            items[position].hashCode().toLong()
        } else {
            -1L // 아이디를 찾지 못했을 때 반환되는 기본값
        }

    }
}