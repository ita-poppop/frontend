package com.ita.poppop.view.main.home.waiting

import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ita.poppop.R
import com.ita.poppop.data.remote.dto.stories.StoryData
import com.ita.poppop.databinding.ItemHomeWaitingLayoutBinding
import com.ita.poppop.view.main.MainFragmentDirections


class HomeWaitingAdapter(
    private val onclick: (Int) -> Unit,
    private var items : List<StoryData>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        setHasStableIds(true)
    }

    inner class HomeWaitingViewHolder(
        private val binding: ItemHomeWaitingLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : StoryData) {
            binding.mcvStory.setOnClickListener {
                onclick(item.storyId)
            }
            if (item.isRead) {
                binding.mcvStory.strokeWidth = 0
            } else {
                val strokeWidthInPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    3f,  // 원하는 dp
                    binding.root.context.resources.displayMetrics
                ).toInt()

                binding.mcvStory.strokeWidth = strokeWidthInPx
            }



            Glide.with(binding.root)
                .load(item.photoUrl)
                .centerCrop()
                .into(binding.ivWaitingImage)

            binding.tvWaitingCount.text = item.estimatedWaitCount.toString()
            binding.tvWaitingTitle.text = item.popupTitle
            binding.tvWaitingLocation.text = item.popupLocation

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        return HomeWaitingViewHolder(ItemHomeWaitingLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HomeWaitingViewHolder).bind(items[position])

    }

    // 아이템 반환 메서드
    private fun getItem(position: Int): StoryData {
        return items[position]
    }

    // 아이템 개수 반환 메서
    override fun getItemCount(): Int = items.size
//
//    // 아이템 고유 ID 반환 메서드
//    override fun getItemId(position: Int): Long {
//        return if (position in items.indices) {
//            items[position].hashCode().toLong()
//        } else {
//            -1L // 아이디를 찾지 못했을 때 반환되는 기본값
//        }
//
//    }
}