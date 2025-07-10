package com.ita.poppop.view.main.home.upcoming

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ita.poppop.data.remote.dto.popups.PlannedData
import com.ita.poppop.data.remote.dto.popups.TrendData
import com.ita.poppop.databinding.ItemHomeUpcomingLayoutBinding

class HomeUpcomingAdapter(
    private val onClick: (Int) -> Unit,
    private var items : List<PlannedData>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        setHasStableIds(true)
    }

    inner class HomeUpcomingViewHolder(
        private val binding: ItemHomeUpcomingLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : PlannedData) {
            binding.root.setOnClickListener{
                onClick(item.id)
            }
            Glide.with(binding.root)
                .load(item.image)
                .centerCrop()
                .into(binding.ivUpcomingPoster)

            binding.tvUpcomingTitle.text = item.title
            binding.tvUpcomingLocation.text = item.location

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        return HomeUpcomingViewHolder(ItemHomeUpcomingLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HomeUpcomingViewHolder).bind(items[position])
    }


    // 아이템 반환 메서드
    private fun getItem(position: Int): PlannedData {
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