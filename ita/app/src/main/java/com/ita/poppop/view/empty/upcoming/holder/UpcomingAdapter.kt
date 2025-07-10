package com.ita.poppop.view.empty.upcoming.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ita.poppop.data.remote.dto.popups.PlannedData
import com.ita.poppop.databinding.ItemUpcomingLayoutBinding


class UpcomingAdapter(
    private val onClick: (Int) -> Unit,
    private var items : List<PlannedData>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        setHasStableIds(true)
    }

    inner class UpcomingViewHolder(
        private val binding: ItemUpcomingLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlannedData) {
            binding.root.setOnClickListener{
                onClick(item.id)
            }
            Glide.with(binding.root)
                .load(item.image)
                .centerCrop()
                .into(binding.ivUpcomingPoster)

            binding.tvUpcomingTitle.text = item.title
            binding.tvUpcomingLocation.text = item.location
            binding.tvUpcomingDday.text = if (item.dday == "0") {
                "D-Day"
            } else {
                "D-${item.dday}"
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        return UpcomingViewHolder(ItemUpcomingLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UpcomingViewHolder).bind(items[position])
    }


    // 아이템 반환 메서드
    private fun getItem(position: Int): PlannedData {
        return items[position]
    }

    // 아이템 개수 반환 메서
    override fun getItemCount(): Int = items.size

}